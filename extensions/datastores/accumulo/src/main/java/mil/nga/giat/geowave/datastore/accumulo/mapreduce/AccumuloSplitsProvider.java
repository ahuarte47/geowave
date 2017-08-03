/*******************************************************************************
 * Copyright (c) 2013-2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License,
 * Version 2.0 which accompanies this distribution and is available at
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 ******************************************************************************/
package mil.nga.giat.geowave.datastore.accumulo.mapreduce;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.admin.Locations;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.TabletId;
import org.apache.accumulo.core.data.impl.KeyExtent;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.giat.geowave.core.index.NumericIndexStrategy;
import mil.nga.giat.geowave.core.index.sfc.data.MultiDimensionalNumericData;
import mil.nga.giat.geowave.core.store.DataStoreOperations;
import mil.nga.giat.geowave.core.store.adapter.AdapterStore;
import mil.nga.giat.geowave.core.store.adapter.DataAdapter;
import mil.nga.giat.geowave.core.store.adapter.statistics.DataStatisticsStore;
import mil.nga.giat.geowave.core.store.adapter.statistics.RowRangeHistogramStatistics;
import mil.nga.giat.geowave.core.store.index.PrimaryIndex;
import mil.nga.giat.geowave.core.store.query.DistributableQuery;
import mil.nga.giat.geowave.core.store.util.DataStoreUtils;
import mil.nga.giat.geowave.datastore.accumulo.AccumuloOperations;
import mil.nga.giat.geowave.datastore.accumulo.util.AccumuloUtils;
import mil.nga.giat.geowave.mapreduce.splits.GeoWaveInputSplit;
import mil.nga.giat.geowave.mapreduce.splits.GeoWaveRowRange;
import mil.nga.giat.geowave.mapreduce.splits.IntermediateSplitInfo;
import mil.nga.giat.geowave.mapreduce.splits.RangeLocationPair;
import mil.nga.giat.geowave.mapreduce.splits.SplitsProvider;

public class AccumuloSplitsProvider extends
		SplitsProvider
{
	private final static Logger LOGGER = LoggerFactory.getLogger(AccumuloSplitsProvider.class);

	@Override
	protected TreeSet<IntermediateSplitInfo> populateIntermediateSplits(
			final TreeSet<IntermediateSplitInfo> splits,
			final DataStoreOperations operations,
			final PrimaryIndex index,
			final List<DataAdapter<Object>> adapters,
			final Map<PrimaryIndex, RowRangeHistogramStatistics<?>> statsCache,
			final AdapterStore adapterStore,
			final DataStatisticsStore statsStore,
			final Integer maxSplits,
			final DistributableQuery query,
			final String[] authorizations )
			throws IOException {

		AccumuloOperations accumuloOperations = null;
		if (operations instanceof AccumuloOperations) {
			accumuloOperations = (AccumuloOperations) operations;
		}
		else {
			LOGGER.error("AccumuloSplitsProvider requires AccumuloOperations object.");
			return splits;
		}

		if ((query != null) && !query.isSupported(index)) {
			return splits;
		}
		Range fullrange;
		try {
			fullrange = unwrapRange(getRangeMax(
					index,
					adapterStore,
					statsStore,
					authorizations));
		}
		catch (final Exception e) {
			fullrange = new Range();
			LOGGER.warn(
					"Cannot ascertain the full range of the data",
					e);
		}

		final String tableName = AccumuloUtils.getQualifiedTableName(
				operations.getTableNameSpace(),
				index.getId().getString());
		final NumericIndexStrategy indexStrategy = index.getIndexStrategy();
		final TreeSet<Range> ranges;
		if (query != null) {
			final List<MultiDimensionalNumericData> indexConstraints = query.getIndexConstraints(indexStrategy);
			if ((maxSplits != null) && (maxSplits > 0)) {
				ranges = AccumuloUtils.byteArrayRangesToAccumuloRanges(DataStoreUtils.constraintsToByteArrayRanges(
						indexConstraints,
						indexStrategy,
						maxSplits));
			}
			else {
				ranges = AccumuloUtils.byteArrayRangesToAccumuloRanges(DataStoreUtils.constraintsToByteArrayRanges(
						indexConstraints,
						indexStrategy,
						-1));
			}
			if (ranges.size() == 1) {
				final Range range = ranges.first();
				if (range.isInfiniteStartKey() || range.isInfiniteStopKey()) {
					ranges.remove(range);
					ranges.add(fullrange.clip(range));
				}
			}
		}
		else {
			ranges = new TreeSet<Range>();
			ranges.add(fullrange);
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Protected range: " + fullrange);
			}
		}
		// get the metadata information for these ranges

		final HashMap<String, String> hostNameCache = getHostNameCache();
		Locations locations;
		try {
			final Connector conn = accumuloOperations.getConnector();
			locations = conn.tableOperations().locate(
					tableName,
					ranges);
		}
		catch (final Exception e) {
			throw new IOException(
					e);
		}
		for (final Entry<TabletId, List<Range>> tabletIdRanges : locations.groupByTablet().entrySet()) {
			final TabletId tabletId = tabletIdRanges.getKey();
			final String tabletServer = locations.getTabletLocation(tabletId);
			final String ipAddress = tabletServer.split(
					":",
					2)[0];

			String location = hostNameCache.get(ipAddress);
			if (location == null) {
				final InetAddress inetAddress = InetAddress.getByName(ipAddress);
				location = inetAddress.getHostName();
				hostNameCache.put(
						ipAddress,
						location);
			}

			final Range tabletRange = tabletId.toRange();
			final Map<PrimaryIndex, List<RangeLocationPair>> splitInfo = new HashMap<PrimaryIndex, List<RangeLocationPair>>();
			final List<RangeLocationPair> rangeList = new ArrayList<RangeLocationPair>();

			for (final Range range : tabletIdRanges.getValue()) {
				final Range clippedRange = tabletRange.clip(range);
				if (!(fullrange.beforeStartKey(clippedRange.getEndKey()) || fullrange.afterEndKey(clippedRange
						.getStartKey()))) {
					final double cardinality = getCardinality(
							getHistStats(
									index,
									adapters,
									adapterStore,
									statsStore,
									statsCache,
									authorizations),
							wrapRange(clippedRange));
					rangeList.add(new AccumuloRangeLocationPair(
							wrapRange(clippedRange),
							location,
							cardinality < 1 ? 1.0 : cardinality));
				}
				else {
					LOGGER.info("Query split outside of range");
				}
				if (LOGGER.isTraceEnabled()) {
					LOGGER.warn("Clipped range: " + rangeList.get(
							rangeList.size() - 1).getRange());
				}
			}
			if (!rangeList.isEmpty()) {
				splitInfo.put(
						index,
						rangeList);
				splits.add(new IntermediateSplitInfo(
						splitInfo,
						this));
			}
		}
		return splits;
	}

	/**
	 * Returns data structure to be filled by binnedRanges Extracted out to
	 * facilitate testing
	 */
	public Map<String, Map<KeyExtent, List<Range>>> getBinnedRangesStructure() {
		final Map<String, Map<KeyExtent, List<Range>>> tserverBinnedRanges = new HashMap<String, Map<KeyExtent, List<Range>>>();
		return tserverBinnedRanges;
	}

	/**
	 * Returns host name cache data structure Extracted out to facilitate
	 * testing
	 */
	public HashMap<String, String> getHostNameCache() {
		final HashMap<String, String> hostNameCache = new HashMap<String, String>();
		return hostNameCache;
	}

	public static GeoWaveRowRange wrapRange(
			final Range range ) {
		return new AccumuloRowRange(
				range);
	}

	public static Range unwrapRange(
			final GeoWaveRowRange range ) {
		if (range instanceof AccumuloRowRange) {
			return ((AccumuloRowRange) range).getRange();
		}
		LOGGER.error("AccumuloSplitsProvider requires use of AccumuloRowRange type.");
		return null;
	}

	@Override
	protected GeoWaveRowRange constructRange(
			final byte[] startKey,
			final boolean isStartKeyInclusive,
			final byte[] endKey,
			final boolean isEndKeyInclusive ) {
		return new AccumuloRowRange(
				new Range(
						new Key(
								new Text(
										startKey)),
						isStartKeyInclusive,
						new Key(
								new Text(
										endKey)),
						isEndKeyInclusive));
	}

	@Override
	protected GeoWaveRowRange defaultConstructRange() {
		return new AccumuloRowRange(
				new Range());
	}

	public static RangeLocationPair defaultConstructRangeLocationPair() {
		return new AccumuloRangeLocationPair();
	}

	@Override
	protected RangeLocationPair constructRangeLocationPair(
			final GeoWaveRowRange range,
			final String location,
			final double cardinality ) {
		return new AccumuloRangeLocationPair(
				range,
				location,
				cardinality);
	}

	@Override
	public GeoWaveInputSplit constructInputSplit(
			final Map<PrimaryIndex, List<RangeLocationPair>> splitInfo,
			final String[] locations ) {
		return new GeoWaveAccumuloInputSplit(
				splitInfo,
				locations);
	}
}

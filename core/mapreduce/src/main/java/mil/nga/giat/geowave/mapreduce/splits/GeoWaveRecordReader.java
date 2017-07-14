package mil.nga.giat.geowave.mapreduce.splits;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import mil.nga.giat.geowave.core.index.ByteArrayId;
import mil.nga.giat.geowave.core.index.ByteArrayRange;
import mil.nga.giat.geowave.core.index.QueryRanges;
import mil.nga.giat.geowave.core.index.SinglePartitionQueryRanges;
import mil.nga.giat.geowave.core.store.CloseableIterator;
import mil.nga.giat.geowave.core.store.CloseableIteratorWrapper;
import mil.nga.giat.geowave.core.store.adapter.AdapterStore;
import mil.nga.giat.geowave.core.store.base.BaseDataStore;
import mil.nga.giat.geowave.core.store.entities.GeoWaveKey;
import mil.nga.giat.geowave.core.store.filter.FilterList;
import mil.nga.giat.geowave.core.store.filter.QueryFilter;
import mil.nga.giat.geowave.core.store.index.PrimaryIndex;
import mil.nga.giat.geowave.core.store.operations.DataStoreOperations;
import mil.nga.giat.geowave.core.store.operations.Reader;
import mil.nga.giat.geowave.core.store.operations.ReaderClosableWrapper;
import mil.nga.giat.geowave.core.store.operations.ReaderParams;
import mil.nga.giat.geowave.core.store.query.DistributableQuery;
import mil.nga.giat.geowave.core.store.query.QueryOptions;
import mil.nga.giat.geowave.mapreduce.input.GeoWaveInputKey;
import mil.nga.giat.geowave.mapreduce.input.InputFormatIteratorWrapper;

/**
 * This class is used by the GeoWaveInputFormat to read data from a GeoWave data
 * store.
 *
 * @param <T>
 *            The native type for the reader
 */
public class GeoWaveRecordReader<T> extends
		RecordReader<GeoWaveInputKey, T>
{

	protected static class ProgressPerRange
	{
		private final float startProgress;
		private final float deltaProgress;

		public ProgressPerRange(
				final float startProgress,
				final float endProgress ) {
			this.startProgress = startProgress;
			this.deltaProgress = endProgress - startProgress;
		}

		public float getOverallProgress(
				final float rangeProgress ) {
			return startProgress + (rangeProgress * deltaProgress);
		}
	}

	protected static final Logger LOGGER = Logger.getLogger(
			GeoWaveRecordReader.class);
	protected long numKeysRead;
	protected CloseableIterator<?> iterator;
	protected Map<RangeLocationPair, ProgressPerRange> progressPerRange;
	protected GeoWaveInputKey currentGeoWaveKey = null;
	protected RangeLocationPair currentGeoWaveRangeIndexPair = null;
	protected T currentValue = null;
	protected GeoWaveInputSplit split;
	protected DistributableQuery query;
	protected QueryOptions queryOptions;
	protected boolean isOutputWritable;
	protected AdapterStore adapterStore;
	protected BaseDataStore dataStore;
	protected DataStoreOperations operations;

	public GeoWaveRecordReader(
			final DistributableQuery query,
			final QueryOptions queryOptions,
			final boolean isOutputWritable,
			final AdapterStore adapterStore,
			final DataStoreOperations operations ) {
		try {
			System.err.println(
					"record reader " + queryOptions.getAdapterIds(
							adapterStore).size());
			if (!queryOptions.getAdapterIds(
					adapterStore).isEmpty()) {
				System.err.println(
						"record reader name " + queryOptions
								.getAdapterIds(
										adapterStore)
								.get(
										0)
								.getString());
			}
		}
		catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.query = query;
		this.queryOptions = queryOptions;
		this.isOutputWritable = isOutputWritable;
		this.adapterStore = adapterStore;
		this.operations = operations;
	}

	/**
	 * Initialize a scanner over the given input split using this task attempt
	 * configuration.
	 */
	@Override
	public void initialize(
			final InputSplit inSplit,
			final TaskAttemptContext attempt )
			throws IOException {
		split = (GeoWaveInputSplit) inSplit;

		numKeysRead = 0;

		final Map<RangeLocationPair, CloseableIterator<Entry<GeoWaveInputKey, T>>> iteratorsPerRange = new LinkedHashMap<RangeLocationPair, CloseableIterator<Entry<GeoWaveInputKey, T>>>();

		final Set<ByteArrayId> indices = split.getIndexIds();
		BigDecimal sum = BigDecimal.ZERO;

		final Map<RangeLocationPair, BigDecimal> incrementalRangeSums = new LinkedHashMap<RangeLocationPair, BigDecimal>();

		for (final ByteArrayId i : indices) {
			final SplitInfo splitInfo = split.getInfo(
					i);
			List<QueryFilter> queryFilters = null;
			if (query != null) {
				queryFilters = query.createFilters(
						splitInfo.getIndex().getIndexModel());
			}
			for (final RangeLocationPair r : splitInfo.getRangeLocationPairs()) {
				final QueryOptions rangeQueryOptions = new QueryOptions(
						queryOptions);
				rangeQueryOptions.setIndex(
						splitInfo.getIndex());
				iteratorsPerRange.put(
						r,
						queryRange(
								splitInfo.getIndex(),
								r.getRange(),
								queryFilters,
								rangeQueryOptions,
								splitInfo.isMixedVisibility()));
				incrementalRangeSums.put(
						r,
						sum);
				sum = sum.add(
						BigDecimal.valueOf(
								r.getCardinality()));
			}
		}

		// finally we can compute percent progress
		progressPerRange = new LinkedHashMap<RangeLocationPair, ProgressPerRange>();
		RangeLocationPair prevRangeIndex = null;
		float prevProgress = 0f;
		if (sum.compareTo(
				BigDecimal.ZERO) > 0) {
			try {
				for (final Entry<RangeLocationPair, BigDecimal> entry : incrementalRangeSums.entrySet()) {
					final BigDecimal value = entry.getValue();
					final float progress = value.divide(
							sum,
							RoundingMode.HALF_UP).floatValue();
					if (prevRangeIndex != null) {
						progressPerRange.put(
								prevRangeIndex,
								new ProgressPerRange(
										prevProgress,
										progress));
					}
					prevRangeIndex = entry.getKey();
					prevProgress = progress;
				}
				progressPerRange.put(
						prevRangeIndex,
						new ProgressPerRange(
								prevProgress,
								1f));

			}
			catch (final Exception e) {
				LOGGER.warn(
						"Unable to calculate progress",
						e);
			}
		}
		// concatenate iterators
		iterator = new CloseableIteratorWrapper<Entry<GeoWaveInputKey, T>>(
				new Closeable() {
					@Override
					public void close()
							throws IOException {
						for (final CloseableIterator<?> it : iteratorsPerRange.values()) {
							it.close();
						}
					}
				},
				concatenateWithCallback(
						iteratorsPerRange.entrySet().iterator(),
						new NextRangeCallback() {

							@Override
							public void setRange(
									final RangeLocationPair indexPair ) {
								currentGeoWaveRangeIndexPair = indexPair;
							}
						}));

	}

	protected CloseableIterator<Entry<GeoWaveInputKey, T>> queryRange(
			final PrimaryIndex i,
			final GeoWaveRowRange range,
			final List<QueryFilter> queryFilters,
			final QueryOptions rangeQueryOptions,
			final boolean mixedVisibility ) {

		final QueryFilter singleFilter = ((queryFilters == null) || queryFilters.isEmpty()) ? null
				: queryFilters.size() == 1 ? queryFilters.get(
						0)
						: new FilterList<QueryFilter>(
								queryFilters);
		try {
			final Reader reader = operations.createReader(
					new ReaderParams(
							i,
							rangeQueryOptions.getAdapterIds(
									adapterStore),
							rangeQueryOptions.getMaxResolutionSubsamplingPerDimension(),
							rangeQueryOptions.getAggregation(),
							rangeQueryOptions.getFieldIdsAdapterPair(),
							// TODO GEOWAVE-1018 should we always use
							// whole row encoding or check the stats for
							// mixed visibilities, probably check stats,
							// although might be passed through
							// configuration
							mixedVisibility,
							false,
							new QueryRanges(
									Collections.singletonList(
											new SinglePartitionQueryRanges(
													range.getPartitionKey() != null ? new ByteArrayId(
															range.getPartitionKey()) : null,
													Collections.singletonList(
															new ByteArrayRange(
																	new ByteArrayId(
																			range.getStartSortKey()),
																	new ByteArrayId(
																			range.getEndSortKey())))))),
							null,
							queryOptions.getLimit(),
							null,
							null,
							rangeQueryOptions.getAuthorizations()));
			return new CloseableIteratorWrapper(
					new ReaderClosableWrapper(
							reader),
					new InputFormatIteratorWrapper<>(
							reader,
							singleFilter,
							adapterStore,
							i,
							isOutputWritable));
		}
		catch (final IOException e) {
			LOGGER.warn(
					"Unable to get adapter IDs",
					e);
		}
		return new CloseableIterator.Empty();
	}

	@Override
	public void close() {
		if (iterator != null) {
			try {
				iterator.close();
			}
			catch (final IOException e) {
				LOGGER.warn(
						"Unable to close iterator",
						e);
			}
		}
	}

	@Override
	public GeoWaveInputKey getCurrentKey()
			throws IOException,
			InterruptedException {
		return currentGeoWaveKey;
	}

	@Override
	public boolean nextKeyValue()
			throws IOException,
			InterruptedException {
		if (iterator != null) {
			if (iterator.hasNext()) {
				++numKeysRead;
				final Object value = iterator.next();
				if (value instanceof Entry) {
					final Entry<GeoWaveInputKey, T> entry = (Entry<GeoWaveInputKey, T>) value;
					currentGeoWaveKey = entry.getKey();
					currentValue = entry.getValue();
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public T getCurrentValue()
			throws IOException,
			InterruptedException {
		return currentValue;
	}

	protected static interface NextRangeCallback
	{
		public void setRange(
				RangeLocationPair indexPair );
	}

	/**
	 * Mostly guava's concatenate method, but there is a need for a callback
	 * between iterators
	 */
	protected static <T> Iterator<Entry<GeoWaveInputKey, T>> concatenateWithCallback(
			final Iterator<Entry<RangeLocationPair, CloseableIterator<Entry<GeoWaveInputKey, T>>>> inputs,
			final NextRangeCallback nextRangeCallback ) {
		Preconditions.checkNotNull(
				inputs);
		return new Iterator<Entry<GeoWaveInputKey, T>>() {
			Iterator<Entry<GeoWaveInputKey, T>> currentIterator = Iterators.emptyIterator();
			Iterator<Entry<GeoWaveInputKey, T>> removeFrom;

			@Override
			public boolean hasNext() {
				boolean currentHasNext;
				while (!(currentHasNext = Preconditions.checkNotNull(
						currentIterator).hasNext()) && inputs.hasNext()) {
					final Entry<RangeLocationPair, CloseableIterator<Entry<GeoWaveInputKey, T>>> entry = inputs.next();
					nextRangeCallback.setRange(
							entry.getKey());
					currentIterator = entry.getValue();
				}
				return currentHasNext;
			}

			@Override
			public Entry<GeoWaveInputKey, T> next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				removeFrom = currentIterator;
				return currentIterator.next();
			}

			@SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH", justification = "Precondition catches null")
			@Override
			public void remove() {
				Preconditions.checkState(
						removeFrom != null,
						"no calls to next() since last call to remove()");
				removeFrom.remove();
				removeFrom = null;
			}
		};
	}

	private static float getOverallProgress(
			final GeoWaveRowRange range,
			final GeoWaveInputKey currentKey,
			final ProgressPerRange progress ) {
		final float rangeProgress = getProgressForRange(
				range,
				currentKey);
		return progress.getOverallProgress(
				rangeProgress);
	}

	private static float getProgressForRange(
			final byte[] start,
			final byte[] end,
			final byte[] position ) {
		final int maxDepth = Math.min(
				Math.max(
						end.length,
						start.length),
				position.length);
		final BigInteger startBI = new BigInteger(
				SplitsProvider.extractBytes(
						start,
						maxDepth));
		final BigInteger endBI = new BigInteger(
				SplitsProvider.extractBytes(
						end,
						maxDepth));
		final BigInteger positionBI = new BigInteger(
				SplitsProvider.extractBytes(
						position,
						maxDepth));
		return (float) (positionBI.subtract(
				startBI).doubleValue()
				/ endBI.subtract(
						startBI).doubleValue());
	}

	private static float getProgressForRange(
			final GeoWaveRowRange range,
			final GeoWaveInputKey currentKey ) {
		if (currentKey == null) {
			return 0f;
		}
		if ((range.getStartSortKey() != null) && (range.getEndSortKey() != null) && (currentKey.getRow() != null)) {
			//TODO GEOWAVE-1018 this doesn't account for partition keys at all
			// just look at the row progress
			return getProgressForRange(
					range.getStartSortKey(),
					range.getEndSortKey(),
					GeoWaveKey.getCompositeId(
							currentKey.getRow()));

		}
		// if we can't figure it out, then claim no progress
		return 0f;
	}

	@Override
	public float getProgress()
			throws IOException {
		if ((numKeysRead > 0) && (currentGeoWaveKey == null)) {
			return 1.0f;
		}
		if (currentGeoWaveRangeIndexPair == null) {
			return 0.0f;
		}
		final ProgressPerRange progress = progressPerRange.get(
				currentGeoWaveRangeIndexPair);
		if (progress == null) {
			return getProgressForRange(
					currentGeoWaveRangeIndexPair.getRange(),
					currentGeoWaveKey);
		}
		return getOverallProgress(
				currentGeoWaveRangeIndexPair.getRange(),
				currentGeoWaveKey,
				progress);
	}

}
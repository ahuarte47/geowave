/*******************************************************************************
 * Copyright (c) 2013-2018 Contributors to the Eclipse Foundation
 * 
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License,
 * Version 2.0 which accompanies this distribution and is available at
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 ******************************************************************************/

package mil.nga.giat.geowave.adapter.raster.plugin;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.data.DataSourceException;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.parameter.Parameter;
import org.geotools.util.DateRange;
import org.opengis.coverage.grid.Format;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.parameter.GeneralParameterValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.ParameterException;

import mil.nga.giat.geowave.adapter.raster.adapter.RasterDataAdapter;
import mil.nga.giat.geowave.adapter.vector.plugin.GeoWaveGTDataStore;
import mil.nga.giat.geowave.adapter.vector.plugin.GeoWavePluginConfig;
import mil.nga.giat.geowave.adapter.vector.utils.DateUtilities;
import mil.nga.giat.geowave.core.cli.operations.config.options.ConfigOptions;
import mil.nga.giat.geowave.core.geotime.store.query.TemporalRange;
import mil.nga.giat.geowave.core.index.ByteArrayId;
import mil.nga.giat.geowave.core.store.CloseableIterator;
import mil.nga.giat.geowave.core.store.CloseableIterator.Wrapper;
import mil.nga.giat.geowave.core.store.operations.remote.options.DataStorePluginOptions;
import mil.nga.giat.geowave.core.store.operations.remote.options.StoreLoader;

/**
 * the reader gets the connection info and returns a grid coverage for every
 * data adapter with an optional associated vector source that provides
 * its feature attributes. 
 * It is useful to support time series data (e.g. Landsat8 data).
 */
public class GeoWaveExtendedRasterReader extends GeoWaveRasterReader {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GeoWaveExtendedRasterReader.class);
    
    private final static SimpleDateFormat DATE_FORMAT;
    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC")); // we DO work only with UTC times
    }
    
    private static final String DATATYPE_SUFFIX = "_DATATYPE";
    
    private List<String> metadataNames;
    private PropertyDescriptor timeDescriptor;
    private TemporalRange timeRange;
    private SimpleFeatureSource featureSource;
    private FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2();
    
    /**
     * Constructor.
     * @param source The source object.
     * @param hints
     * @throws IOException
     */
    public GeoWaveExtendedRasterReader(final Object source, final Hints hints) throws IOException {
        super(source, hints);
    }
    
    /**
     * Constructor.
     * @param source The source object.
     * @throws IOException
     */
    public GeoWaveExtendedRasterReader(final Object source) throws IOException {
        super(source);
    }
    
    /**
     * Constructor.
     * @param config The source GeoWaveRasterConfig object.
     * @throws DataSourceException
     */
    public GeoWaveExtendedRasterReader(final GeoWaveRasterConfig config) throws DataSourceException {
        super(config);
    }
    
    private void init() {
        if (metadataNames == null) {
            final List<String> metadataNames = new ArrayList<String>();
            final GeoWaveRasterConfig config = this.getConfig();
            
            if (config.isGranuleIndexStoreSet() && config.isGranuleIndexLayerSet()) {
                final String vectorStoreName = config.getGranuleIndexStore();
                final String vectorLayerName = config.getGranuleIndexLayer();
                final StoreLoader vectorStoreLoader = new StoreLoader(vectorStoreName);
                
                // first check to make sure the data store exists
                if (!vectorStoreLoader.loadFromConfig(ConfigOptions.getDefaultPropertyFile())) {
                    throw new ParameterException("Cannot find store name: " + vectorStoreLoader.getStoreName());
                }
                
                // check to make sure the vector layer exists
                final DataStorePluginOptions vectorStoreOptions = vectorStoreLoader.getDataStorePlugin();
                try {
                    GeoWavePluginConfig pluginConfig = new GeoWavePluginConfig(vectorStoreOptions);
                    GeoWaveGTDataStore dataStore = new GeoWaveGTDataStore(pluginConfig);
                    featureSource = dataStore.getFeatureSource(vectorLayerName);
                } 
                catch (Exception e) {
                    throw new ParameterException("Cannot find layer name: " + config.getGranuleIndexLayer(), e);
                }
                if (featureSource == null) {
                    throw new ParameterException("Cannot find layer name: " + config.getGranuleIndexLayer());
                }
                
                // check TIME dimension
                for (PropertyDescriptor descriptor : featureSource.getSchema().getDescriptors()) {
                    if (Date.class.isAssignableFrom(descriptor.getType().getBinding())) {
                        String timeField = descriptor.getName().toString();
                        
                        TemporalRange timeRange = DateUtilities.getTemporalRange(vectorStoreOptions, new ByteArrayId(vectorLayerName), timeField);
                        if (timeRange != null) {
                            metadataNames.add(GridCoverage2DReader.HAS_TIME_DOMAIN);
                            metadataNames.add(GridCoverage2DReader.TIME_DOMAIN);
                            metadataNames.add(GridCoverage2DReader.TIME_DOMAIN_MINIMUM);
                            metadataNames.add(GridCoverage2DReader.TIME_DOMAIN_MAXIMUM);
                            metadataNames.add(GridCoverage2DReader.TIME_DOMAIN + DATATYPE_SUFFIX);
                            this.timeDescriptor = descriptor;
                            this.timeRange = timeRange;
                            break;
                        }
                    }
                }
            }
            this.metadataNames = metadataNames;
        }
    }
    
    @Override
    public Format getFormat() {
        init();
        
        // we could check other dimensions here
        if (timeDescriptor != null) {
            return new GeoWaveGTRasterFormat(Arrays.asList(AbstractGridFormat.TIME));            
        }
        return super.getFormat();
    }
    
    
    @Override
    public String[] getMetadataNames(final String coverageName) {
        init();
        
        if (metadataNames !=null && metadataNames.size() > 0) {
            return metadataNames.toArray(new String[metadataNames.size()]);
        }
        return super.getMetadataNames(coverageName);
    }
    
    @Override
    public String getMetadataValue(final String coverageName, final String name) {
        init();
        
        if (timeDescriptor != null) {
            if (name.equalsIgnoreCase(GridCoverage2DReader.HAS_TIME_DOMAIN)) {
                return Boolean.toString(Boolean.TRUE);
            }
            if (name.equalsIgnoreCase(GridCoverage2DReader.TIME_DOMAIN)) {
                // TODO: 
                // What is the best? Full list of values (It will be a performance bottleneck for many records),
                // or the DateRange from statistics.
                // 
                //final String timeField = timeDescriptor.getName().getLocalPart();
                //
                //final Query dimQuery = new Query(featureSource.getSchema().getName().getLocalPart());
                //dimQuery.setPropertyNames(Arrays.asList(timeField));
                //
                //try {
                //    SimpleFeatureCollection featureCollection = featureSource.getFeatures(dimQuery);
                //    final UniqueVisitor visitor = new UniqueVisitor(timeField);
                //    featureCollection.accepts(visitor, null);
                //    @SuppressWarnings("unchecked")
                //    Set<Date> values = visitor.getUnique();
                //    
                //    StringBuilder sb = new StringBuilder();
                //    for (Date date : values) sb.append(DATE_FORMAT.format(date)).append(',');
                //    return sb.length() > 0 ? sb.toString().substring(0, sb.length() - 1) : "";
                //}
                //catch (IOException e) {
                //    LOGGER.error("Unable to get Date unique values of '" +  timeField + "'", e);
                //    return null;
                //}
                return DATE_FORMAT.format(timeRange.getStartTime()) + "/" + DATE_FORMAT.format(timeRange.getEndTime());
            }
            if (name.equalsIgnoreCase(GridCoverage2DReader.TIME_DOMAIN + DATATYPE_SUFFIX)) {
                return Date.class.getName().toString();
            }
            if (name.equalsIgnoreCase(GridCoverage2DReader.TIME_DOMAIN_MINIMUM)) {
                return DATE_FORMAT.format(timeRange.getStartTime());
            }
            if (name.equalsIgnoreCase(GridCoverage2DReader.TIME_DOMAIN_MAXIMUM)) {
                return DATE_FORMAT.format(timeRange.getEndTime());
            }
        }
        return super.getMetadataValue(coverageName, name);
    }

    @Override
    protected CloseableIterator<GridCoverage> wrap(final CloseableIterator<GridCoverage> iterator, final GeneralParameterValue[] params) {
        init();
        
        if (timeDescriptor != null && params != null) {
            GeneralEnvelope envelope = null;
            Filter timeFilter = null;
            String timeField = timeDescriptor.getName().getLocalPart();
            
            // Request contains a TIME filter criteria?
            for (final GeneralParameterValue generalParameterValue : params) {
                final Parameter<Object> param = (Parameter<Object>) generalParameterValue;
                
                if (param.getDescriptor().getName().getCode().equals(AbstractGridFormat.READ_GRIDGEOMETRY2D.getName().toString())) {
                    final GridGeometry2D gg = (GridGeometry2D) param.getValue();
                    envelope = (GeneralEnvelope)gg.getEnvelope();
                }
                else
                if (param.getDescriptor().getName().getCode().equals(AbstractGridFormat.TIME.getName().toString())) {
                    Object value = param.getValue();
                    
                    if (value instanceof List<?> && ((List<?>)value).size() > 0) {
                        value = ((List<?>)value).get(0);
                        
                        if (value instanceof DateRange) {
                            DateRange dateRange = (DateRange)value;
                            timeFilter = filterFactory.between(filterFactory.property(timeField), 
                                                               filterFactory.literal(DATE_FORMAT.format(dateRange.getMinValue())), 
                                                               filterFactory.literal(DATE_FORMAT.format(dateRange.getMaxValue())));
                        }
                        else
                        if (value instanceof Date) {
                            Date date = (Date)value;
                            timeFilter = filterFactory.equals (filterFactory.property(timeField), filterFactory.literal(DATE_FORMAT.format(date)));
                        }
                    }
                }
            }
            if (timeFilter != null) {
                Map<String, String> featureSet = new HashMap<String, String>();
                
                SimpleFeatureType featureSchema = featureSource.getSchema();
                String geometryField = featureSchema.getGeometryDescriptor().getLocalName();
                
                try {
                    Filter filter = timeFilter;
                    if (envelope != null) {
                        ReferencedEnvelope env = new ReferencedEnvelope(envelope);
                        filter = filterFactory.and(filterFactory.bbox(filterFactory.property(geometryField), env), filter);
                    }
                    
                    Query query = new Query();
                    query.setPropertyNames(Arrays.asList(geometryField, timeField));
                    query.setFilter(filter);
                    SimpleFeatureCollection featureCollection = featureSource.getFeatures(query);
                    
                    try (final SimpleFeatureIterator featureIterator = featureCollection.features()) {
                        while (featureIterator.hasNext()) {
                            final SimpleFeature simpleFeature = featureIterator.next();
                            final String featureId = simpleFeature.getID();
                            featureSet.put(featureId, featureId);
                        }
                    }
                    if (featureSet.size() == 0) {
                        iterator.close();
                        new Wrapper(Collections.emptyIterator());
                    }
                    
                    class WrapIterator implements CloseableIterator<GridCoverage> {
                        private CloseableIterator<GridCoverage> iterator;
                        private Map<String, String> featureSet;
                        private GridCoverage current;
                        
                        public WrapIterator(Map<String, String> featureSet, CloseableIterator<GridCoverage> iterator) {
                            this.featureSet = featureSet;
                            this.iterator = iterator;
                        }
                        
                        @Override
                        public boolean hasNext() {
                            while (iterator.hasNext()) {
                                GridCoverage coverage = iterator.next();
                                
                                // featureSet contains current dataId?
                                final Object dataidObj = ((GridCoverage2D)coverage).getProperty(RasterDataAdapter.TILE_DATAID_KEY);
                                if (dataidObj != null) {
                                    final String key = dataidObj.toString();
                                    
                                    if (featureSet.containsKey(key)) {
                                        current = coverage;
                                        return true;
                                    }
                                }
                                else {
                                    current = coverage;
                                    return true;
                                }
                            }
                            return false;
                        }
                        
                        @Override
                        public GridCoverage next() {
                            return current;
                        }
                        
                        @Override
                        public void close() throws IOException {
                            featureSet.clear();
                            iterator.close();
                        }
                    }
                    return new WrapIterator(featureSet, iterator);
                }
                catch (IOException e) {
                    LOGGER.error("Exception encountered getting feature collection", e);
                }
                featureSet.clear();
            }
        }
        return super.wrap(iterator, params);
    }
}

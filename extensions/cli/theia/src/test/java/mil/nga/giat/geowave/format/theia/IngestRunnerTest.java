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
package mil.nga.giat.geowave.format.theia;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.SystemUtils;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.identity.FeatureIdImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;

import com.beust.jcommander.ParameterException;

import it.geosolutions.jaiext.JAIExt;
import mil.nga.giat.geowave.adapter.raster.FitToIndexGridCoverage;
import mil.nga.giat.geowave.adapter.raster.plugin.gdal.InstallGdal;
import mil.nga.giat.geowave.adapter.vector.query.cql.CQLQuery;
import mil.nga.giat.geowave.adapter.vector.utils.DateUtilities;
import mil.nga.giat.geowave.core.cli.api.OperationParams;
import mil.nga.giat.geowave.core.cli.operations.config.options.ConfigOptions;
import mil.nga.giat.geowave.core.cli.parser.ManualOperationParams;
import mil.nga.giat.geowave.core.store.DataStore;
import mil.nga.giat.geowave.core.store.GeoWaveStoreFinder;
import mil.nga.giat.geowave.core.store.cli.remote.options.DataStorePluginOptions;
import mil.nga.giat.geowave.core.store.cli.remote.options.StoreLoader;
import mil.nga.giat.geowave.core.store.memory.MemoryStoreFactoryFamily;
import mil.nga.giat.geowave.core.store.query.EverythingQuery;
import mil.nga.giat.geowave.core.store.query.QueryOptions;
import mil.nga.giat.geowave.format.theia.IngestRunner;
import mil.nga.giat.geowave.format.theia.TheiaBasicCommandLineOptions;
import mil.nga.giat.geowave.format.theia.TheiaDownloadCommandLineOptions;
import mil.nga.giat.geowave.format.theia.TheiaRasterIngestCommandLineOptions;
import mil.nga.giat.geowave.format.theia.VectorOverrideCommandLineOptions;
import mil.nga.giat.geowave.core.store.CloseableIterator;

public class IngestRunnerTest
{
	@BeforeClass
	public static void setup()
			throws IOException {

		// Skip this test if we're on a Mac
		org.junit.Assume.assumeTrue(isNotMac());

		GeoWaveStoreFinder.getRegisteredStoreFactoryFamilies().put(
				"memory",
				new MemoryStoreFactoryFamily());

		InstallGdal.main(new String[] {
			System.getenv("GDAL_DIR")
		});
	}

	private static boolean isNotMac() {
		return !SystemUtils.IS_OS_MAC;
	}

	@Test
	public void testIngest()
			throws Exception {
		JAIExt.initJAIEXT();

		if (!Tests.authenticationSettingsAreValid()) return;

		TheiaBasicCommandLineOptions analyzeOptions = new TheiaBasicCommandLineOptions();
		analyzeOptions.setWorkspaceDir(Tests.WORKSPACE_DIR);
		analyzeOptions.setLocation("T30TXN");
		analyzeOptions.setStartDate(DateUtilities.parseISO("2018-01-01T00:00:00Z"));
		analyzeOptions.setEndDate(DateUtilities.parseISO("2018-01-02T00:00:00Z"));
		analyzeOptions
				.setCqlFilter("BBOX(shape,-1.8274,42.3253,-1.6256,42.4735) AND location='T30TXN' AND (band='B4' OR band='B8')");

		String[] settings = Tests.authenticationSettings();
		String iden = settings[0];
		String pass = settings[1];

		TheiaDownloadCommandLineOptions downloadOptions = new TheiaDownloadCommandLineOptions();
		downloadOptions.setOverwriteIfExists(false);
		downloadOptions.setUserIdent(iden);
		downloadOptions.setPassword(pass);

		TheiaRasterIngestCommandLineOptions ingestOptions = new TheiaRasterIngestCommandLineOptions();
		ingestOptions.setRetainImages(true);
		ingestOptions.setCreatePyramid(true);
		ingestOptions.setCreateHistogram(true);
		ingestOptions.setScale(100);

		VectorOverrideCommandLineOptions vectorOverrideOptions = new VectorOverrideCommandLineOptions();
		vectorOverrideOptions.setVectorStore("memorystore2");
		vectorOverrideOptions.setVectorIndex("spatialindex,spatempindex");

		IngestRunner runner = new IngestRunner(
				analyzeOptions,
				downloadOptions,
				ingestOptions,
				vectorOverrideOptions,
				Arrays.asList(
						"memorystore",
						"spatialindex"));

		ManualOperationParams params = new ManualOperationParams();
		params.getContext().put(
				ConfigOptions.PROPERTIES_FILE_CONTEXT,
				new File(
						IngestRunnerTest.class.getClassLoader().getResource(
								"geowave-config.properties").toURI()));

		runner.runInternal(params);

		try (CloseableIterator<Object> results = getStore(
				params).query(
				new QueryOptions(),
				new EverythingQuery())) {
			assertTrue(
					"Store is not empty",
					results.hasNext());
		}
		
		final FilterFactory ff = CommonFactoryFinder.getFilterFactory(null);
        Set<FeatureId> featureIds = new HashSet<FeatureId>();
        
        try (CloseableIterator<Object> results = getStore(params).query(new QueryOptions(), new EverythingQuery())) {
            while (results.hasNext()) {
                Object entry = results.next();
                
                GridCoverage2D coverage = null;
                
                if (entry instanceof FitToIndexGridCoverage) {
                    entry = ((FitToIndexGridCoverage) entry).getOriginalCoverage();
                }
                if (entry instanceof GridCoverage2D) {
                    coverage = (GridCoverage2D)entry;
                }
                
                if (coverage != null) {
                    String coverageName = coverage.getName().toString();
                    Map properties = coverage.getProperties();
                    
                    System.out.println("# Coverage: " + coverageName);
                    for (Object key : properties.keySet()) {
                        Object value = properties.get(key);
                        System.out.println(" + key=" + key.toString() + " value=" + (value!=null ? value.toString() : "<null>"));
                    }
                    System.out.println("");
                    
                    if (featureIds.size() == 0) {
                        featureIds.add(new FeatureIdImpl(coverageName));
                    }
                }
            }
        }
                
        final Filter filter = ff.id(featureIds);
        final CQLQuery query = new CQLQuery(null, filter, null);
        
        try (CloseableIterator<Object> results = getStore(params).query(new QueryOptions(), query)) {
            while (results.hasNext()) {
                Object entry = results.next();
                
                
            }
        }
        
		// Not sure what assertions can be made about the indexes.
	}

	private DataStore getStore(
			OperationParams params ) {
		File configFile = (File) params.getContext().get(
				ConfigOptions.PROPERTIES_FILE_CONTEXT);

		StoreLoader inputStoreLoader = new StoreLoader(
				"memorystore");
		if (!inputStoreLoader.loadFromConfig(configFile)) {
			throw new ParameterException(
					"Cannot find store name: " + inputStoreLoader.getStoreName());
		}
		DataStorePluginOptions storeOptions = inputStoreLoader.getDataStorePlugin();
		return storeOptions.createDataStore();
	}
}

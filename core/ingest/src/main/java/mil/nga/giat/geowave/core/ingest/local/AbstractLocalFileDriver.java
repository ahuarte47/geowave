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
package mil.nga.giat.geowave.core.ingest.local;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import mil.nga.giat.geowave.core.ingest.DataAdapterProvider;
import mil.nga.giat.geowave.core.ingest.IngestUtils;
import mil.nga.giat.geowave.core.store.operations.remote.options.IndexPluginOptions;

/**
 * This class can be sub-classed to handle recursing over a local directory
 * structure and passing along the plugin specific handling of any supported
 * file for a discovered plugin.
 * 
 * @param <P>
 *            The type of the plugin this driver supports.
 * @param <R>
 *            The type for intermediate data that can be used throughout the
 *            life of the process and is passed along for each call to process a
 *            file.
 */
abstract public class AbstractLocalFileDriver<P extends LocalPluginBase, R>
{
	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractLocalFileDriver.class);
	protected LocalInputCommandLineOptions localInput;

	public AbstractLocalFileDriver(
			LocalInputCommandLineOptions input ) {
		localInput = input;
	}

	protected boolean checkIndexesAgainstProvider(
			String providerName,
			DataAdapterProvider<?> adapterProvider,
			List<IndexPluginOptions> indexOptions ) {
		boolean valid = true;
		for (IndexPluginOptions option : indexOptions) {
			if (!IngestUtils.isCompatible(
					adapterProvider,
					option)) {
				// HP Fortify "Log Forging" false positive
				// What Fortify considers "user input" comes only
				// from users with OS-level access anyway
				LOGGER.warn("Local file ingest plugin for ingest type '" + providerName
						+ "' does not support dimensionality '" + option.getType() + "'");
				valid = false;
			}
		}
		return valid;
	}
	
	public static void main(String[] args) throws IOException, URISyntaxException{
		
		//AKIAI4JZSZE3JRYAQYQQ
		//GQRna+UV0mDGLB2PPdYnLFNtwk8D8/jaQM5d7Xjp
		
//		Map<String, ?> env = ImmutableMap.<String, Object> builder()
//				.put(com.upplication.s3fs.AmazonS3Factory.ACCESS_KEY, "access key")
//				.put(com.upplication.s3fs.AmazonS3Factory.SECRET_KEY, "secret key").build();
		//Map<String, ?> env = ImmutableMap.<String, Object> builder().build();
		//System.err.println(Files.walk(FileSystems.newFileSystem(new URI("s3://s3.amazonaws.com/"), env, Thread.currentThread().getContextClassLoader()).getPath("/geowave-benchmarks/")).count());
		     URL u = new URL("http://www.yourserver.com:80/abc/demo.htm");
		    System.out.println("The URL is " + u);
		    System.out.println("The file part is " + u.getFile());
		    System.out.println("The path part is " + u.getPath());
	}

	protected void processInput(
			final String inputPath,
			final Map<String, P> localPlugins,
			final R runData )
			throws IOException {
		if (inputPath == null) {
			LOGGER.error("Unable to ingest data, base directory or file input not specified");
			return;
		}
		final File f = new File(
				inputPath);
		if (!f.exists()) {
			LOGGER.error("Input file '" + f.getAbsolutePath() + "' does not exist");
			throw new IllegalArgumentException(
					inputPath + " does not exist");
		}
		final File base = f.isDirectory() ? f : f.getParentFile();

		for (final LocalPluginBase localPlugin : localPlugins.values()) {
			localPlugin.init(base);
		}
		Files.walkFileTree(
				Paths.get(inputPath),
				new LocalPluginFileVisitor<P, R>(
						localPlugins,
						this,
						runData,
						localInput.getExtensions()));
	}

	abstract protected void processFile(
			final URL file,
			String typeName,
			P plugin,
			R runData )
			throws IOException;
}

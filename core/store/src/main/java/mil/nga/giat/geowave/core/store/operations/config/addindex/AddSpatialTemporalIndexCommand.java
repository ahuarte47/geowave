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
package mil.nga.giat.geowave.core.store.operations.config.addindex;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

import mil.nga.giat.geowave.core.cli.annotations.GeowaveOperation;
import mil.nga.giat.geowave.core.cli.annotations.RestParameters;
import mil.nga.giat.geowave.core.cli.api.Command;
import mil.nga.giat.geowave.core.cli.api.DefaultOperation;
import mil.nga.giat.geowave.core.cli.api.OperationParams;
import mil.nga.giat.geowave.core.cli.operations.config.ConfigSection;
import mil.nga.giat.geowave.core.cli.operations.config.options.ConfigOptions;
import mil.nga.giat.geowave.core.store.operations.remote.options.IndexPluginOptions;

@GeowaveOperation(name = "spatial_temporal", parentOperation = AddIndexSection.class, restEnabled = GeowaveOperation.RestEnabledType.POST)
@Parameters(commandDescription = "Configure an index for usage in GeoWave")
public class AddSpatialTemporalIndexCommand extends
		DefaultOperation<Void> implements
		Command
{
	private final static Logger LOGGER = LoggerFactory.getLogger(AddSpatialTemporalIndexCommand.class);

	@Parameter(description = "<name>", required = true)
	@RestParameters(names = {
		"name"
	})
	private List<String> parameters = new ArrayList<String>();

	@Parameter(names = {
		"-d",
		"--default"
	}, description = "Make this the default index creating stores")
	private Boolean makeDefault;

	private String type = "spatial_temporal";

	@ParametersDelegate
	private IndexPluginOptions pluginOptions = new IndexPluginOptions();

	@Override
	public boolean prepare(
			final OperationParams params ) {

		// Load SPI options for the given type into pluginOptions.
		pluginOptions.selectPlugin(type);

		// Successfully prepared.
		return true;
	}

	@Override
	public void execute(
			final OperationParams params ) {
		computeResults(params);
	}

	public IndexPluginOptions getPluginOptions() {
		return pluginOptions;
	}

	public String getPluginName() {
		return parameters.get(0);
	}

	public String getNamespace() {
		return IndexPluginOptions.getIndexNamespace(getPluginName());
	}

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(
			final String indexName ) {
		parameters = new ArrayList<String>();
		parameters.add(indexName);
	}

	public Boolean getMakeDefault() {
		return makeDefault;
	}

	public void setMakeDefault(
			final Boolean makeDefault ) {
		this.makeDefault = makeDefault;
	}

	public String getType() {
		return type;
	}

	public void setType(
			final String type ) {
		this.type = type;
	}

	public void setPluginOptions(
			final IndexPluginOptions pluginOptions ) {
		this.pluginOptions = pluginOptions;
	}

	@Override
	public Void computeResults(
			final OperationParams params ) {

		// Ensure that a name is chosen.
		if (getParameters().size() < 1) {
			System.out.println(getParameters());
			throw new ParameterException(
					"Must specify index name");
		}

		if (getType() == null) {
			throw new ParameterException(
					"No type could be infered");
		}

		final File propFile = (File) params.getContext().get(
				ConfigOptions.PROPERTIES_FILE_CONTEXT);
		final Properties existingProps = ConfigOptions.loadProperties(
				propFile,
				null);

		// Make sure we're not already in the index.
		final IndexPluginOptions existPlugin = new IndexPluginOptions();
		if (existPlugin.load(
				existingProps,
				getNamespace())) {
			throw new ParameterException(
					"That index already exists: " + getPluginName());
		}

		final String namespace = getNamespace();
		getPluginOptions().save(
				existingProps,
				namespace);

		// Make default?
		if (Boolean.TRUE.equals(makeDefault)) {

			existingProps.setProperty(
					IndexPluginOptions.DEFAULT_PROPERTY_NAMESPACE,
					getPluginName());
		}

		// Write properties file
		ConfigOptions.writeProperties(
				propFile,
				existingProps);

		return null;
	}
}
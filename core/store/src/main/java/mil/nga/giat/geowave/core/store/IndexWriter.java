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
package mil.nga.giat.geowave.core.store;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import mil.nga.giat.geowave.core.index.ByteArrayId;
import mil.nga.giat.geowave.core.store.data.VisibilityWriter;
import mil.nga.giat.geowave.core.store.index.PrimaryIndex;

public interface IndexWriter<T> extends
		Closeable
{
	/**
	 * Write the entry using the index writer's configure field visibility
	 * writer.
	 * 
	 * @param writableAdapter
	 * @param entry
	 * @return
	 * @throws IOException
	 */
	public List<ByteArrayId> write(
			final T entry )
			throws IOException;

	public List<ByteArrayId> write(
			final T entry,
			final VisibilityWriter<T> fieldVisibilityWriter )
			throws IOException;

	public PrimaryIndex[] getIndices();

	public void flush();
}

/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open Web Server (SWOWS).

 * SWOWS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * SWOWS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General
 * Public License along with SWOWS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.swows.producer;

import com.hp.hpl.jena.sparql.core.DatasetGraph;

/**
 * The Abstract Class GraphProducerListener is the common
 * ancestor of the listener classes that operate on graphs.
 * Concrete derived classes must implement the
 * {@code notifyGraphCreation} method.
 */
public abstract class GraphProducerListener implements ProducerListener {

	/**
	 * Notify dataset creation calling.
	 *
	 * @param inputDataset the input dataset
	 * @param dataset the created dataset
	 * {@code notifyGraphCreation} with the default graph
	 * of the dataset.
	 * @see org.swows.producer.ProducerListener#notifyDatasetCreation(com.hp.hpl.jena.sparql.core.DatasetGraph, com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public void notifyDatasetCreation(DatasetGraph inputDataset, DatasetGraph dataset) {
		notifyGraphCreation(inputDataset, dataset.getDefaultGraph());
	}

}

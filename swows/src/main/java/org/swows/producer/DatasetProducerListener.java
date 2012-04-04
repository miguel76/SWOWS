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

import org.swows.graph.SingleGraphDataset;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

/**
 * The Abstract Class DatasetProducerListener is the common
 * ancestor of the listener classes that operate on
 * datasets.
 * Concrete derived classes must implement the
 * {@code notifyDatasetCreation} method.
 */
public abstract class DatasetProducerListener implements ProducerListener {

	/**
	 * Notify graph creation calling.
	 *
	 * @param inputDataset the input dataset
	 * @param graph the created graph
	 * {@code notifyDatasetCreation} with a single graph
	 * dataset.
	 * @see org.swows.producer.ProducerListener#notifyGraphCreation(com.hp.hpl.jena.sparql.core.DatasetGraph, com.hp.hpl.jena.graph.Graph)
	 */
	@Override
	public void notifyGraphCreation(DatasetGraph inputDataset, final Graph graph) {
		notifyDatasetCreation( inputDataset, new SingleGraphDataset(graph) );
	}

}

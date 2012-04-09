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
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;

/**
 * The Abstract Class GraphProducer is the common
 * ancestor of all producer classes that return a graph.
 * Derived concrete classes must implement the
 * {@code createGraph} method.
 */
public abstract class GraphProducer implements Producer {

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#createGraph(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public abstract DynamicGraph createGraph(DynamicDataset inputDataset);

	/**
	 * Return a dataset with as default graph the one
	 * returned by {@code createGraph(inputDataset)} and no
	 * named graphs.
	 *
	 * @param inputDataset the input dataset
	 * @return the dataset with the single graph
	 * @see org.swows.producer.Producer#createDataset(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public DynamicDataset createDataset(final DynamicDataset inputDataset) {
		return new SingleGraphDataset(createGraph(inputDataset));
	}

}

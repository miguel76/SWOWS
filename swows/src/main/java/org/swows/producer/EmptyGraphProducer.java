/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open datatafloW System (SWOWS).

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

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

/**
 * The single instance of the Class EmptyGraphProducer
 * produces always an empty graph.
 * It's never used directly in a dataflow so doesn't
 * implement the standard Producer constructor.
 */
public class EmptyGraphProducer extends GraphProducer {

	private static EmptyGraphProducer singleton = null;

	/**
	 * Gets the single instance of EmptyGraphProducer.
	 *
	 * @return single instance of EmptyGraphProducer
	 */
	public static EmptyGraphProducer getInstance() {
		if (singleton == null)
			singleton = new EmptyGraphProducer();
		return singleton;
	}

	private EmptyGraphProducer() {
		super(); // independent from the context
	}

	public EmptyGraphProducer(Graph conf, Node confRoot, final ProducerMap map) {
		// TODO: if we make a factory framework we can have a single instance
		this(); // independent from the context
	}

	/**
	 * Returns the empty graph.
	 *
	 * @param inputDataset the input dataset
	 * @return the empty graph
	 * @see org.swows.producer.GraphProducer#createGraph(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
		return DynamicGraph.emptyGraph;
	}

	/**
	 * Returns always false cause the {@code EmptyGraphProducer}
	 * instance doesn't depend from any other producer.
	 *
	 * @param producer the producer
	 * @return false
	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
	 */
	public boolean dependsFrom(Producer producer) {
		return false;
	}

}

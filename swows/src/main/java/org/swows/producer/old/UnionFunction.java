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
package org.swows.producer.old;

import java.util.Iterator;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.swows.graph.algebra.MultiUnion;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.producer.ProducerMap;

/**
 * The Class UnionFunction calculates the union of a set of
 * graphs.
 */
public class UnionFunction extends GraphSetToGraphFunction {

	/**
	 * Instantiates a new union function.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see RDFProducer
	 */
	public UnionFunction(Graph conf, Node confRoot, ProducerMap map) {
		super(conf, confRoot, map);
	}

	/**
	 * Instantiates a new union function.
	 *
	 * @param prodIter an interator on the input producers
	 */
	public UnionFunction(Iterator<RDFProducer> prodIter) {
		super(prodIter);
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.GraphSetToGraphFunction#exec(java.util.Iterator)
	 */
	@Override
	public DynamicGraph exec(Iterator<DynamicGraph> input) {

		DynamicGraph resultGraph = new DynamicGraphFromGraph( Graph.emptyGraph );

		if (input.hasNext()) {
			resultGraph = input.next();
		}

		if (input.hasNext()) {
			MultiUnion unionGraph = new MultiUnion(new DynamicGraph[] {resultGraph,input.next()} );
			while (input.hasNext()) {
				unionGraph.addGraph(input.next());
			}
			resultGraph = unionGraph;
		}

		return resultGraph;

	}

}

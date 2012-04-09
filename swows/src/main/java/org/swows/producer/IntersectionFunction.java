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

import java.util.Iterator;

import org.swows.graph.events.DynamicGraph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.compose.Intersection;

/**
 * The Class IntersectionFunction calculates the
 * intersection of a set of graphs.
 */
public class IntersectionFunction extends GraphSetToGraphFunction {

/*
	public static Factory<GraphSetToGraphFunction> factory =
		new Factory<GraphSetToGraphFunction>() {
			@Override
			public GraphSetToGraphFunction create() {
				return getInstance();
			}
		};

	private static IntersectionFunction singleton = null;

	private IntersectionFunction() {

	}

	public static IntersectionFunction getInstance() {
		if (singleton == null)
			singleton = new IntersectionFunction();
		return singleton;
	}
*/

	/**
	 * Instantiates a new intersection function.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public IntersectionFunction(Graph conf, Node confRoot, ProducerMap map) {
		super(conf, confRoot, map);
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.GraphSetToGraphFunction#exec(java.util.Iterator)
	 */
	@Override
	public DynamicGraph exec(Iterator<DynamicGraph> input) {

		DynamicGraph resultGraph = null;

		if (input.hasNext()) {
			resultGraph = input.next();
		} else {
			throw new RuntimeException("Intersection of Empty Graph List is Not Allowed");
		}

//		while (input.hasNext()) {
//			resultGraph = new Intersection(resultGraph, input.next());
//		}

		return resultGraph;

	}

}

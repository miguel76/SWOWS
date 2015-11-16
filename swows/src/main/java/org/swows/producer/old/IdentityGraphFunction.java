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


import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.swows.graph.events.DynamicGraph;
import org.swows.producer.ProducerMap;

/**
 * The Class IdentityGraphFunction is the identity function
 * on graphs: returns the same graph given in input.
 */
public class IdentityGraphFunction extends GraphFunction {

	/**
	 * Instantiates a new identity graph function.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see RDFProducer
	 */
	public IdentityGraphFunction(Graph conf, Node confRoot, ProducerMap map) {
		super(conf, confRoot, map);
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.GraphFunction#exec(org.apache.jena.graph.Graph)
	 */
	@Override
	public DynamicGraph exec(DynamicGraph input) {
		return input;
	}

}

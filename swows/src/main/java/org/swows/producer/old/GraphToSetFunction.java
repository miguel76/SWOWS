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
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDFS;
import org.swows.graph.DynamicChangingGraph;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;
import org.swows.producer.ProducerMap;
import org.swows.vocabulary.SWI;

public abstract class GraphToSetFunction extends GraphFunction {

	public GraphToSetFunction(Graph conf, Node confRoot, ProducerMap map) {
		super(conf, confRoot, map);
	}

	public abstract Iterator<Node> createIterator(Graph inputGraph);

	private Graph execWorker(Graph input) {
		Iterator<Node> elements = createIterator(input);
        Graph newGraph = GraphFactory.createGraphMem();
        while (elements.hasNext()) {
        	newGraph.add(
        			new Triple(	SWI.GraphRoot.asNode(), RDFS.member.asNode(), elements.next() ) );
        }
		return newGraph;
	}

	@Override
	public DynamicGraph exec(final DynamicGraph input) {
		final DynamicChangingGraph buildingGraph = new DynamicChangingGraph(execWorker(input));
		input.getEventManager2().register(new Listener() {
			public void notifyUpdate(Graph source, GraphUpdate update) {
				buildingGraph.setBaseGraph(execWorker(input));
			}
		} );
		return buildingGraph;
	}

}

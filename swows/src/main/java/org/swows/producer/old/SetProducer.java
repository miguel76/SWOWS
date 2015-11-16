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

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDFS;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.vocabulary.SWI;

public abstract class SetProducer extends GraphProducer {

	public abstract Iterator<Node> createIterator(DatasetGraph inputDataset);

	@Override
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
		Iterator<Node> elements = createIterator(inputDataset);
        DynamicGraph newGraph = new DynamicGraphFromGraph( GraphFactory.createGraphMem() );
		//System.out.println("Empty graph created");
		//Node root = NodeFactory.createURI("http://www.swows.org/rootedGraphs/instance#root");
		
		//System.out.println("Filling graph...");
        while (elements.hasNext()) {
        	newGraph.add(
        			new Triple(	SWI.GraphRoot.asNode(), RDFS.member.asNode(), elements.next() ) );
        }
		//System.out.println("Graph filled: " + newGraph);
		return newGraph;
	}

}

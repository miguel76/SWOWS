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
package org.swows.graph;

import org.swows.graph.events.DelegatingDynamicGraph;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.graph.events.EventManager;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.SimpleEventManager;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.compose.CompositionBase;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

public class RecursionGraph extends DelegatingDynamicGraph {
	
	EventManager localEventManager = new SimpleEventManager(this);
	
	/**
	 * Instantiates a new building graph.
	 */
	protected RecursionGraph() {
		super();
		baseGraphCopy = new DynamicGraphFromGraph( Graph.emptyGraph );
	}

	/**
	 * Instantiates a new building graph.
	 *
	 * @param initialGraph the initial graph
	 */
	public RecursionGraph(DynamicGraph initialGraph) {
		super();
		baseGraphCopy = initialGraph;
		initialGraph.getEventManager2().register(localEventManager);
	}

	/**
	 * Sets the base graph.
	 *
	 * @param newGraph the new graph
	 * @param sourceGraph the source graph
	 */
	public synchronized void setBaseGraph(DynamicGraph newGraph) {

//		System.out.println(this + ": *** OLD GRAPH *******");
//		ModelFactory.createModelForGraph(baseGraphCopy).write(System.out,"N3");
//		System.out.println();
//		System.out.println(this + ": *** NEW GRAPH *******");
//		ModelFactory.createModelForGraph(newGraph).write(System.out,"N3");
//		System.out.println();

		Graph oldGraph = baseGraphCopy;
		
		baseGraphCopy.getEventManager2().unregister(localEventManager);
		baseGraphCopy = newGraph;
		baseGraphCopy.getEventManager2().register(localEventManager);

		final Graph addedGraph = GraphFactory.createGraphMem();
		GraphUtil.add(
				addedGraph,
				CompositionBase.butNot(
						newGraph.find(Node.ANY, Node.ANY, Node.ANY),
						oldGraph.find(Node.ANY, Node.ANY, Node.ANY)));
		final Graph deletedGraph = GraphFactory.createGraphMem();
		GraphUtil.add(
				deletedGraph,
				CompositionBase.butNot(
						oldGraph.find(Node.ANY, Node.ANY, Node.ANY),
						newGraph.find(Node.ANY, Node.ANY, Node.ANY)));

		if (!addedGraph.isEmpty() || !deletedGraph.isEmpty()) {
			localEventManager.notifyUpdate(new GraphUpdate() {
				@Override
				public Graph getAddedGraph() {
					return addedGraph;
				}
				@Override
				public Graph getDeletedGraph() {
					return deletedGraph;
				}
			});
		}
	}

	/* (non-Javadoc)
	 * @see org.swows.graph.DelegatingGraph#getBaseGraph()
	 */
	@Override
	protected DynamicGraph getBaseGraph() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.swows.graph.DelegatingGraph#getEventManager()
	 */
	@Override
	public EventManager getEventManager2() {
		return localEventManager;
	}

}

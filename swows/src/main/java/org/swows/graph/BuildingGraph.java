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
package org.swows.graph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphEvents;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.compose.Difference;
import com.hp.hpl.jena.graph.impl.SimpleEventManager;

/**
 * The Class BuildingGraph allows to change the base graph
 * producing the events involved in the change.
 * It's used in {@code org.swows.producer.BuildingGraphProducer}.
 */
public class BuildingGraph extends DelegatingGraph {

	/** The local event manager. */
	protected GraphEventManager localEventManager =
		new SimpleEventManager(this);

	/**
	 * Instantiates a new building graph.
	 */
	protected BuildingGraph() {
		super();
	}

	/**
	 * Instantiates a new building graph.
	 *
	 * @param initialGraph the initial graph
	 */
	public BuildingGraph(Graph initialGraph) {
		super();
		baseGraphCopy = initialGraph;
		baseGraphCopy.getEventManager().register(localEventManager);
	}

	/**
	 * Sets the base graph.
	 *
	 * @param newGraph the new graph
	 * @param sourceGraph the source graph
	 */
	public synchronized void setBaseGraph(Graph newGraph, Graph sourceGraph) {

//		System.out.println(this + ": *** OLD GRAPH *******");
//		ModelFactory.createModelForGraph(baseGraphCopy).write(System.out,"N3");
//		System.out.println();
//		System.out.println(this + ": *** NEW GRAPH *******");
//		ModelFactory.createModelForGraph(newGraph).write(System.out,"N3");
//		System.out.println();

		Graph oldGraph = baseGraphCopy;
		
		baseGraphCopy.getEventManager().unregister(localEventManager);
		baseGraphCopy = newGraph;
		baseGraphCopy.getEventManager().register(localEventManager);

		Graph srcGraph = (sourceGraph != null) ? sourceGraph : this;
		Graph addedGraph = new Difference(newGraph, oldGraph);
		Graph deletedGraph = new Difference(oldGraph, newGraph);
		if (!addedGraph.isEmpty() || !deletedGraph.isEmpty()) {
			localEventManager.notifyEvent(srcGraph, GraphEvents.startRead);
			localEventManager.notifyAddIterator(
					srcGraph,
					addedGraph.find(Node.ANY, Node.ANY, Node.ANY));
			localEventManager.notifyDeleteIterator(
					srcGraph,
					deletedGraph.find(Node.ANY, Node.ANY, Node.ANY));
			localEventManager.notifyEvent(srcGraph, GraphEvents.finishRead);
		}
	}

	/* (non-Javadoc)
	 * @see org.swows.graph.DelegatingGraph#getBaseGraph()
	 */
	@Override
	protected Graph getBaseGraph() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.swows.graph.DelegatingGraph#getEventManager()
	 */
	@Override
	public GraphEventManager getEventManager() {
		return localEventManager;
	}


}

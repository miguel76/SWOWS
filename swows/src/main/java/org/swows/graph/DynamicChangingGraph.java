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

import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.graph.events.GraphUpdate;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.compose.Difference;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

/**
 * The Class BuildingGraph allows to change the base graph
 * producing the events involved in the change.
 * It's used in {@code org.swows.producer.BuildingGraphProducer}.
 */
public class DynamicChangingGraph extends DynamicGraphFromGraph {

	/**
	 * Instantiates a new building graph.
	 */
	protected DynamicChangingGraph() {
		super(Graph.emptyGraph);
	}

	/**
	 * Instantiates a new building graph.
	 *
	 * @param initialGraph the initial graph
	 */
	public DynamicChangingGraph(Graph initialGraph) {
		super(initialGraph);
	}

	/**
	 * Sets the base graph.
	 *
	 * @param newGraph the new graph
	 * @param sourceGraph the source graph
	 */
	public synchronized void setBaseGraph(Graph newGraph) {

//		System.out.println(this + ": *** OLD GRAPH *******");
//		ModelFactory.createModelForGraph(baseGraphCopy).write(System.out,"N3");
//		System.out.println();
//		System.out.println(this + ": *** NEW GRAPH *******");
//		ModelFactory.createModelForGraph(newGraph).write(System.out,"N3");
//		System.out.println();

		Graph oldGraph = baseGraph;
		baseGraph = newGraph;

		final Graph addedGraph = GraphFactory.createGraphMem();
		addedGraph.getBulkUpdateHandler().add(new Difference(newGraph, oldGraph));
		final Graph deletedGraph = GraphFactory.createGraphMem();
		deletedGraph.getBulkUpdateHandler().add(new Difference(oldGraph, newGraph));
		if (!addedGraph.isEmpty() || !deletedGraph.isEmpty()) {
			eventManager.notifyUpdate(new GraphUpdate() {
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

}

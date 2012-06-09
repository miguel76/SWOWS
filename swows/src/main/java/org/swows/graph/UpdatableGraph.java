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
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

public class UpdatableGraph extends DelegatingDynamicGraph {
	
	public UpdatableGraph(
			final DynamicGraph baseGraph,
			final DynamicGraph addGraph,
			final DynamicGraph deleteGraph ) {
		baseGraphCopy = new DynamicGraphFromGraph( GraphFactory.createGraphMem() );
		final BulkUpdateHandler bulkUpdateHandler = baseGraphCopy.getBulkUpdateHandler();
		bulkUpdateHandler.add(baseGraph);
		bulkUpdateHandler.add(addGraph);
		bulkUpdateHandler.delete(deleteGraph);
		baseGraph.getEventManager2().register(
				new Listener() {
					@Override
					public void notifyUpdate(Graph source, final GraphUpdate update) {
						baseGraphCopy.getBulkUpdateHandler().add(update.getAddedGraph());
						baseGraphCopy.getBulkUpdateHandler().delete(update.getDeletedGraph());
						getEventManager2().notifyUpdate(new GraphUpdate() {
							@Override
							public Graph getAddedGraph() {
								return update.getAddedGraph();
							}
							@Override
							public Graph getDeletedGraph() {
								return update.getDeletedGraph();
							}
						});
					}
				} );
		addGraph.getEventManager2().register(
				new Listener() {
					@Override
					public void notifyUpdate(Graph source, final GraphUpdate update) {
						baseGraphCopy.getBulkUpdateHandler().add(update.getAddedGraph());
						getEventManager2().notifyUpdate(new GraphUpdate() {
							@Override
							public Graph getAddedGraph() {
								return update.getAddedGraph();
							}
							@Override
							public Graph getDeletedGraph() {
								return Graph.emptyGraph;
							}
						});
					}
				} );
		deleteGraph.getEventManager2().register(
				new Listener() {
					@Override
					public void notifyUpdate(Graph source, final GraphUpdate update) {
						baseGraphCopy.getBulkUpdateHandler().delete(update.getAddedGraph());
						getEventManager2().notifyUpdate(new GraphUpdate() {
							@Override
							public Graph getAddedGraph() {
								return Graph.emptyGraph;
							}
							@Override
							public Graph getDeletedGraph() {
								return update.getAddedGraph();
							}
						});
					}
				} );
	}
	
	@Override
	protected DynamicGraph getBaseGraph() {
		return null; // Not used because baseGraphCopy is set in constructor
	}

}

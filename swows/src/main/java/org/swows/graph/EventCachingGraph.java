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

import java.util.HashSet;
import java.util.Set;

import org.swows.graph.events.DelegatingDynamicGraph;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

/**
 * The Class LoggingGraph allows to log the graph
 * to which is connected and its updates.
 * It's used in {@code org.swows.producer.LoggingGraphProducer}.
 */
public class EventCachingGraph extends DelegatingDynamicGraph {

//	private Listener graphListener;
//	private DynamicGraph connectedGraph;
//	private EventManager eventManager = new SimpleEventManager(this);
	private Set<GraphUpdate> cachedGraphUpdates = null;

	/**
	 * Instantiates a new logging graph.
	 *
	 * @param connectedGraph the connected graph
	 * @param logger the log4j logger
	 * @param initialGraphDebug if true the initial graph will be traced  
	 * @param graphUpdateDebug if true the graph updates will be debugged/traced  
	 */
	public EventCachingGraph(
			final DynamicGraph connectedGraph) {
		super();
//		this.connectedGraph = connectedGraph;
		Graph initialGraph = GraphFactory.createGraphMem();
		initialGraph.getBulkUpdateHandler().add(connectedGraph);
		baseGraphCopy =	new DynamicGraphFromGraph( initialGraph );
//		graphListener =
//				new Listener() {
//					@Override
//					public void notifyUpdate(Graph source, GraphUpdate update) {
//						cachedGraphUpdates.add(update);
//					}
//				};
		connectedGraph.getEventManager2().register(new Listener() {
			@Override
			public void notifyUpdate(Graph source, GraphUpdate update) {
				getCachedGraphUpdates().add(update);
			}
		});
	}

	public synchronized void sendEvents() {
		if (cachedGraphUpdates != null) {
			for (GraphUpdate update : cachedGraphUpdates) {
				BulkUpdateHandler buh = baseGraphCopy.getBulkUpdateHandler();
				buh.delete(update.getDeletedGraph());
				buh.add(update.getAddedGraph());
			}
			cachedGraphUpdates = null;
			((DynamicGraphFromGraph) baseGraphCopy).sendUpdateEvents();
		}
	}
	
	private Set<GraphUpdate> getCachedGraphUpdates() {
		if (cachedGraphUpdates == null)
			cachedGraphUpdates = new HashSet<GraphUpdate>();
		return cachedGraphUpdates;
	}
	
	/* (non-Javadoc)
	 * @see org.swows.graph.DelegatingGraph#getBaseGraph()
	 */
	@Override
	protected DynamicGraph getBaseGraph() {
		return null;
	}

}

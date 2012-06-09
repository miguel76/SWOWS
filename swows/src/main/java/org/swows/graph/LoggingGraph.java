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

import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.swows.graph.events.DelegatingDynamicGraph;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.EventManager;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;
import org.swows.graph.events.SimpleEventManager;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * The Class LoggingGraph allows to log the graph
 * to which is connected and its updates.
 * It's used in {@code org.swows.producer.LoggingGraphProducer}.
 */
public class LoggingGraph extends DelegatingDynamicGraph {

	private Logger logger; 
	private Listener graphListener;
	private EventManager eventManager = new SimpleEventManager(this);

	/**
	 * Instantiates a new logging graph.
	 *
	 * @param connectedGraph the connected graph
	 * @param logger the log4j logger
	 * @param initialGraphDebug if true the initial graph will be traced  
	 * @param graphUpdateDebug if true the graph updates will be debugged/traced  
	 */
	public LoggingGraph(
			final DynamicGraph connectedGraph, final Logger logger,
			boolean initialGraphDebug, boolean graphUpdateDebug) {
		super();
		this.baseGraphCopy = connectedGraph;
		this.logger = logger;
		if (initialGraphDebug) {
			logger.debug("Initial Graph Creation");
			traceGraph("Initial Graph", connectedGraph);
		}
		graphListener =
				new Listener() {

					@Override
					public void notifyUpdate(Graph source, GraphUpdate update) {
						logger.debug("Notifying events");
						traceGraph("Add Events Graph", update.getAddedGraph());
						traceGraph("Delete Events Graph", update.getDeletedGraph());
						eventManager.notifyUpdate(update);
					}
					
				};
		connectedGraph.getEventManager2().register(graphListener);
					
	}

	private void traceGraph(String title, Graph graph) {
		if (logger.isTraceEnabled()) {
			StringWriter sw = new StringWriter();
			sw.write("*** " + title + " ***");
			sw.write(":\n");
			ModelFactory.createModelForGraph(graph).write(sw,"N3");
			sw.write(":\n");
			sw.write("*** End of " + title + "***");
			sw.flush();
			logger.trace(sw.toString());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.swows.graph.DelegatingGraph#getBaseGraph()
	 */
	@Override
	protected DynamicGraph getBaseGraph() {
		return null;
	}

	@Override
	public EventManager getEventManager2() {
		return eventManager;
	}

}

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

import java.io.StringWriter;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphListener;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.SimpleEventManager;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * The Class LoggingGraph allows to log the graph
 * to which is connected and its updates.
 * It's used in {@code org.swows.producer.LoggingGraphProducer}.
 */
public class LoggingGraph extends DelegatingGraph {

	private Logger logger; 
	private GraphListener graphListener;
	private GraphEventManager eventManager = new SimpleEventManager(this);

	/**
	 * Instantiates a new logging graph.
	 *
	 * @param connectedGraph the connected graph
	 * @param logger the log4j logger
	 * @param initialGraphDebug if true the initial graph will be traced  
	 * @param graphUpdateDebug if true the graph updates will be debugged/traced  
	 */
	public LoggingGraph(
			final Graph connectedGraph, final Logger logger,
			boolean initialGraphDebug, boolean graphUpdateDebug) {
		super();
		this.baseGraphCopy = connectedGraph;
		this.logger = logger;
		if (initialGraphDebug) {
			logger.debug("Initial Graph Creation");
			traceGraph("Initial Graph", connectedGraph);
		}
		graphListener =
				new PushGraphListener(connectedGraph, eventManager) {

					protected void notifyEvents() {
						logger.debug("Notifying events");
						traceGraph("Add Events Graph", addEvents);
						traceGraph("Delete Events Graph", deleteEvents);
						super.notifyEvents();
					}

					protected void notifyAdd(Triple t) {
						eventManager.notifyAddTriple(connectedGraph, t);
					}

					protected void notifyDelete(Triple t) {
						eventManager.notifyDeleteTriple(connectedGraph, t);
					}
					
				};
		connectedGraph.getEventManager().register(graphListener);
					
	}

	private void traceGraph(String title, Graph graph) {
		if (logger.isTraceEnabled()) {
			StringWriter sw = new StringWriter();
			sw.write(title);
			sw.write(":\n");
			ModelFactory.createModelForGraph(graph).write(sw,"N3");
			sw.flush();
			logger.trace(sw.toString());
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
	 * @see com.hp.hpl.jena.graph.Graph#getEventManager()
	 */
	@Override
	public GraphEventManager getEventManager() {
		return eventManager;
	}

}

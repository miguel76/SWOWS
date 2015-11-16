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
package org.swows.graph.events;

import org.apache.jena.graph.Capabilities;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphEventManager;
import org.apache.jena.graph.GraphStatisticsHandler;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.TransactionHandler;
import org.apache.jena.graph.Triple;
import org.apache.jena.shared.AddDeniedException;
import org.apache.jena.shared.DeleteDeniedException;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.log4j.Logger;
import org.swows.util.Utils;

public class DynamicGraphFromGraph implements DynamicGraph {
	
	protected Graph baseGraph;
	protected EventManager eventManager;
	private SimpleGraphUpdate currGraphUpdate = null;
	protected Logger logger = Logger.getLogger(this.getClass());
			
	private SimpleGraphUpdate getCurrGraphUpdate() {
		if (currGraphUpdate == null)
			currGraphUpdate = new SimpleGraphUpdate(baseGraph);
		return currGraphUpdate;
	}
	
	public DynamicGraphFromGraph(Graph graph) {
		baseGraph = graph;
		this.eventManager = new SimpleEventManager(this);
		logger.debug("Graph " + Utils.standardStr(this) + " created");
	}

	public DynamicGraphFromGraph(Graph graph, EventManager eventManager) {
		baseGraph = graph;
		this.eventManager = eventManager;
		logger.debug("Graph " + Utils.standardStr(this) + " created");
	}

	public boolean dependsOn(Graph other) {
		return baseGraph.dependsOn(other);
	}

	public TransactionHandler getTransactionHandler() {
		return baseGraph.getTransactionHandler();
	}

	public Capabilities getCapabilities() {
		return baseGraph.getCapabilities();
	}

	public GraphEventManager getEventManager() {
		return baseGraph.getEventManager();
	}

	public GraphStatisticsHandler getStatisticsHandler() {
		return baseGraph.getStatisticsHandler();
	}

	public PrefixMapping getPrefixMapping() {
		return baseGraph.getPrefixMapping();
	}

	public void delete(Triple t) throws DeleteDeniedException {
		getCurrGraphUpdate().putDeletedTriple(t);
		baseGraph.delete(t);
	}

	public ExtendedIterator<Triple> find(Triple m) {
		return baseGraph.find(m);
	}

	public ExtendedIterator<Triple> find(Node s, Node p, Node o) {
		return baseGraph.find(s, p, o);
	}

	public boolean isIsomorphicWith(Graph g) {
		return baseGraph.isIsomorphicWith(g);
	}

	public boolean contains(Node s, Node p, Node o) {
		return baseGraph.contains(s, p, o);
	}

	public boolean contains(Triple t) {
		return baseGraph.contains(t);
	}

	public void close() {
		baseGraph.close();
	}

	public boolean isEmpty() {
		return baseGraph.isEmpty();
	}

	public int size() {
		return baseGraph.size();
	}

	public boolean isClosed() {
		return baseGraph.isClosed();
	}

	public void add(Triple t) throws AddDeniedException {
		getCurrGraphUpdate().putAddedTriple(t);
		baseGraph.add(t);
	}

	public EventManager getEventManager2() {
		return eventManager;
	}
	
	public String toString() {
		return baseGraph.toString();
	}
	
	public synchronized boolean sendUpdateEvents() {
		boolean modified = false;
		if (currGraphUpdate != null && !currGraphUpdate.isEmpty()) {
			logger.debug("sending update events in " + Utils.standardStr(this));
			logger.trace("deleted graph: " + currGraphUpdate.getDeletedGraph());
			logger.trace("added graph: " + currGraphUpdate.getAddedGraph());
			eventManager.notifyUpdate(currGraphUpdate);
			modified = true;
		}
		currGraphUpdate = null;
		return modified;
	}

	public void clear() {
		getCurrGraphUpdate().putDeletedTriples(baseGraph);
		baseGraph.clear();
	}

	public void remove(Node s, Node p, Node o) {
		getCurrGraphUpdate().putDeletedTriples(find(s,p,o));
		baseGraph.remove(s, p, o);
	}
	
}

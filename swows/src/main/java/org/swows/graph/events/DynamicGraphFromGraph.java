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

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.swows.util.Utils;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphStatisticsHandler;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.DeleteDeniedException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class DynamicGraphFromGraph implements DynamicGraph {
	
	protected DelegatingInnerGraph baseGraph;
	protected EventManager eventManager;
	private SimpleGraphUpdate currGraphUpdate = null;
//	private Transaction currTransaction = null;
	protected Logger logger = Logger.getLogger(this.getClass());
	
	private Queue<Transaction> transactionQueue = new ArrayDeque<Transaction>();

//	public synchronized void setCurrentTransaction(Transaction transaction) {
//		currTransaction = transaction;
//	}
			
	public synchronized void addTransaction(Transaction transaction) {
		transactionQueue.add(transaction);
	}
			
	public DynamicGraphFromGraph(Graph graph, Transaction transaction) {
		baseGraph = new DelegatingInnerGraph(graph);
		addTransaction(transaction);
		this.eventManager = new SimpleEventManager(baseGraph);
		logger.debug("Graph " + Utils.standardStr(this) + " created");
	}

	public DynamicGraphFromGraph(Graph graph, Transaction transaction, EventManager eventManager) {
		baseGraph = new DelegatingInnerGraph(graph);
		addTransaction(transaction);
		this.eventManager = eventManager;
		logger.debug("Graph " + Utils.standardStr(this) + " created");
	}
	
	private class DelegatingInnerGraph implements Graph {
		private Graph baseGraph;
		private SimpleGraphUpdate currGraphUpdate;
		private DelegatingInnerGraph(Graph baseGraph) {
			this.baseGraph = baseGraph;
		}

		public SimpleGraphUpdate getCurrGraphUpdate() {
			if (currGraphUpdate == null)
				currGraphUpdate = new SimpleGraphUpdate(getCurrentTransaction(), baseGraph);
			return currGraphUpdate;
		}

		public boolean dependsOn(Graph other) {
			return baseGraph.dependsOn(other);
		}

		public TransactionHandler getTransactionHandler() {
			return baseGraph.getTransactionHandler();
		}

		@Deprecated
		public BulkUpdateHandler getBulkUpdateHandler() {
			return new BulkUpdateHandler() {
				BulkUpdateHandler buh = baseGraph.getBulkUpdateHandler();
						public void removeAll() {
					getCurrGraphUpdate().putDeletedTriples(baseGraph);
					buh.removeAll();
				}
				
						public void remove(Node s, Node p, Node o) {
					getCurrGraphUpdate().putDeletedTriples(baseGraph.find(s, p, o));
					buh.remove(s, p, o);
				}
				
						public void delete(Graph g, boolean withReifications) {
					getCurrGraphUpdate().putDeletedTriples(g);
					buh.delete(g, withReifications);
				}
				
						public void delete(Graph g) {
					getCurrGraphUpdate().putDeletedTriples(g);
					buh.delete(g);
				}
				
						public void delete(Iterator<Triple> it) {
					getCurrGraphUpdate().putDeletedTriples(it);
					buh.delete(it);
				}
				
						public void delete(List<Triple> triples) {
					getCurrGraphUpdate().putDeletedTriples(triples);
					buh.delete(triples);
				}
				
						public void delete(Triple[] triples) {
					getCurrGraphUpdate().putDeletedTriples(triples);
					buh.delete(triples);
				}
				
						public void add(Graph g, boolean withReifications) {
					getCurrGraphUpdate().putAddedTriples(g);
					buh.add(g, withReifications);
				}
				
						public void add(Graph g) {
					getCurrGraphUpdate().putAddedTriples(g);
					buh.add(g);
				}
				
						public void add(Iterator<Triple> it) {
					getCurrGraphUpdate().putAddedTriples(it);
					buh.add(it);
				}
				
						public void add(List<Triple> triples) {
					getCurrGraphUpdate().putAddedTriples(triples);
					buh.add(triples);
				}
				
						public void add(Triple[] triples) {
					getCurrGraphUpdate().putAddedTriples(triples);
					buh.add(triples);
				}
			};
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

		public ExtendedIterator<Triple> find(TripleMatch m) {
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

		public String toString() {
			return baseGraph.toString();
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

	public synchronized boolean sendUpdateEvents() {
		boolean modified = false;
		if (currGraphUpdate != null && !currGraphUpdate.isEmpty()) {
			logger.debug("sending update events in " + Utils.standardStr(this));
			logger.trace("deleted graph: " + currGraphUpdate.getDeletedGraph());
			logger.trace("added graph: " + currGraphUpdate.getAddedGraph());
			eventManager.notifyUpdate(getCurrentTransaction());
			modified = true;
		}
		currGraphUpdate = null;
		return modified;
	}

	@Override
	public Transaction getCurrentTransaction() {
		return transactionQueue.peek();
	}

	public Transaction endCurrentTransaction() {
		return transactionQueue.poll();
	}

	@Override
	public Graph getCurrentGraph() {
		return baseGraph;
	}

	@Override
	public GraphUpdate getCurrentGraphUpdate() {
		return currGraphUpdate;
	}

	@Override
	public EventManager getEventManager() {
		return eventManager;
	}
	
	protected Graph getBaseGraph() {
		return baseGraph.baseGraph;
	}
	
}

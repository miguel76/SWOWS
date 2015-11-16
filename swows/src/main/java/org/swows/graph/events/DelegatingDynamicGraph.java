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

/**
 * The Class DelegatingGraph is a graph that delegates the
 * execution of each method to a "base graph" (the one
 * returned by the method {@code getBaseGraph}.
 * This method is called just once: the returned graph is
 * cached for later method calls. 
 */
public abstract class DelegatingDynamicGraph implements DynamicGraph {

	/**
	 * Gets the base graph.
	 *
	 * @return the base graph
	 */
	protected abstract DynamicGraph getBaseGraph();

	/** The base graph cached copy. */
	protected DynamicGraph baseGraphCopy = null;

	/**
	 * Gets the local base graph (use cached reference if
	 * available).
	 *
	 * @return the local base graph
	 */
	private DynamicGraph getLocalBaseGraph() {
		if (baseGraphCopy == null)
			baseGraphCopy = getBaseGraph();
		return baseGraphCopy;
	}

	/*
	protected void invalidateLocalCopy() {
		baseGraphCopy = null;
	}
	 */

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#close()
	 */
	public void close() {
		getLocalBaseGraph().close();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#contains(org.apache.jena.graph.Triple)
	 */
	public boolean contains(Triple triple) {
		return getLocalBaseGraph().contains(triple);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#contains(org.apache.jena.graph.Node, org.apache.jena.graph.Node, org.apache.jena.graph.Node)
	 */
	public boolean contains(Node subject, Node predicate, Node object) {
		return getLocalBaseGraph().contains(subject, predicate, object);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#delete(org.apache.jena.graph.Triple)
	 */
	public void delete(Triple triple) throws DeleteDeniedException {
		getLocalBaseGraph().delete(triple);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#dependsOn(org.apache.jena.graph.Graph)
	 */
	public boolean dependsOn(Graph graph) {
		return ( graph == getLocalBaseGraph() || getLocalBaseGraph().dependsOn(graph) );
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#find(org.apache.jena.graph.TripleMatch)
	 */
	public ExtendedIterator<Triple> find(final Triple tripleMatch) {
		return getLocalBaseGraph().find(tripleMatch);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#find(org.apache.jena.graph.Node, org.apache.jena.graph.Node, org.apache.jena.graph.Node)
	 */
	public ExtendedIterator<Triple> find(Node subject, Node predicate, Node object) {
		return getLocalBaseGraph().find(subject, predicate, object);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#getBulkUpdateHandler()
	 */
	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#getCapabilities()
	 */
	public Capabilities getCapabilities() {
		return getLocalBaseGraph().getCapabilities();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#getEventManager()
	 */
	public GraphEventManager getEventManager() {
		return getLocalBaseGraph().getEventManager();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#getPrefixMapping()
	 */
	public PrefixMapping getPrefixMapping() {
		return getLocalBaseGraph().getPrefixMapping();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#getStatisticsHandler()
	 */
	public GraphStatisticsHandler getStatisticsHandler() {
		return getLocalBaseGraph().getStatisticsHandler();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#getTransactionHandler()
	 */
	public TransactionHandler getTransactionHandler() {
		return getLocalBaseGraph().getTransactionHandler();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#isClosed()
	 */
	public boolean isClosed() {
		return getLocalBaseGraph().isClosed();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#isEmpty()
	 */
	public boolean isEmpty() {
		return getLocalBaseGraph().isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#isIsomorphicWith(org.apache.jena.graph.Graph)
	 */
	public boolean isIsomorphicWith(Graph graph) {
		return getLocalBaseGraph().isIsomorphicWith(graph);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.Graph#size()
	 */
	public int size() {
		return getLocalBaseGraph().size();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.graph.GraphAdd#add(org.apache.jena.graph.Triple)
	 */
	public void add(Triple triple) throws AddDeniedException {
		getLocalBaseGraph().add(triple);
	}

	public EventManager getEventManager2() {
		return getLocalBaseGraph().getEventManager2();
	}
	
	public String toString() {
		return getLocalBaseGraph().toString();
	}

	public void clear() {
		getLocalBaseGraph().clear();
	}

	public void remove(Node s, Node p, Node o) {
		getLocalBaseGraph().remove(s, p, o);
	}

}

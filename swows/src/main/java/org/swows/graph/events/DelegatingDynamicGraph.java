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

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphStatisticsHandler;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.query.QueryHandler;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.DeleteDeniedException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

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
	 * @see com.hp.hpl.jena.graph.Graph#close()
	 */
	@Override
	public void close() {
		getLocalBaseGraph().close();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#contains(com.hp.hpl.jena.graph.Triple)
	 */
	@Override
	public boolean contains(Triple triple) {
		return getLocalBaseGraph().contains(triple);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#contains(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node)
	 */
	@Override
	public boolean contains(Node subject, Node predicate, Node object) {
		return getLocalBaseGraph().contains(subject, predicate, object);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#delete(com.hp.hpl.jena.graph.Triple)
	 */
	@Override
	public void delete(Triple triple) throws DeleteDeniedException {
		getLocalBaseGraph().delete(triple);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#dependsOn(com.hp.hpl.jena.graph.Graph)
	 */
	@Override
	public boolean dependsOn(Graph graph) {
		return ( graph == getLocalBaseGraph() || getLocalBaseGraph().dependsOn(graph) );
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#find(com.hp.hpl.jena.graph.TripleMatch)
	 */
	@Override
	public ExtendedIterator<Triple> find(final TripleMatch tripleMatch) {
		return getLocalBaseGraph().find(tripleMatch);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#find(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node)
	 */
	@Override
	public ExtendedIterator<Triple> find(Node subject, Node predicate, Node object) {
		return getLocalBaseGraph().find(subject, predicate, object);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#getBulkUpdateHandler()
	 */
	@Override
	public BulkUpdateHandler getBulkUpdateHandler() {
		return getLocalBaseGraph().getBulkUpdateHandler();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#getCapabilities()
	 */
	@Override
	public Capabilities getCapabilities() {
		return getLocalBaseGraph().getCapabilities();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#getEventManager()
	 */
	@Override
	public GraphEventManager getEventManager() {
		return getLocalBaseGraph().getEventManager();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#getPrefixMapping()
	 */
	@Override
	public PrefixMapping getPrefixMapping() {
		return getLocalBaseGraph().getPrefixMapping();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#getReifier()
	 */
	@Override
	public Reifier getReifier() {
		return getLocalBaseGraph().getReifier();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#getStatisticsHandler()
	 */
	@Override
	public GraphStatisticsHandler getStatisticsHandler() {
		return getLocalBaseGraph().getStatisticsHandler();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#getTransactionHandler()
	 */
	@Override
	public TransactionHandler getTransactionHandler() {
		return getLocalBaseGraph().getTransactionHandler();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#isClosed()
	 */
	@Override
	public boolean isClosed() {
		return getLocalBaseGraph().isClosed();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getLocalBaseGraph().isEmpty();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#isIsomorphicWith(com.hp.hpl.jena.graph.Graph)
	 */
	@Override
	public boolean isIsomorphicWith(Graph graph) {
		return getLocalBaseGraph().isIsomorphicWith(graph);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#queryHandler()
	 */
	@Override
	public QueryHandler queryHandler() {
		return getLocalBaseGraph().queryHandler();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#size()
	 */
	@Override
	public int size() {
		return getLocalBaseGraph().size();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.GraphAdd#add(com.hp.hpl.jena.graph.Triple)
	 */
	@Override
	public void add(Triple triple) throws AddDeniedException {
		getLocalBaseGraph().add(triple);
	}

	@Override
	public EventManager getEventManager2() {
		return getLocalBaseGraph().getEventManager2();
	}
	
	@Override
	public String toString() {
		return getLocalBaseGraph().toString();
	}

}

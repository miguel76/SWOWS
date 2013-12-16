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

import com.hp.hpl.jena.graph.Graph;

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

	@Override
	public Transaction getCurrentTransaction() {
		return getLocalBaseGraph().getCurrentTransaction();
	}

	@Override
	public Graph getCurrentGraph() {
		return getLocalBaseGraph().getCurrentGraph();
	}

	@Override
	public GraphUpdate getCurrentGraphUpdate() {
		return getLocalBaseGraph().getCurrentGraphUpdate();
	}

	@Override
	public EventManager getEventManager() {
		return getLocalBaseGraph().getEventManager();
	}

	/*
	protected void invalidateLocalCopy() {
		baseGraphCopy = null;
	}
	 */


}

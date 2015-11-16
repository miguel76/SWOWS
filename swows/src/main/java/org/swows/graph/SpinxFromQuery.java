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

import org.apache.jena.graph.Capabilities;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphEventManager;
import org.apache.jena.graph.GraphStatisticsHandler;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.TransactionHandler;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.SimpleEventManager;
import org.apache.jena.graph.impl.SimpleTransactionHandler;
import org.apache.jena.query.Query;
import org.apache.jena.shared.AddDeniedException;
import org.apache.jena.shared.DeleteDeniedException;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.util.iterator.ExtendedIterator;

public class SpinxFromQuery implements Graph {
	
//	private Query query;
	private TransactionHandler transactionHandler = new SimpleTransactionHandler();
	private GraphEventManager eventManager = new SimpleEventManager(this);
	private GraphStatisticsHandler graphStatisticsHandler =
			new GraphStatisticsHandler() {
				public long getStatistic(Node S, Node P, Node O) {
					return -1;
				}
			};
	private PrefixMapping prefixMapping = new PrefixMappingImpl();
	private boolean closed = false;
	
	public SpinxFromQuery(Query query) {
//		this.query = query;
	}

	public void add(Triple t) throws AddDeniedException {
		throw new AddDeniedException("Read-Only Query-based Graph");
	}

	public boolean dependsOn(Graph other) {
		return false;
	}

	public TransactionHandler getTransactionHandler() {
		return transactionHandler;
	}

	public Capabilities getCapabilities() {
		return new Capabilities() {
			
			public boolean sizeAccurate() {
				return false;
			}
			
			public boolean iteratorRemoveAllowed() {
				return false;
			}
			
			public boolean handlesLiteralTyping() {
				return true;
			}
			
			public boolean findContractSafe() {
				// ???
				return false;
			}
			
			public boolean deleteAllowed(boolean everyTriple) {
				return false;
			}
			
			public boolean deleteAllowed() {
				return false;
			}
			
			public boolean canBeEmpty() {
				// there should be at least the root xml element
				return false;
			}
			
			public boolean addAllowed(boolean everyTriple) {
				return false;
			}
			
			public boolean addAllowed() {
				return false;
			}
			
		};
	}

	public GraphEventManager getEventManager() {
		// TODO Auto-generated method stub
		return eventManager;
	}

	public GraphStatisticsHandler getStatisticsHandler() {
		return graphStatisticsHandler;
	}

	public PrefixMapping getPrefixMapping() {
		return prefixMapping;
	}

	public void delete(Triple t) throws DeleteDeniedException {
		throw new DeleteDeniedException("Read-Only Query-based Graph");
	}

	public ExtendedIterator<Triple> find(Triple m) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExtendedIterator<Triple> find(Node s, Node p, Node o) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isIsomorphicWith(Graph g) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean contains(Node s, Node p, Node o) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean contains(Triple t) {
		// TODO Auto-generated method stub
		return false;
	}

	public void close() {
		closed = true;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public int size() {
		/* By graph interface contract, implementors are
		 * forced just to give a lower bound, we return
		 * 0 for simplicity. 
		 */
		return 0;
	}

	public boolean isClosed() {
		return closed;
	}

	public void clear() {
		throw new DeleteDeniedException("Read-Only Query-based Graph");
	}

	public void remove(Node s, Node p, Node o) {
		throw new DeleteDeniedException("Read-Only Query-based Graph");
	}

}

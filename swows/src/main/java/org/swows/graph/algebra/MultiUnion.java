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
package org.swows.graph.algebra;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;
import org.swows.graph.events.SimpleGraphUpdate;
import org.swows.graph.events.SimpleListener;
import org.swows.graph.events.Transaction;
import org.swows.util.Utils;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

/**
 * The Class MultiUnion adds to {@code com.hp.hpl.jena.graph.compose.MultiUnion}
 * events management functionality.
 */
public class MultiUnion extends DynamicGraphFromGraph {
	
	private ArrayList<DynamicGraph> inputGraphs = new ArrayList<DynamicGraph>();
	
	// TODO: move to DynamicGraphFromGraph?
	private Queue<Transaction> transactionQueue = new ArrayDeque<Transaction>();

	private void registerListener(final DynamicGraph source) {
		source.getEventManager().register(
   			 new Listener() {
   					
   					SimpleGraphUpdate currGraphUpdate;

   					protected void notifyDelete(Triple triple) {
   				        for (DynamicGraph currGraph: inputGraphs ) {
  							if (currGraph != source && currGraph.getCurrentGraph().contains(triple))
   								return;
   						}
   						currGraphUpdate.putDeletedTriple(triple);
   					}

   					protected void notifyAdd(Triple triple) {
   				        for (DynamicGraph currGraph: inputGraphs ) {
   							if (currGraph != source && currGraph.getCurrentGraph().contains(triple))
   								return;
   						}
   						currGraphUpdate.putAddedTriple(triple);
   					}

   					protected void beginNotify(Transaction transaction) {
   						currGraphUpdate = new SimpleGraphUpdate(transaction);
   					}

   					protected void endNotify(Transaction transaction) {
   						if (currGraphUpdate != null && !currGraphUpdate.isEmpty()) {
   							logger.debug("sending update events in " + Utils.standardStr(this));
   							eventManager.notifyUpdate(transaction);
   						}
   						currGraphUpdate = null;
   					}

   					public synchronized void notifyUpdate(Transaction transaction) {
   						beginNotify(transaction);
   						GraphUpdate update = source.getCurrentGraphUpdate();
   						Iterator<Triple> addedTriples = update.getAddedGraph().find(Node.ANY, Node.ANY, Node.ANY);
   						while( addedTriples.hasNext() )
   							notifyAdd( addedTriples.next() );
   						Iterator<Triple> deletedTriples = update.getDeletedGraph().find(Node.ANY, Node.ANY, Node.ANY);
   						while( deletedTriples.hasNext() )
   							notifyDelete( deletedTriples.next() );
   						endNotify(transaction);
   					}

					@Override
					public void startTransaction(Transaction transaction) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void commit(Transaction transaction) {
						// TODO Auto-generated method stub
						
					}

   				}
   			);
		
	}
	
	private void registerListener() {
        for (DynamicGraph g: inputGraphs ) {
        	registerListener(g);
        }
	}
	
	private com.hp.hpl.jena.graph.compose.MultiUnion getBaseMultiUnion() {
		return (com.hp.hpl.jena.graph.compose.MultiUnion) getBaseGraph();
	}

	/**
	 * Instantiates a new multi union with event management.
	 */
	public MultiUnion(Transaction transaction) {
        super( new com.hp.hpl.jena.graph.compose.MultiUnion(), transaction );
        transactionQueue.add(transaction);
	}

    /**
     * Instantiates a new multi union with event management.
     *
     * @param graphs the input graphs
     */
    public MultiUnion( DynamicGraph[] dynamicGraphs, Transaction transaction) {
        super( new com.hp.hpl.jena.graph.compose.MultiUnion(), transaction );
        for (int dgIndex = 0; dgIndex < dynamicGraphs.length; dgIndex++ )
        	addGraph(dynamicGraphs[dgIndex]);
        transactionQueue.add(transaction);
        registerListener();
    }

    /**
     * Instantiates a new multi union with event management.
     *
     * @param graphs the input graphs
     */
    public MultiUnion( Iterator<DynamicGraph> dynamicGraphs, Transaction transaction ) {
        super( new com.hp.hpl.jena.graph.compose.MultiUnion(), transaction );
        for (; dynamicGraphs.hasNext(); addGraph(dynamicGraphs.next()) );
        transactionQueue.add(transaction);
        registerListener();
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.compose.MultiUnion#addGraph(com.hp.hpl.jena.graph.Graph)
     */
    public void addGraph( DynamicGraph graph ) {
        if (! inputGraphs.contains( graph ) ) {
        	getBaseMultiUnion().addGraph( graph.getCurrentGraph() );
        	inputGraphs.add(graph);
         }
    }

}

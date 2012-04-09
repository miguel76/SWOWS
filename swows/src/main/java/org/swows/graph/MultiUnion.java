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

import java.util.Iterator;
import java.util.List;

import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.graph.events.Listener;
import org.swows.graph.events.SimpleGraphUpdate;
import org.swows.graph.events.SimpleListener;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;

/**
 * The Class MultiUnion adds to {@code com.hp.hpl.jena.graph.compose.MultiUnion}
 * events management functionality.
 */
public class MultiUnion extends DynamicGraphFromGraph {

	private Listener localListener = new SimpleListener() {
		
		SimpleGraphUpdate graphUpdate;

		@Override
		protected void notifyDelete(Graph source, Triple triple) {
			for (Graph currGraph: getSubGraphs()) {
				if (currGraph != source && currGraph.contains(triple))
					return;
			}
			graphUpdate.putDeletedTriple(triple);
		}

		@Override
		protected void notifyAdd(Graph source, Triple triple) {
			for (Graph currGraph: getSubGraphs()) {
				if (currGraph != source && currGraph.contains(triple))
					return;
			}
			graphUpdate.putAddedTriple(triple);
		}

		@Override
		protected void beginNotify(Graph source) {
			graphUpdate = new SimpleGraphUpdate();
		}

		@Override
		protected void endNotify(Graph source) {
			eventManager.notifyUpdate(graphUpdate);
			graphUpdate = null;
		}

	};

	private void registerListener() {
        for (Graph g: getSubGraphs() ) {
        	( (DynamicGraph) g ).getEventManager2().register(localListener);
        }
	}
	
	private com.hp.hpl.jena.graph.compose.MultiUnion getBaseMultiUnion() {
		return (com.hp.hpl.jena.graph.compose.MultiUnion) baseGraph;
	}

	private List<Graph> getSubGraphs() {
		List<Graph> subGraphs = getBaseMultiUnion().getSubGraphs();
		subGraphs.add(getBaseMultiUnion().getBaseGraph());
		return subGraphs;
	}

	/**
	 * Instantiates a new multi union with event management.
	 */
	public MultiUnion() {
		super( new com.hp.hpl.jena.graph.compose.MultiUnion() );
	}

    /**
     * Instantiates a new multi union with event management.
     *
     * @param graphs the input graphs
     */
    public MultiUnion( Graph[] graphs) {
        super( new com.hp.hpl.jena.graph.compose.MultiUnion(graphs) );
        registerListener();
    }

    /**
     * Instantiates a new multi union with event management.
     *
     * @param graphs the input graphs
     */
    public MultiUnion( Iterator<Graph> graphs ) {
        super( new com.hp.hpl.jena.graph.compose.MultiUnion(graphs) );
        registerListener();
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.compose.MultiUnion#addGraph(com.hp.hpl.jena.graph.Graph)
     */
    public void addGraph( DynamicGraph graph ) {
        if (! getSubGraphs().contains( graph ) ) {
        	getBaseMultiUnion().addGraph( graph );
        	graph.getEventManager2().register(localListener);
        }
    }

}

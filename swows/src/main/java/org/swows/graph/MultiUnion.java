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

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;

/**
 * The Class MultiUnion adds to {@code com.hp.hpl.jena.graph.compose.MultiUnion}
 * events management functionality.
 */
public class MultiUnion extends com.hp.hpl.jena.graph.compose.MultiUnion {

	private class GraphListener extends PushGraphListener {

		public GraphListener(Graph sourceGraph) {
			super(sourceGraph, getEventManager());
		}

		@Override
		protected void notifyDelete(Triple t) {
			for (Graph currGraph: m_subGraphs) {
				if (currGraph != sourceGraph && currGraph.contains(t))
					return;
			}
			getEventManager().notifyDeleteTriple(sourceGraph, t);
		}

		@Override
		protected void notifyAdd(Triple t) {
			for (Graph currGraph: m_subGraphs) {
				if (currGraph != sourceGraph && currGraph.contains(t))
					return;
			}
			getEventManager().notifyAddTriple(sourceGraph, t);
		}

	};

	private void registerListeners() {
        for (Graph g: m_subGraphs) {
        	g.getEventManager().register(new GraphListener(g));
        }
	}

	/**
	 * Instantiates a new multi union with event management.
	 */
	public MultiUnion() {
		super();
	}

    /**
     * Instantiates a new multi union with event management.
     *
     * @param graphs the input graphs
     */
    public MultiUnion( Graph[] graphs) {
        super( graphs );
        registerListeners();
    }

    /**
     * Instantiates a new multi union with event management.
     *
     * @param graphs the input graphs
     */
    public MultiUnion( Iterator<Graph> graphs ) {
        super( graphs );
        registerListeners();
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.graph.compose.MultiUnion#addGraph(com.hp.hpl.jena.graph.Graph)
     */
    @Override
    public void addGraph( Graph graph ) {
        if (!m_subGraphs.contains( graph )) {
        	super.addGraph( graph );
        	graph.getEventManager().register(new GraphListener(graph));
        }
    }

}

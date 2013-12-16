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
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

public abstract class DynamicDataset {

    /** Get the default graph as a DynamicGraph */
    public abstract DynamicGraph getDefaultGraph() ;

    /** Get the graph named by graphNode : returns null on no graph 
     * NB Whether a dataset contains a graph if there are no triples is not defined - see the specifc implementation.
     * Some datasets are "open" - they have all graphs even if no triples,
     * */
    public abstract DynamicGraph getGraph(Node graphNode) ;

//    public boolean containsGraph(Node graphNode) ;
//
//    /** Iterate over all names of named graphs */
//    public Iterator<Node> listGraphNodes() ;

    public abstract void setDefaultGraph(DynamicGraph g) ;
    
    public void setDefaultGraph(Graph g) {
    	setDefaultGraph((DynamicGraph) g);
    }

    public abstract void addGraph(Node graphName, DynamicGraph graph) ;
    public void addGraph(Node graphName, Graph graph) {
    	addGraph(graphName,(DynamicGraph) graph);
    }

}

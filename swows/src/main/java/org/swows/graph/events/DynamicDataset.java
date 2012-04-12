package org.swows.graph.events;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

public abstract class DynamicDataset implements DatasetGraph {

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

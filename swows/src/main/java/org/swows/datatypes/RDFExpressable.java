package org.swows.datatypes;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

public interface RDFExpressable {

//	public Graph getGraph();
//	public Node getNode();
//	public void addGraphAround(Graph graph, Node node);
	public Node addRootedGraph(Graph graph);
//	public Object getValue();
	
}

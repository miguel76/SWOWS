package org.swows.producer;

import java.util.Iterator;

import org.swows.vocabulary.Instance;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

public abstract class SetProducer extends GraphProducer {

	public abstract Iterator<Node> createIterator(DatasetGraph inputDataset);

	@Override
	public Graph createGraph(DatasetGraph inputDataset) {
		Iterator<Node> elements = createIterator(inputDataset);
        Graph newGraph = GraphFactory.createGraphMem();
		//System.out.println("Empty graph created");
		//Node root = Node.createURI("http://www.swows.org/rootedGraphs/instance#root");
		
		//System.out.println("Filling graph...");
        while (elements.hasNext()) {
        	newGraph.add(
        			new Triple(	Instance.GraphRoot.asNode(), RDFS.member.asNode(), elements.next() ) );
        }
		//System.out.println("Graph filled: " + newGraph);
		return newGraph;
	}

}

package org.swows.graph;

import java.util.Iterator;

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;

import com.hp.hpl.jena.graph.Node;

public class RecursionDataset extends DynamicDatasetCollection {

	protected DynamicDataset ds;

	public RecursionDataset() {
		ds = new DynamicDatasetMap(new RecursionGraph(DynamicGraph.emptyGraph));
	}

	public RecursionDataset(DynamicDataset dataset) {
		ds = new DynamicDatasetMap(new RecursionGraph(dataset.getDefaultGraph()));
		Iterator<Node> graphNames = dataset.listGraphNodes();
		while (graphNames.hasNext()) {
			Node graphName = graphNames.next();
			ds.addGraph(graphName, new RecursionGraph(dataset.getGraph(graphName)));
		}
	}

	@Override
	public DynamicGraph getDefaultGraph() {
		return ds.getDefaultGraph();
	}

	public RecursionGraph getGraph(Node graphName) {
		if (ds.containsGraph(graphName))
			return (RecursionGraph) ds.getGraph(graphName);
		else {
			RecursionGraph newLocalGraph = new RecursionGraph(DynamicGraph.emptyGraph);
			ds.addGraph(graphName, newLocalGraph);
			return newLocalGraph;
		}
	}

	@Override
	public Iterator<Node> listGraphNodes() {
		return ds.listGraphNodes();
	}

	public void setBaseDataset(DynamicDataset dataset) {
//		System.out.println("New graph: " + dataset.getDefaultGraph());
		((RecursionGraph) ds.getDefaultGraph()).setBaseGraph(dataset.getDefaultGraph());
		Iterator<Node> graphNames = dataset.listGraphNodes();
		while (graphNames.hasNext()) {
			Node graphName = graphNames.next();
			if (ds.containsGraph(graphName))
				((RecursionGraph) ds.getGraph(graphName)).setBaseGraph(dataset.getGraph(graphName));
			else
				ds.addGraph(graphName, new RecursionGraph(dataset.getGraph(graphName)));
		}
	}

}

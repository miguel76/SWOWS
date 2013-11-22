package org.swows.processor;

import com.hp.hpl.jena.graph.Graph;

public class GraphChannelData extends ChannelDataFromGraph {
	
	private Graph graph;

	public GraphChannelData(Graph graph) {
		this.graph = graph;
	}
	
	@Override
	public Graph asGraph() {
		return graph;
	}

}

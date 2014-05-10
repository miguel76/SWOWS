package org.swows.producer;

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.spinx.QueryFactory;
import org.swows.vocabulary.SWI;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;

public class GraphQueryProducer implements QueryProducer {
	
	private Producer graphProducer;
	private Node queryNode;
	
	public GraphQueryProducer(Producer graphProducer, Node queryNode) {
		this.graphProducer = graphProducer;
		this.queryNode = queryNode;
	}

	public GraphQueryProducer(Producer graphProducer) {
		this(graphProducer, null);
	}

	@Override
	public Query createQuery(DynamicDataset inputDataset) {
		if (graphProducer == null)
			return null;
		DynamicGraph graph = graphProducer.createGraph(inputDataset);
//		graph.getPrefixMapping()
		if (graph == null)
			return null;
		return QueryFactory.toQuery(graph, (queryNode == null) ? SWI.GraphRoot.asNode() : queryNode);
	}

}

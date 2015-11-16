package org.swows.producer.old;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.spinx.QueryFactory;
import org.swows.vocabulary.SWI;

public class GraphQueryProducer extends QueryProducer {
	
	private RDFProducer graphProducer;
	private Node queryNode;
	
	public GraphQueryProducer(RDFProducer graphProducer, Node queryNode) {
		this.graphProducer = graphProducer;
		this.queryNode = queryNode;
	}

	public GraphQueryProducer(RDFProducer graphProducer) {
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

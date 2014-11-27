package org.swows.producer;

import org.apache.log4j.Logger;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.spinx.QueryFactory;
import org.swows.transformation.Transformation;
import org.swows.transformation.QueryTransformation;
import org.swows.transformation.UpdateRequestTransformation;
import org.swows.vocabulary.SWI;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.update.UpdateRequest;

public class GraphTransformProducerFromGraph implements TransformationProducer {
	
	private RDFProducer graphProducer;
	private Node rootNode;
    private static final Logger logger = Logger.getLogger(GraphTransformProducerFromGraph.class);
	
	public GraphTransformProducerFromGraph(RDFProducer graphProducer, Node rootNode) {
		this.graphProducer = graphProducer;
		this.rootNode = (rootNode != null) ? rootNode : SWI.GraphRoot.asNode();
	}

	public GraphTransformProducerFromGraph(RDFProducer graphProducer) {
		this(graphProducer, null);
	}

	@Override
	public Transformation createGraphTransform(DynamicDataset inputDataset) {
		logger.debug("In createGraphTransform, rootNode: " + rootNode);
		if (graphProducer == null)
			return null;
		DynamicGraph graph = graphProducer.createGraph(inputDataset);
//		graph.getPrefixMapping()
		if (graph == null)
			return null;
		logger.trace("Config Graph: " + graph);
		Query query = QueryFactory.toQuery(graph, rootNode);
		if (query != null) {
			logger.trace("Query from Config Graph: " + query);
 			return new QueryTransformation(query);
		}
		UpdateRequest updateRequest = QueryFactory.toUpdateRequest(graph, rootNode);
		if (updateRequest != null) {
			logger.trace("Update Request from Config Graph: " + updateRequest);
			return new UpdateRequestTransformation(updateRequest);
		}
		return null;
	}

}

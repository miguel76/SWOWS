package org.swows.transformation;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.update.GraphStore;
import org.apache.jena.update.GraphStoreFactory;
import org.apache.jena.update.Update;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;
import org.topbraid.spin.arq.ARQFactory;
import org.topbraid.spin.model.SPINFactory;
import org.topbraid.spin.vocabulary.SP;

public class UpdateRequestTransformation implements Transformation {
	
	private UpdateRequest updateRequest;
	
	public UpdateRequestTransformation(UpdateRequest updateRequest) {
		this.updateRequest = updateRequest;
	}

	public UpdateRequestTransformation(Update updateOperation) {
		this.updateRequest = new UpdateRequest(updateOperation);
	}
	
	private static UpdateRequest getOperation(Graph configGraph, Node configRoot) {
		return ARQFactory
				.get()
				.createUpdateRequest(
					SPINFactory.asUpdate((Resource)
							ModelFactory
							.createModelForGraph(configGraph)
							.asRDFNode(configRoot)));
	}

	private static TransformationFactory operationFactory =
			new TransformationFactory() {

		@Override
		public Transformation transformationFromGraph(
				Graph configGraph,
				Node configRoot) {
			return new UpdateRequestTransformation(
					getOperation(configGraph, configRoot));
		}
	};

	private static TransformationFactory requestFactory =
			new TransformationFactory() {

		@Override
		public Transformation transformationFromGraph(
				Graph configGraph,
				Node configRoot) {
			Node textNode = GraphUtils.getSingleValueOptProperty(configGraph, configRoot, SP.text.asNode());
			if (textNode != null && textNode.isLiteral())
				return new UpdateRequestTransformation(UpdateFactory.create(textNode.getLiteralLexicalForm()));
			Node opList = GraphUtils.getSingleValueOptProperty(configGraph, configRoot, DF.updateOperations.asNode());
			UpdateRequest updateRequest = new UpdateRequest();
			if (opList != null) {
				RDFList opListRes =
						(RDFList) ModelFactory.createModelForGraph(configGraph).asRDFNode(opList);
				for (RDFNode opNode : opListRes.asJavaList()) {
					UpdateRequest currUpdateRequest = getOperation(configGraph, opNode.asNode());
					for (Update currUpdate : currUpdateRequest.getOperations())
						updateRequest.add(currUpdate);
				}
			}
			return new UpdateRequestTransformation(updateRequest);
		}
	};

	public static TransformationFactory getOperationFactory() {
		return operationFactory;
	}		

	public static TransformationFactory getRequestFactory() {
		return requestFactory;
	}		

	public DatasetGraph apply(DatasetGraph inputDataset) {
		DatasetGraph datasetForUpdate = GraphUtils.cloneDatasetGraph(inputDataset);
		GraphUtils.makeDynamic(datasetForUpdate);
		GraphStore graphStore = GraphStoreFactory.create(datasetForUpdate);
		UpdateProcessor updateProcessor = UpdateExecutionFactory.create(updateRequest, graphStore);
//		do {
//			logger.debug("Single update start in " + hashCode());
		updateProcessor.execute();
//			logger.debug("Single update end in " + hashCode());
//		} while (((DynamicGraphFromGraph) datasetForUpdate.getDefaultGraph()).sendUpdateEvents());
		return graphStore;
	}

}

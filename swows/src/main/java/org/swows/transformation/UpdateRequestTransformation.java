package org.swows.transformation;

import org.swows.source.DatasetSource;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;
import org.topbraid.spin.arq.ARQFactory;
import org.topbraid.spin.model.SPINFactory;
import org.topbraid.spin.vocabulary.SP;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.Update;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

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

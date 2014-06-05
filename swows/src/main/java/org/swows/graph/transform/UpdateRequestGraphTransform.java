package org.swows.graph.transform;

import org.swows.util.GraphUtils;

import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

public class UpdateRequestGraphTransform implements GraphTransform {
	
	private UpdateRequest updateRequest;
	
	public UpdateRequestGraphTransform(UpdateRequest updateRequest) {
		this.updateRequest = updateRequest;
	}

	@Override
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

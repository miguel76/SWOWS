package org.swows.producer;

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.transform.GraphTransform;
import org.swows.graph.transform.QueryGraphTransform;
import org.swows.graph.transform.UpdateRequestGraphTransform;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

public class GraphTransformProducerFromString implements GraphTransformProducer {
	
	private String transform, baseURI;
	
	public GraphTransformProducerFromString(String transform, String baseURI) {
		this.transform = transform;
		this.baseURI = baseURI;
	}

	public GraphTransformProducerFromString(String query) {
		this(query, null);
	}

	@Override
	public GraphTransform createGraphTransform(DynamicDataset inputDataset) {
		if (transform == null)
			return null;
		Query query = QueryFactory.create(transform, baseURI);
		if (query != null)
			return new QueryGraphTransform(query);
		UpdateRequest updateRequest = UpdateFactory.create(transform, baseURI);
		if (updateRequest != null)
			return new UpdateRequestGraphTransform(updateRequest);
		return null;
	}

}

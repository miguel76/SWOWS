package org.swows.producer.old;

import org.swows.graph.events.DynamicDataset;
import org.swows.transformation.Transformation;
import org.swows.transformation.QueryTransformation;
import org.swows.transformation.UpdateRequestTransformation;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

public class GraphTransformProducerFromString implements TransformationProducer {
	
	private static final String MIME_QUERY = "application/sparql-query", MIME_UPDATE_REQUEST = "application/sparql-update";
	
	private String transform, baseURI, mimeType;
	
	public GraphTransformProducerFromString(String transform, String baseURI, String mimeType) {
		this.transform = transform;
		this.baseURI = baseURI;
		this.mimeType = (mimeType != null) ? mimeType : MIME_QUERY;
	}

	public GraphTransformProducerFromString(String query, String mimeType) {
		this(query, null, mimeType);
	}

	public GraphTransformProducerFromString(String query) {
		this(query, null);
	}

	@Override
	public Transformation createGraphTransform(DynamicDataset inputDataset) {
		if (transform == null)
			return null;
		if (mimeType.equals(MIME_QUERY)) {
			Query query = QueryFactory.create(transform, baseURI);
			if (query != null)
				return new QueryTransformation(query);
		}
		if (mimeType.equals(MIME_UPDATE_REQUEST)) {
			UpdateRequest updateRequest = UpdateFactory.create(transform, baseURI);
			if (updateRequest != null)
				return new UpdateRequestTransformation(updateRequest);
		}
		return null;
	}

}

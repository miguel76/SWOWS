package org.swows.producer.old;

import org.apache.jena.query.Query;
import org.swows.graph.events.DynamicDataset;

public class StringQueryProducer extends QueryProducer {
	
	private String query, baseURI;
	
	public StringQueryProducer(String query, String baseURI) {
		this.query = query;
		this.baseURI = baseURI;
	}

	public StringQueryProducer(String query) {
		this(query, null);
	}

	@Override
	public Query createQuery(DynamicDataset inputDataset) {
		if (query != null)
			return org.apache.jena.query.QueryFactory.create(query, baseURI);
		return null;
	}

}

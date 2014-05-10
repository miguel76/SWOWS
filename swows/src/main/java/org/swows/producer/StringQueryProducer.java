package org.swows.producer;

import org.swows.graph.events.DynamicDataset;

import com.hp.hpl.jena.query.Query;

public class StringQueryProducer implements QueryProducer {
	
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
			return com.hp.hpl.jena.query.QueryFactory.create(query, baseURI);
		return null;
	}

}

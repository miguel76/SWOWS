package org.swows.producer.old;

import org.apache.jena.query.Query;
import org.swows.graph.events.DynamicDataset;

public class CoalesceQueryProducer extends QueryProducer {

	private QueryProducer prioritaryProducer;
	private QueryProducer secondaryProducer;
	
	public CoalesceQueryProducer(QueryProducer prioritaryProducer, QueryProducer secondaryProducer) {
		this.prioritaryProducer = prioritaryProducer;
		this.secondaryProducer = secondaryProducer;
	}

	@Override
	public Query createQuery(DynamicDataset inputDataset) {
		Query resultQuery = null;
		if (prioritaryProducer != null)
			resultQuery = prioritaryProducer.createQuery(inputDataset);
		if (resultQuery != null)
			return resultQuery;
		if (secondaryProducer != null)
			return secondaryProducer.createQuery(inputDataset);
		return null;
	}

}

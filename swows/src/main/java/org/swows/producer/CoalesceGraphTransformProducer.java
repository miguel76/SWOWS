package org.swows.producer;

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.transform.GraphTransform;

public class CoalesceGraphTransformProducer implements GraphTransformProducer {

	private GraphTransformProducer prioritaryProducer;
	private GraphTransformProducer secondaryProducer;
	
	public CoalesceGraphTransformProducer(GraphTransformProducer prioritaryProducer, GraphTransformProducer secondaryProducer) {
		this.prioritaryProducer = prioritaryProducer;
		this.secondaryProducer = secondaryProducer;
	}

	@Override
	public GraphTransform createGraphTransform(DynamicDataset inputDataset) {
		GraphTransform resultGraphTransform = null;
		if (prioritaryProducer != null)
			resultGraphTransform = prioritaryProducer.createGraphTransform(inputDataset);
		if (resultGraphTransform != null)
			return resultGraphTransform;
		if (secondaryProducer != null)
			return secondaryProducer.createGraphTransform(inputDataset);
		return null;
	}

}

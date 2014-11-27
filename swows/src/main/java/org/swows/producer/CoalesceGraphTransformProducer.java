package org.swows.producer;

import org.swows.graph.events.DynamicDataset;
import org.swows.transformation.Transformation;

public class CoalesceGraphTransformProducer implements TransformationProducer {

	private TransformationProducer prioritaryProducer;
	private TransformationProducer secondaryProducer;
	
	public CoalesceGraphTransformProducer(TransformationProducer prioritaryProducer, TransformationProducer secondaryProducer) {
		this.prioritaryProducer = prioritaryProducer;
		this.secondaryProducer = secondaryProducer;
	}

	@Override
	public Transformation createGraphTransform(DynamicDataset inputDataset) {
		Transformation resultGraphTransform = null;
		if (prioritaryProducer != null)
			resultGraphTransform = prioritaryProducer.createGraphTransform(inputDataset);
		if (resultGraphTransform != null)
			return resultGraphTransform;
		if (secondaryProducer != null)
			return secondaryProducer.createGraphTransform(inputDataset);
		return null;
	}

}

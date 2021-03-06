package org.swows.producer.old;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.swows.graph.events.DynamicDataset;
import org.swows.producer.Producer;
import org.swows.producer.ProducerMap;
import org.swows.transformation.Transformation;
import org.swows.transformation.TransformationProducerFactory;

public class Transformer extends DatasetProducer {
	
	private RDFProducer inputDatasetProducer;
	private Producer<Transformation> transformationProducer;
	
	public Transformer(
			RDFProducer inputDatasetProducer,
			Producer<Transformation> transformationProducer) {
		this.inputDatasetProducer = inputDatasetProducer;
		this.transformationProducer = transformationProducer;
	}

	public Transformer(Graph conf, Node confRoot, ProducerMap map) {
		this(	new InlineDatasetProducer(conf, confRoot, map),
				TransformationProducerFactory.get().createProducer(conf, confRoot, map));
	}

	@Override
	public boolean dependsFrom(RDFProducer producer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DynamicDataset createDataset(DynamicDataset inputDataset) {
		return transformationProducer
				.create(inputDataset)
				.apply(inputDatasetProducer.createDataset(inputDataset));
	}

}

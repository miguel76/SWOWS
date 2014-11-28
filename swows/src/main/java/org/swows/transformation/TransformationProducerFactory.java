package org.swows.transformation;

import org.swows.graph.events.DynamicDataset;
import org.swows.producer.ConstantProducerFactory;
import org.swows.producer.ProducerFactory;
import org.swows.producer.ProducerMap;
import org.swows.producer.old.Producer;
import org.swows.producer.old.RDFProducer;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

public class TransformationProducerFactory implements ProducerFactory<Transformation> {
	
	@Override
	public Producer<Transformation> createProducer(
			Graph conf, Node confRoot, final ProducerMap map) {
		Node inlineConfNode =
				GraphUtils.getSingleValueOptProperty(conf, confRoot, DF.inlineConfig.asNode());
		if (inlineConfNode != null)
			return (new ConstantProducerFactory<Transformation>())
					.createProducer(TransformationRegistry.get().transformationFromGraph(conf, inlineConfNode));
		Node confNode =
				GraphUtils.getSingleValueProperty(conf, confRoot, DF.config.asNode());
		final Node confRootNode =
				GraphUtils.getSingleValueProperty(conf, confRoot, DF.configRoot.asNode());
		final RDFProducer graphProducer = map.getProducer(confNode);
		return new Producer<Transformation>() {
			@Override
			public Transformation create(DynamicDataset inputDataset) {
				return TransformationRegistry.get().transformationFromGraph(
						graphProducer.createGraph(inputDataset),
						confRootNode) ;
			}
		};
	}
	
	private static TransformationProducerFactory singleton = new TransformationProducerFactory();
	
	public static TransformationProducerFactory get() {
		return singleton;
	}

}

package org.swows.transformation;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.swows.producer.ConstantProducerFactory;
import org.swows.producer.Producer;
import org.swows.producer.ProducerFactory;
import org.swows.producer.ProducerMap;
import org.swows.source.DatasetSource;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;

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
		final Producer<DatasetSource> graphProducer = map.getProducer(confNode);
		return new Producer<Transformation>() {
			@Override
			public Transformation create(DatasetSource source) {
				return TransformationRegistry.get().transformationFromGraph(
						graphProducer.create(source).lastDataset().getDefaultGraph(),
						confRootNode) ;
			}
		};
	}
	
	private static TransformationProducerFactory singleton = new TransformationProducerFactory();
	
	public static TransformationProducerFactory get() {
		return singleton;
	}

}

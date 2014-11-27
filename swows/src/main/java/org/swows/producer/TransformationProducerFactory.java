package org.swows.producer;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

public interface TransformationProducerFactory {

	TransformationProducer createTransformationProducer(
			Graph conf, Node confRoot, ProducerMap map);
	
}

package org.swows.producer;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;

public interface ProducerFactory<T> {

	Producer<T> createProducer(
			Graph conf, Node confRoot, ProducerMap map);
	
}

package org.swows.producer;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

public interface ProducerFactory<T> {

	Producer<T> createProducer(
			Graph conf, Node confRoot, ProducerMap map);
	
}

package org.swows.producer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphUtil;
import org.apache.jena.graph.Node;
import org.apache.jena.vocabulary.RDF;

public class ProducerRegistry<T> {
	
	private Map<Node, ProducerFactory<T>> map =
			new HashMap<Node, ProducerFactory<T>>();
	
	public void register(Node configClass, ProducerFactory<T> factory) {
		map.put(configClass, factory);
	}

	public void unregister(Node configClass, ProducerFactory<T> factory) {
		if (map.get(configClass).equals(factory))
			map.remove(configClass);
	}

	public ProducerRegistry() {
	}
	
	public Producer<T> producerFromGraph(Graph configGraph, Node configRoot, ProducerMap prodMap) {
		final Set<ProducerFactory<T>> factories = new HashSet<ProducerFactory<T>>();
		GraphUtil
				.listObjects(configGraph, configRoot, RDF.type.asNode())
				.filterKeep(new Predicate<Node>() {
					@Override
					public boolean test(Node configClass) {
						ProducerFactory<T> factory = map.get(configClass);
						if (factory != null)
							factories.add(factory);
						return false;
					}
				}).hasNext();
		switch(factories.size()) {
		case 0:
			throw new RuntimeException(
					"No transformation factory found "
					+ "for node " + configRoot
					+ " in graph " + configGraph);
		case 1:
			for (ProducerFactory<T> factory : factories)
				return factory.createProducer(configGraph, configRoot, prodMap);
		default:
			throw new RuntimeException(
					"Too much transformation factories found "
					+ "for node " + configRoot
					+ " in graph " + configGraph);
		}
	}
	
}

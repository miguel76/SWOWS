package org.swows.comp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.swows.producer.Producer;
import org.swows.producer.ProducerFactory;
import org.swows.producer.ProducerMap;
import org.swows.source.DatasetSource;
import org.swows.vocabulary.DF;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphUtil;
import org.apache.jena.graph.Node;
import org.apache.jena.util.iterator.Filter;
import org.apache.jena.vocabulary.RDF;

public class DataflowComponentRegistry {
	
	private Map<Node, ProducerFactory<DatasetSource>> registry =
			new HashMap<Node, ProducerFactory<DatasetSource>>();
	
	public void register(Node configClass, ProducerFactory<DatasetSource> factory) {
		registry.put(configClass, factory);
	}

	public void unregister(Node configClass, ProducerFactory<DatasetSource> factory) {
		if (registry.get(configClass).equals(factory))
			registry.remove(configClass);
	}

	private DataflowComponentRegistry() {
		
		register(DF.Merger.asNode(), Merger.getFactory());
		register(DF.Transformer.asNode(), Transformer.getFactory());
		register(DF.DefaultGraphSelector.asNode(), DefaultGraphSelector.getFactory());
		register(DF.NamedGraphSelector.asNode(), DefaultGraphSelector.getFactory());

	}
	
	public Producer<DatasetSource> createProducer(
			Graph configGraph, Node configRoot, ProducerMap producerMap) {
		final Set<ProducerFactory<DatasetSource>> factories =
				new HashSet<ProducerFactory<DatasetSource>>();
		GraphUtil
				.listObjects(configGraph, configRoot, RDF.type.asNode())
				.filterKeep(new Filter<Node>() {
					@Override
					public boolean accept(Node configClass) {
						ProducerFactory<DatasetSource> factory = registry.get(configClass);
						if (factory != null)
							factories.add(factory);
						return false;
					}
				}).hasNext();
		switch(factories.size()) {
		case 0:
			throw new RuntimeException(
					"No producer factory found "
					+ "for node " + configRoot
					+ " in graph " + configGraph);
		case 1:
			for (ProducerFactory<DatasetSource> factory : factories)
				return factory.createProducer(configGraph, configRoot, producerMap);
		default:
			throw new RuntimeException(
					"Too much producer factories found "
					+ "for node " + configRoot
					+ " in graph " + configGraph);
		}
	}
	
	private static DataflowComponentRegistry singleton;
	
	public static DataflowComponentRegistry get() {
		if (singleton == null)
			singleton = new DataflowComponentRegistry();
		return singleton;
	}
	
}

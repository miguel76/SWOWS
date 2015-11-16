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

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.RDF;

public class DataflowComponentRegistry {
	
	private Map<Node, ProducerFactory<DatasetSource>> map =
			new HashMap<Node, ProducerFactory<DatasetSource>>();
	
	public void register(Node configClass, ProducerFactory<DatasetSource> factory) {
		map.put(configClass, factory);
	}

	public void unregister(Node configClass, ProducerFactory<DatasetSource> factory) {
		if (map.get(configClass).equals(factory))
			map.remove(configClass);
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
						ProducerFactory<DatasetSource> factory = map.get(configClass);
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

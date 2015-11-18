package org.swows.comp;

import java.util.HashMap;
import java.util.Map;

import org.swows.producer.Producer;
import org.swows.producer.ProducerMap;
import org.swows.source.DatasetSource;
import org.swows.transformation.Transformation;
import org.swows.transformation.TransformationFactory;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;

public class StatelessDataflowFactory implements TransformationFactory {

	@Override
	public Transformation transformationFromGraph(
			final Graph configGraph,
			final Node configRoot) {
		final Map<Node,Producer<DatasetSource>> map = new HashMap<Node, Producer<DatasetSource>>();
		final ProducerMap mapView = new ProducerMap() {
			
			@Override
			public Producer<DatasetSource> getProducer(Node graphId) {
				Producer<DatasetSource> producer = map.get(graphId);
				if (producer == null) {
					producer =
							DataflowComponentRegistry.get()
							.createProducer(configGraph, graphId, this);
					map.put(graphId, producer);
				}
				return producer;
			}
		};
		Producer<DatasetSource> inputProducer =
				new Producer<DatasetSource>() {

					@Override
					public DatasetSource create(DatasetSource inputDatasetSource) {
						return inputDatasetSource;
					}
				};
		map.put(configRoot, inputProducer);
		final Producer<DatasetSource> outputProducer =
				InlineDataset.getFactory().createProducer(configGraph, configRoot, mapView);
		return new Transformation() {
			
			@Override
			public DatasetSource apply(DatasetSource inputDataset) {
				return outputProducer.create(inputDataset);
			}
		};
	}

}

package org.swows.comp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.swows.producer.Producer;
import org.swows.producer.ProducerMap;
import org.swows.source.DatasetSource;
import org.swows.source.DatasetSourceFromDatasets;
import org.swows.transformation.Transformation;
import org.swows.transformation.TransformationFactory;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;

public class DataflowFactory implements TransformationFactory {
	
	private static DataflowFactory singleton = null;
	
	public static DataflowFactory get() {
		if (singleton == null) {
			singleton = new DataflowFactory();
		}
		return singleton;
	}
	
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
		final Set<Transitioner> transitioners = new HashSet<Transitioner>();
		for (Producer<DatasetSource> producer : map.values()) {
			if (producer instanceof Transitioner) {
//				Transitioner transitioner = (Transitioner) producer;
				transitioners.add((Transitioner) producer);
			}
		}
		return new Transformation() {
			
			@Override
			public DatasetSource apply(DatasetSource inputDataset) {
				final DatasetSource transitionOutput = outputProducer.create(inputDataset);
				DatasetSource output =
						new DatasetSourceFromDatasets(transitionOutput.lastDataset()) {
					{
						registerAsSnapshotListenerTo(transitionOutput);
						for (Transitioner t : transitioners)
							registerAsSnapshotListenerTo(t);
					}
					@Override
					protected void readyForExecution() {
						boolean transitionersChanged = false;
						for (Transitioner t : transitioners) {
							if (t.hasChanged()) {
								transitionersChanged = true;
								break;
							}
						}
						if (!transitionersChanged)
							setNewDataset(transitionOutput.lastDataset());
					}
				};
				for (Transitioner t : transitioners) t.newTransition();
				return output;
			}
		};
	}

}

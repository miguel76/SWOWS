package org.swows.comp;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.DatasetGraph;
import org.swows.producer.Producer;
import org.swows.producer.ProducerFactory;
import org.swows.producer.ProducerMap;
import org.swows.source.DatasetSource;
import org.swows.source.DatasetSourceFromDatasets;
import org.swows.transformation.TransformationRegistry;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;

public class Transformer extends DatasetSourceFromDatasets {
	
	private static DatasetSource transformationSource(
			Graph configGraph, Node configRoot, DatasetSource inputDatasetSource) {
		return TransformationRegistry.get()
				.transformationFromGraph(
						configGraph,
						configRoot)
				.apply(inputDatasetSource);
	}
	
	private DatasetSource configGraphSource, inputDatasetSource;
	private Node configRoot;
	
	private DatasetGraph updateTransformation() {
		final DatasetSource transSource =
				transformationSource(
						configGraphSource.lastDataset().getDefaultGraph(),
						configRoot,
						new DatasetSourceFromDatasets(inputDatasetSource.lastDataset()) {
							{
								registerAsSnapshotListenerTo(configGraphSource);
								registerAsSnapshotListenerTo(inputDatasetSource);
							}
							@Override
							protected void readyForExecution() {
								if (!configGraphSource.hasChanged())
									setNewDataset(inputDatasetSource.lastDataset());
							}
						});
		new DatasetSourceFromDatasets(inputDatasetSource.lastDataset()) {
			{
				registerAsSnapshotListenerTo(transSource);
			}
			@Override
			protected void readyForExecution() {
				Transformer.this.setNewDataset(transSource.lastDataset());
			}
		};
		return transSource.lastDataset();
	}
	
	public Transformer(
			DatasetSource configGraphSource,
			Node configRoot,
			DatasetSource inputDatasetSource) {
		super();
		this.configGraphSource = configGraphSource;
		this.configRoot = configRoot;
		this.inputDatasetSource = inputDatasetSource;
		lastDataset = updateTransformation();
//		lastDataset = transSource.lastDataset();
				
		registerAsSnapshotListenerTo(configGraphSource);
		registerAsSnapshotListenerTo(inputDatasetSource);
	}

	@Override
	protected void readyForExecution() {
		if (configGraphSource.hasChanged()) {
			setNewDataset(updateTransformation());
//			setNewDataset(transSource.lastDataset());
		}
	}
	
	private static ProducerFactory<DatasetSource> factory =
			new ProducerFactory<DatasetSource>() {

		@Override
		public Producer<DatasetSource> createProducer(
				final Graph conf,
				final Node confRoot,
				final ProducerMap map) {
			Node inlineConfNode =
					GraphUtils.getSingleValueOptProperty(conf, confRoot, DF.inlineConfig.asNode());
			if (inlineConfNode != null) {
				return new Producer<DatasetSource>() {
					private Producer<DatasetSource> inputProducer =
							InlineDataset.getFactory().createProducer(conf, confRoot, map);

					@Override
					public DatasetSource create(DatasetSource inputDatasetSource) {
						return transformationSource(
								conf, inlineConfNode,
								inputProducer.create(inputDatasetSource));
					}
				};
			}

			final Node confNode =
					GraphUtils.getSingleValueProperty(conf, confRoot, DF.config.asNode());
			final Node confRootNode =
					GraphUtils.getSingleValueProperty(conf, confRoot, DF.configRoot.asNode());
			return new Producer<DatasetSource>() {
				private Producer<DatasetSource> inputProducer =
						InlineDataset.getFactory().createProducer(conf, confRoot, map);
				private Producer<DatasetSource> configGraphProducer =
						map.getProducer(confNode);

				@Override
				public DatasetSource create(DatasetSource inputDatasetSource) {
					return new Transformer(
							configGraphProducer.create(inputDatasetSource),
							confRootNode,
							inputProducer.create(inputDatasetSource));
				}
			};
		}
		
	};
	
	public static ProducerFactory<DatasetSource> getFactory() {
		return factory;
	}


}

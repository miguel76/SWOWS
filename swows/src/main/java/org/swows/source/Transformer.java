package org.swows.source;

import org.swows.graph.events.DynamicDataset;
import org.swows.producer.ConstantProducerFactory;
import org.swows.producer.InlineDatasetProducer;
import org.swows.producer.Producer;
import org.swows.producer.ProducerFactory;
import org.swows.producer.ProducerMap;
import org.swows.producer.RDFProducer;
import org.swows.transformation.Transformation;
import org.swows.transformation.TransformationRegistry;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

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
//					{
//						Producer<DatasetSource> inputProducer;
					private Producer<DatasetSource> inputProducer =
							new InlineDatasetProducer(conf, confRoot, map);
//					}

					@Override
					public DatasetSource create(DynamicDataset inputDataset) {
						DatasetSource transformer =
								transformationSource(
										conf, confRoot,
										inputProducer.create(inputDataset));
					}
				};
			}
//				return (new ConstantProducerFactory<Transformation>())
//						.createProducer(TransformationRegistry.get().transformationFromGraph(conf, inlineConfNode));
			final Node confNode =
					GraphUtils.getSingleValueProperty(conf, confRoot, DF.config.asNode());
			final Node confRootNode =
					GraphUtils.getSingleValueProperty(conf, confRoot, DF.configRoot.asNode());
			return new Producer<DatasetSource>() {
//				{
//					Producer<DatasetSource> inputProducer;
				private Producer<DatasetSource> inputProducer =
						new InlineDatasetProducer(conf, confRoot, map);
				private Producer<DatasetSource> configGraphProducer =
						map.getProducer(confNode);
//				}

				@Override
				public DatasetSource create(DynamicDataset inputDataset) {
					DatasetSource transformer =
							new Transformer(
									configGraphProducer.create(inputDataset), confRootNode,
									inputProducer.create(inputDataset));
				}
			};
		}
		
	};
	
	public static ProducerFactory<DatasetSource> getFactory() {
		return factory;
	}


}

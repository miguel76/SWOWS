package org.swows.comp;

import org.swows.producer.Producer;
import org.swows.producer.ProducerFactory;
import org.swows.producer.ProducerMap;
import org.swows.source.DatasetSource;
import org.swows.source.DatasetSourceTemporal;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

public class Transitioner extends DatasetSourceTemporal {
	
	private DatasetSource inputGraphSource;
	
	public Transitioner(DatasetSource inputGraphSource) {
		super();
		this.inputGraphSource = inputGraphSource;
		registerAsSnapshotListenerTo(inputGraphSource);
	}
	
	public void newTransition() {
		change(inputGraphSource.lastDataset());
	}
	
	public DatasetSource getInputSource() {
		return inputGraphSource;
	}

	@Override
	protected void readyForExecution() {
		if (inputGraphSource.hasChanged())
			newTransition();
	}

	private static ProducerFactory<DatasetSource> factory =
			new ProducerFactory<DatasetSource>() {

		@Override
		public Producer<DatasetSource> createProducer(
				final Graph conf,
				final Node confRoot,
				final ProducerMap map) {
			Node datasetNode =
					GraphUtils.getSingleValueProperty(conf, confRoot, DF.input.asNode());
			final Producer<DatasetSource> datasetProducer =
					map.getProducer(datasetNode);

			return new Producer<DatasetSource>() {

				@Override
				public DatasetSource create(DatasetSource inputDatasetSource) {
					return new Transitioner(datasetProducer.create(inputDatasetSource));
				}
			};
			
		}
		
	};
	
	public static ProducerFactory<DatasetSource> getFactory() {
		return factory;
	}

}

/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open datatafloW System (SWOWS).

 * SWOWS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * SWOWS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General
 * Public License along with SWOWS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.swows.comp;

import org.swows.producer.Producer;
import org.swows.producer.ProducerFactory;
import org.swows.producer.ProducerMap;
import org.swows.source.DatasetChanges;
import org.swows.source.DatasetChangesFactory;
import org.swows.source.DatasetSource;
import org.swows.source.DatasetSourceBase;
import org.swows.source.DatasetSourceListener;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;

public class NamedGraphSelector extends DatasetSourceBase {
	
	DatasetSource datasetSource;
	Node graphName;
	boolean listeningFromSnapshots = false;
	boolean listeningFromChanges = false;
	
	private static DatasetGraph createDataset(
			DatasetSource datasetSource,
			Node graphName) {
		return DatasetGraphFactory.createOneGraph(
				datasetSource.lastDataset().getGraph(graphName));
	}
	
	private DatasetChanges createChanges() {
		DatasetChanges inputChanges= datasetSource.changesFromPrevDataset();
		
		return DatasetChangesFactory.fromDatasets(
				DatasetGraphFactory.createOneGraph(
						inputChanges.getAddedAsDataset().getGraph(graphName)),
				DatasetGraphFactory.createOneGraph(
						inputChanges.getDeletedAsDataset().getGraph(graphName)));
	}
	
	public NamedGraphSelector(
			DatasetSource datasetSource,
			Node graphName) {
		super(createDataset(datasetSource, graphName));
		this.datasetSource = datasetSource;
		this.graphName = graphName;
	}
	
	@Override
	public void registerSnapshotListener(DatasetSourceListener l) {
		super.registerSnapshotListener(l);
		if (!listeningFromSnapshots) {
			registerAsSnapshotListenerTo(datasetSource);
			listeningFromSnapshots = true;
		}
	}

	@Override
	public void registerChangesListener(DatasetSourceListener l) {
		super.registerSnapshotListener(l);
		if (!listeningFromChanges) {
			registerAsChangesListenerTo(datasetSource);
			listeningFromChanges = true;
		}
	}
	
	@Override
	public synchronized DatasetGraph lastDataset() {
		return lastDataset;
	}

	@Override
	public synchronized DatasetChanges changesFromPrevDataset() {
		return changesFromPrev;
	}
	
	@Override
	protected void allSnapshotListenersReady() {
		lastDataset = null;
	}

	@Override
	protected void allChangesListenersReady() {
		changesFromPrev = null;
	}

	@Override
	protected void allListenersReady() { }

	@Override
	protected void readyForExecution() {
		if (listeningFromSnapshots)
			lastDataset = createDataset(datasetSource, graphName);
		if (listeningFromChanges)
			changesFromPrev = createChanges();
		hasChanged = true;
		notifyAdvanced();
	}

	private static ProducerFactory<DatasetSource> factory =
			new ProducerFactory<DatasetSource>() {

		@Override
		public Producer<DatasetSource> createProducer(
				final Graph conf,
				final Node confRoot,
				final ProducerMap map) {
			Node datasetNode = GraphUtils.getSingleValueProperty(conf, confRoot, DF.datasetProducer.asNode());
			final Node nameNode = GraphUtils.getSingleValueProperty(conf, confRoot, DF.name.asNode());
			final Producer<DatasetSource> datasetProducer =
					map.getProducer(datasetNode);

			return new Producer<DatasetSource>() {

				@Override
				public DatasetSource create(DatasetSource inputDatasetSource) {
					return new NamedGraphSelector(datasetProducer.create(inputDatasetSource), nameNode);
				}
			};
			
		}
		
	};
	
	public static ProducerFactory<DatasetSource> getFactory() {
		return factory;
	}

}

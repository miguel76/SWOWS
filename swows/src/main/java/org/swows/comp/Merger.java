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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.graph.GraphFactory;
import org.swows.producer.Producer;
import org.swows.producer.ProducerFactory;
import org.swows.producer.ProducerMap;
import org.swows.source.DatasetSource;
import org.swows.source.DatasetSourceFromDatasets;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;

public class Merger extends DatasetSourceFromDatasets {
	
	Set<DatasetSource> inputGraphSources;
	boolean listeningFromSnapshots = false;
	boolean listeningFromChanges = false;
	
	private static DatasetGraph createDataset(
			Set<DatasetSource> inputGraphSources) {
		Graph outputGraph = GraphFactory.createGraphMem();
		for (DatasetSource inputGraphSource : inputGraphSources) {
			Graph inputGraph = inputGraphSource.lastDataset().getDefaultGraph();
			Iterator<Triple> inputIter =
					inputGraph.find(Node.ANY, Node.ANY, Node.ANY);
			while (inputIter.hasNext())
				outputGraph.add(inputIter.next());
		}
		return DatasetGraphFactory.createOneGraph(outputGraph);
	}
	
	public Merger(Set<DatasetSource> inputGraphSources) {
		super(createDataset(inputGraphSources));
		this.inputGraphSources = inputGraphSources;
	}
	
	@Override
	protected void readyForExecution() {
		setNewDataset(createDataset(inputGraphSources));;
	}

	private static ProducerFactory<DatasetSource> factory =
			new ProducerFactory<DatasetSource>() {

		@Override
		public Producer<DatasetSource> createProducer(
				final Graph conf,
				final Node confRoot,
				final ProducerMap map) {
			final Set<Producer<DatasetSource>> inputProducers =
					new HashSet<Producer<DatasetSource>>();
			Iterator<Node> inputNodes = GraphUtils.getPropertyValues(conf, confRoot, DF.input.asNode());
			while (inputNodes.hasNext()) {
				Producer<DatasetSource> producer = map.getProducer(inputNodes.next());
				inputProducers.add(producer);
			}
			return new Producer<DatasetSource>() {

				@Override
				public DatasetSource create(DatasetSource inputDatasetSource) {
					Set<DatasetSource> inputGraphSources =
							new HashSet<DatasetSource>();
					for (Producer<DatasetSource> inputProducer: inputProducers) {
						inputGraphSources.add(inputProducer.create(inputDatasetSource));
					}
					return new Merger(inputGraphSources);
				}
			};
			
		}
		
	};
	
	public static ProducerFactory<DatasetSource> getFactory() {
		return factory;
	}

}

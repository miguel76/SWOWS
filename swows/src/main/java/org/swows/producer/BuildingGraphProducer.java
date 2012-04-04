/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open Web Server (SWOWS).

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
package org.swows.producer;

import java.util.HashMap;
import java.util.Map;

import org.swows.graph.BuildingGraph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

/**
 * The Class BuildingGraphProducer returns an empty graph
 * until it is attached to another Producer.
 * When it is attached returns graphs, initially empty,
 * that keep synchronized to the graphs returning from
 * {@code createGraph } calls to the attached Producer.
 * This class is used to manage recursion in {@link DataflowProducer }.
 * It's never used directly in a dataflow so doesn't
 * implement the standard Producer constructor.
 */
public class BuildingGraphProducer extends GraphProducer {

	private NotifyingProducer connectedProducer;
	
	private Map<DatasetGraph,BuildingGraph> localGraphs = new HashMap<DatasetGraph, BuildingGraph>();
//	private BuildingGraph localGraph = new BuildingGraph(Graph.emptyGraph);

	private BuildingGraph getLocalGraph(DatasetGraph inputDataset) {
		if (localGraphs.containsKey(inputDataset))
			return localGraphs.get(inputDataset);
		else {
			BuildingGraph newLocalGraph = new BuildingGraph(Graph.emptyGraph);
			localGraphs.put(inputDataset, newLocalGraph);
			return newLocalGraph;
		}
	}

	/**
	 * Attache the instance to another producer.
	 *
	 * @param prod the producer
	 */
	public void attacheTo(NotifyingProducer prod) {
		connectedProducer = prod;
		prod.registerListener(
				new GraphProducerListener() {
					@Override
					public void notifyGraphCreation(DatasetGraph inputDataset, Graph graph) {
						getLocalGraph(inputDataset).setBaseGraph(graph,null);
					}
				});
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.GraphProducer#createGraph(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public Graph createGraph(DatasetGraph inputDataset) {
		return getLocalGraph(inputDataset);
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
	 */
	@Override
	public boolean dependsFrom(Producer producer) {
		return (producer == connectedProducer || connectedProducer.dependsFrom(producer));
	}

}

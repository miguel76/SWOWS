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
package org.swows.producer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.swows.graph.DynamicDatasetCollection;
import org.swows.graph.RecursionGraph;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;

import com.hp.hpl.jena.graph.Node;
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
public class BuildingDatasetProducer extends DatasetProducer {

	private NotifyingProducer connectedProducer;
	
	private Set<Node> graphNameSet;
	private Map<DatasetGraph,RecursionGraph> localDefaultGraphs = new HashMap<DatasetGraph, RecursionGraph>();
	private Map<DatasetGraph,Map<Node,RecursionGraph>> localNamedGraphs = new HashMap<DatasetGraph, Map<Node,RecursionGraph>>();
//	private BuildingGraph localGraph = new BuildingGraph(Graph.emptyGraph);

	private RecursionGraph getLocalDefaultGraph(DatasetGraph inputDataset) {
		if (localDefaultGraphs.containsKey(inputDataset))
			return localDefaultGraphs.get(inputDataset);
		else {
			RecursionGraph newLocalGraph = new RecursionGraph(DynamicGraph.emptyGraph);
			localDefaultGraphs.put(inputDataset, newLocalGraph);
			return newLocalGraph;
		}
	}

	private RecursionGraph getLocalNamedGraph(DatasetGraph inputDataset, Node graphName) {
		Map<Node,RecursionGraph> thisInputDatasetNamedMap;
		if (localNamedGraphs.containsKey(inputDataset))
			thisInputDatasetNamedMap = localNamedGraphs.get(inputDataset);
		else {
			thisInputDatasetNamedMap = new HashMap<Node, RecursionGraph>();
			localNamedGraphs.put(inputDataset, thisInputDatasetNamedMap);
		}
		if (thisInputDatasetNamedMap.containsKey(graphName))
			return thisInputDatasetNamedMap.get(graphName);
		else {
			RecursionGraph newLocalGraph = new RecursionGraph(DynamicGraph.emptyGraph);
			thisInputDatasetNamedMap.put(graphName, newLocalGraph);
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
				new DatasetProducerListener() {
					public void notifyDatasetCreation(DynamicDataset inputDataset, DynamicDataset dataset) {
						getLocalDefaultGraph(inputDataset).setBaseGraph(dataset.getDefaultGraph());
						if (graphNameSet == null) {
							graphNameSet = new HashSet<Node>();
							Iterator<Node> graphNames = dataset.listGraphNodes();
							while (graphNames.hasNext())
								graphNameSet.add(graphNames.next());
						}
						Iterator<Node> graphNames = dataset.listGraphNodes();
						while (graphNames.hasNext()) {
							Node graphName = graphNames.next();
							getLocalNamedGraph(inputDataset, graphName).setBaseGraph(dataset.getGraph(graphName));
						}
					}
				});
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
	 */
	public boolean dependsFrom(Producer producer) {
		return (producer == connectedProducer || connectedProducer.dependsFrom(producer));
	}

	@Override
	public DynamicDataset createDataset(final DynamicDataset inputDataset) {
		return new DynamicDatasetCollection() {
			@Override
			public DynamicGraph getGraph(Node graphNode) {
				return getLocalNamedGraph(inputDataset, graphNode);
			}
			@Override
			public DynamicGraph getDefaultGraph() {
				return getLocalDefaultGraph(inputDataset);
			}
			@Override
			public Iterator<Node> listGraphNodes() {
				if (graphNameSet == null)
					throw new RuntimeException("The graph names are still undefined");
				return graphNameSet.iterator();
			}
		};
	}

}

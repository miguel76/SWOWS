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

import org.swows.graph.DelegatingGraph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

/**
 * The Class RecursiveGraphProducer is used to calculate
 * recursion, repeatedly executing an other producer until
 * convergence is reached.
 * The inner producer, called step graph producer, is set
 * throw the {@link #setStepGraphProducer} method.
 * RecursiveGraphProducer it's never used directly in a dataflow so doesn't
 * implement the standard Producer constructor.
 */
public class RecursiveGraphProducer extends GraphProducer {

	private GraphProducer stepGraphProducer = null;

	/**
	 * Sets the step graph producer.
	 *
	 * @param stepGraphProducer the new step graph producer
	 */
	public void setStepGraphProducer(GraphProducer stepGraphProducer) {
		this.stepGraphProducer = stepGraphProducer;
	}

	/**
	 * Gets the step graph producer.
	 *
	 * @return the step graph producer
	 */
	public GraphProducer getStepGraphProducer() {
		return stepGraphProducer;
	}

	private static Map<GraphProducer,Map<DatasetGraph, Graph>> tempGraphMap = new HashMap<GraphProducer,Map<DatasetGraph, Graph>>();

	private static Graph getFromTempGraphMap(GraphProducer graphProd, DatasetGraph dataset) {
		Map<DatasetGraph, Graph> innerMap = tempGraphMap.get(graphProd);
		if (innerMap == null)
			return null;
		return innerMap.get(dataset);
	}

	private static void addToTempGraphMap(GraphProducer graphProd, DatasetGraph dataset, Graph graph) {
		Map<DatasetGraph, Graph> innerMap = tempGraphMap.get(graphProd);
		if (innerMap == null) {
			innerMap = new HashMap<DatasetGraph, Graph>();
			tempGraphMap.put(graphProd, innerMap);
		}
		innerMap.put(dataset, graph);
	}

	private static void removeFromTempGraphMap(GraphProducer graphProd, DatasetGraph dataset) {
		Map<DatasetGraph, Graph> innerMap = tempGraphMap.get(graphProd);
		innerMap.remove(dataset);
		if (innerMap.isEmpty())
			tempGraphMap.remove(graphProd);
	}

//	private Map<DatasetGraph, Graph> tempGraphMap = new HashMap<DatasetGraph, Graph>();
//	private Map<DatasetGraph, Integer> tempGraphMapUse = new HashMap<DatasetGraph, Integer>();

	/* (non-Javadoc)
	 * @see org.swows.producer.GraphProducer#createGraph(com.hp.hpl.jena.sparql.core.DatasetGraph)
 	 */
	@Override
	public Graph createGraph(final DatasetGraph inputDataset) {
		Graph tempGraph = null;
		synchronized (tempGraphMap) {
			tempGraph = getFromTempGraphMap(stepGraphProducer, inputDataset);
			if (tempGraph == null) {
				//tempGraphMapUse.put(inputDataset,new Integer(0));
				tempGraph =
					new DelegatingGraph() {
						private Graph currGraph = Graph.emptyGraph;
						private boolean currGraphUsed = true;
						{
							//tempGraphMapUse.put(inputDataset,new Integer(tempGraphMapUse.get(inputDataset).intValue() + 1));
							addToTempGraphMap(stepGraphProducer, inputDataset, this);
							Graph prevGraph = null;
							while (currGraphUsed && (prevGraph == null || !currGraph.isIsomorphicWith(prevGraph))) {
								prevGraph = currGraph;
								currGraphUsed = false;
								currGraph = stepGraphProducer.createGraph(inputDataset);
								//System.out.println("*** currGraph ****");
								//ModelFactory.createModelForGraph(currGraph).write(System.out,"N3");
								//System.out.println("******************");
							}
							//tempGraphMapUse.put(inputDataset,new Integer(tempGraphMapUse.get(inputDataset).intValue() - 1));
							synchronized (tempGraphMap) {
								removeFromTempGraphMap(stepGraphProducer, inputDataset);
							}
						}
						@Override
						protected Graph getBaseGraph() {
							currGraphUsed = true;
							return currGraph;
						}
					};
				//addToTempGraphMap(stepGraphProducer, inputDataset, tempGraph);
			}
		}
		return tempGraph;
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
	 */
	@Override
	public boolean dependsFrom(Producer producer) {
		return (producer == stepGraphProducer || stepGraphProducer.dependsFrom(producer));
	}

}

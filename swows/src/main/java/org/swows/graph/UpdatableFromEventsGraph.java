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
package org.swows.graph;

import java.util.Iterator;
import java.util.List;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphUtil;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.sparql.core.DatasetGraphMap;
import org.apache.jena.sparql.graph.GraphFactory;
import org.swows.graph.events.DelegatingDynamicGraph;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;
import org.swows.vocabulary.SWI;

public class UpdatableFromEventsGraph extends DelegatingDynamicGraph {
	
//	List<Graph> addEventGraphList = new Vector<Graph>();
//	List<Query> addQueryList = new Vector<Query>();
//	List<Graph> deleteEventGraphList = new Vector<Graph>();
//	List<Query> deleteQueryList = new Vector<Query>();

	public UpdatableFromEventsGraph(
			final List<DynamicGraph> addEventGraphList,
			final List<Query> addQueryList,
			final List<DynamicDataset> addQueryInputDatasetList,
			final List<DynamicGraph> deleteEventGraphList,
			final List<Query> deleteQueryList,
			final List<DynamicDataset> deleteQueryInputDatasetList ) {
		baseGraphCopy = new DynamicGraphFromGraph( GraphFactory.createGraphMem() );

		{
		Iterator<Query> queryIter = addQueryList.iterator();
		Iterator<DynamicDataset> inputDatasetIter = addQueryInputDatasetList.iterator();
		for (final DynamicGraph eventGraph : addEventGraphList) {
			final Query query = queryIter.next();
			final DynamicDataset originalInputDataset = inputDatasetIter.next();
			eventGraph.getEventManager2().register(
					new Listener() {
						public synchronized void notifyUpdate(Graph source, final GraphUpdate update) {
							System.out.println("Start Add notifyUpdate");
							System.out.println("This graph: " + baseGraphCopy);
							System.out.println("Added graph: " +  update.getAddedGraph());
							System.out.println("Deleted graph: " +  update.getDeletedGraph());
							System.out.println("Default input graph: " + originalInputDataset.getDefaultGraph());
							DatasetGraphMap inputDataset = new DatasetGraphMap(originalInputDataset);
							
							Graph thisGraph = GraphFactory.createGraphMem();
							GraphUtil.addInto(thisGraph, baseGraphCopy);
							inputDataset.addGraph(SWI.ThisGraph.asNode(), thisGraph);
							
							Graph addedGraph = GraphFactory.createGraphMem();
							GraphUtil.addInto(addedGraph, update.getAddedGraph());
							inputDataset.addGraph(SWI.AddedGraph.asNode(), addedGraph);
							
							Graph deletedGraph = GraphFactory.createGraphMem();
							GraphUtil.addInto(deletedGraph, update.getDeletedGraph());
							inputDataset.addGraph(SWI.DeletedGraph.asNode(), deletedGraph);
							
							QueryExecution queryExecution =
									QueryExecutionFactory.create(query, DatasetFactory.create(inputDataset));
					        final Graph resGraph = queryExecution.execConstruct().getGraph();
							System.out.println("Query Result: " + resGraph);
							queryExecution.close();
							GraphUtil.addInto(baseGraphCopy, resGraph);
							((DynamicGraphFromGraph) baseGraphCopy).sendUpdateEvents();
//							getEventManager2().notifyUpdate(new GraphUpdate() {
//								@Override
//								public Graph getAddedGraph() {
//									return resGraph;
//								}
//								@Override
//								public Graph getDeletedGraph() {
//									return Graph.emptyGraph;
//								}
//							});
							System.out.println("End of Add notifyUpdate");
						}
					} );
		}
		}

		{
		Iterator<Query> queryIter = deleteQueryList.iterator();
		Iterator<DynamicDataset> inputDatasetIter = deleteQueryInputDatasetList.iterator();
		for (final DynamicGraph eventGraph : deleteEventGraphList) {
			final Query query = queryIter.next();
			final DynamicDataset originalInputDataset = inputDatasetIter.next();
			eventGraph.getEventManager2().register(
					new Listener() {
						public synchronized void notifyUpdate(Graph source, final GraphUpdate update) {
//							System.out.println("Start Delete notifyUpdate");
//							System.out.println("This graph: " + baseGraphCopy);
//							System.out.println("Added graph: " +  update.getAddedGraph());
//							System.out.println("Deleted graph: " +  update.getDeletedGraph());
							DatasetGraphMap inputDataset = new DatasetGraphMap(originalInputDataset);
							// TODO: probably local copies needed as in the "adder notifier"
							inputDataset.addGraph(SWI.ThisGraph.asNode(), baseGraphCopy);
							inputDataset.addGraph(SWI.AddedGraph.asNode(), update.getAddedGraph());
							inputDataset.addGraph(SWI.DeletedGraph.asNode(), update.getDeletedGraph());
							QueryExecution queryExecution =
									QueryExecutionFactory.create(query, DatasetFactory.create(inputDataset));
					        final Graph resGraph = queryExecution.execConstruct().getGraph();
//							System.out.println("Query Result: " + resGraph);
							queryExecution.close();
							GraphUtil.deleteFrom(baseGraphCopy, resGraph);
							((DynamicGraphFromGraph) baseGraphCopy).sendUpdateEvents();
//							getEventManager2().notifyUpdate(new GraphUpdate() {
//								@Override
//								public Graph getAddedGraph() {
//									return Graph.emptyGraph;
//								}
//								@Override
//								public Graph getDeletedGraph() {
//									return resGraph;
//								}
//							});
//							System.out.println("End of Delete notifyUpdate");
						}
					} );
		}
		}
	}
	
	@Override
	protected DynamicGraph getBaseGraph() {
		return null; // Not used because baseGraphCopy is set in constructor
	}

}

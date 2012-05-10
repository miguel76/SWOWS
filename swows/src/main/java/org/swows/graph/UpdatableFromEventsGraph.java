package org.swows.graph;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.swows.graph.events.DelegatingDynamicGraph;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;
import org.swows.vocabulary.Instance;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraphMap;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

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
						@Override
						public void notifyUpdate(Graph source, final GraphUpdate update) {
							DatasetGraphMap inputDataset = new DatasetGraphMap(originalInputDataset);
							inputDataset.addGraph(Instance.ThisGraph.asNode(), baseGraphCopy);
							inputDataset.addGraph(Instance.AddedGraph.asNode(), update.getAddedGraph());
							inputDataset.addGraph(Instance.DeletedGraph.asNode(), update.getDeletedGraph());
							QueryExecution queryExecution =
									QueryExecutionFactory.create(query, DatasetFactory.create(inputDataset));
					        final Graph resGraph = queryExecution.execConstruct().getGraph();
							queryExecution.close();
							baseGraphCopy.getBulkUpdateHandler().add(resGraph);
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
						@Override
						public void notifyUpdate(Graph source, final GraphUpdate update) {
							DatasetGraphMap inputDataset = new DatasetGraphMap(originalInputDataset);
							inputDataset.addGraph(Instance.ThisGraph.asNode(), baseGraphCopy);
							inputDataset.addGraph(Instance.AddedGraph.asNode(), update.getAddedGraph());
							inputDataset.addGraph(Instance.DeletedGraph.asNode(), update.getDeletedGraph());
							QueryExecution queryExecution =
									QueryExecutionFactory.create(query, DatasetFactory.create(inputDataset));
					        final Graph resGraph = queryExecution.execConstruct().getGraph();
							queryExecution.close();
							baseGraphCopy.getBulkUpdateHandler().delete(resGraph);
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

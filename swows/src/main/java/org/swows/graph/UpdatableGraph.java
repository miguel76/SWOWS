package org.swows.graph;

import org.swows.graph.events.DelegatingDynamicGraph;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

public class UpdatableGraph extends DelegatingDynamicGraph {
	
	public UpdatableGraph(
			final DynamicGraph baseGraph,
			final DynamicGraph addGraph,
			final DynamicGraph deleteGraph ) {
		baseGraphCopy = new DynamicGraphFromGraph( GraphFactory.createGraphMem() );
		final BulkUpdateHandler bulkUpdateHandler = baseGraphCopy.getBulkUpdateHandler();
		bulkUpdateHandler.add(baseGraph);
		bulkUpdateHandler.add(addGraph);
		bulkUpdateHandler.delete(deleteGraph);
		baseGraph.getEventManager2().register(
				new Listener() {
					@Override
					public void notifyUpdate(Graph source, final GraphUpdate update) {
						baseGraphCopy.getBulkUpdateHandler().add(update.getAddedGraph());
						baseGraphCopy.getBulkUpdateHandler().delete(update.getDeletedGraph());
						getEventManager2().notifyUpdate(new GraphUpdate() {
							@Override
							public Graph getAddedGraph() {
								return update.getAddedGraph();
							}
							@Override
							public Graph getDeletedGraph() {
								return update.getDeletedGraph();
							}
						});
					}
				} );
		addGraph.getEventManager2().register(
				new Listener() {
					@Override
					public void notifyUpdate(Graph source, final GraphUpdate update) {
						baseGraphCopy.getBulkUpdateHandler().add(update.getAddedGraph());
						getEventManager2().notifyUpdate(new GraphUpdate() {
							@Override
							public Graph getAddedGraph() {
								return update.getAddedGraph();
							}
							@Override
							public Graph getDeletedGraph() {
								return Graph.emptyGraph;
							}
						});
					}
				} );
		deleteGraph.getEventManager2().register(
				new Listener() {
					@Override
					public void notifyUpdate(Graph source, final GraphUpdate update) {
						baseGraphCopy.getBulkUpdateHandler().delete(update.getAddedGraph());
						getEventManager2().notifyUpdate(new GraphUpdate() {
							@Override
							public Graph getAddedGraph() {
								return Graph.emptyGraph;
							}
							@Override
							public Graph getDeletedGraph() {
								return update.getAddedGraph();
							}
						});
					}
				} );
	}
	
	@Override
	protected DynamicGraph getBaseGraph() {
		return null; // Not used because baseGraphCopy is set in constructor
	}

}

package org.swows.graph;

import org.swows.graph.events.DelegatingDynamicGraph;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.graph.events.EventManager;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.SimpleEventManager;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.compose.Difference;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

public class RecursionGraph extends DelegatingDynamicGraph {
	
	EventManager localEventManager = new SimpleEventManager(this);
	
	/**
	 * Instantiates a new building graph.
	 */
	protected RecursionGraph() {
		super();
		baseGraphCopy = new DynamicGraphFromGraph( Graph.emptyGraph );
	}

	/**
	 * Instantiates a new building graph.
	 *
	 * @param initialGraph the initial graph
	 */
	public RecursionGraph(DynamicGraph initialGraph) {
		super();
		baseGraphCopy = initialGraph;
		initialGraph.getEventManager2().register(localEventManager);
	}

	/**
	 * Sets the base graph.
	 *
	 * @param newGraph the new graph
	 * @param sourceGraph the source graph
	 */
	public synchronized void setBaseGraph(DynamicGraph newGraph) {

//		System.out.println(this + ": *** OLD GRAPH *******");
//		ModelFactory.createModelForGraph(baseGraphCopy).write(System.out,"N3");
//		System.out.println();
//		System.out.println(this + ": *** NEW GRAPH *******");
//		ModelFactory.createModelForGraph(newGraph).write(System.out,"N3");
//		System.out.println();

		Graph oldGraph = baseGraphCopy;
		
		baseGraphCopy.getEventManager2().unregister(localEventManager);
		baseGraphCopy = newGraph;
		baseGraphCopy.getEventManager2().register(localEventManager);

		final Graph addedGraph = GraphFactory.createGraphMem();
//		addedGraph.getBulkUpdateHandler().add(newGraph);
//		addedGraph.getBulkUpdateHandler().delete(oldGraph);
		addedGraph.getBulkUpdateHandler().add(new Difference(newGraph, oldGraph));
		final Graph deletedGraph = GraphFactory.createGraphMem();
//		addedGraph.getBulkUpdateHandler().add(oldGraph);
//		addedGraph.getBulkUpdateHandler().delete(newGraph);
		deletedGraph.getBulkUpdateHandler().add(new Difference(oldGraph, newGraph));
//		final Graph addedGraph = new Difference(newGraph, oldGraph);
//		final Graph deletedGraph = new Difference(oldGraph, newGraph);
		if (!addedGraph.isEmpty() || !deletedGraph.isEmpty()) {
			localEventManager.notifyUpdate(new GraphUpdate() {
				@Override
				public Graph getAddedGraph() {
					return addedGraph;
				}
				@Override
				public Graph getDeletedGraph() {
					return deletedGraph;
				}
			});
		}
	}

	/* (non-Javadoc)
	 * @see org.swows.graph.DelegatingGraph#getBaseGraph()
	 */
	@Override
	protected DynamicGraph getBaseGraph() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.swows.graph.DelegatingGraph#getEventManager()
	 */
	@Override
	public EventManager getEventManager2() {
		return localEventManager;
	}

}

package org.swows.graph;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.SimpleEventManager;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

public class UpdatableGraph extends DelegatingGraph {
	
	/** The local event manager. */
	protected GraphEventManager localEventManager =
		new SimpleEventManager(this);

	public UpdatableGraph(
			final Graph baseGraph,
			final Graph addGraph,
			final Graph deleteGraph ) {
		baseGraphCopy = GraphFactory.createGraphMem();
		final BulkUpdateHandler bulkUpdateHandler = baseGraphCopy.getBulkUpdateHandler();
		bulkUpdateHandler.add(baseGraph);
		bulkUpdateHandler.add(addGraph);
		bulkUpdateHandler.delete(deleteGraph);
		baseGraph.getEventManager().register(
				new PushGraphListener(baseGraph, localEventManager) {
					protected void notifyAdd(Triple t) {
						baseGraphCopy.add(t);
					}
					protected void notifyDelete(Triple t) {
						baseGraphCopy.delete(t);
					}
				} );
		addGraph.getEventManager().register(
				new PushGraphListener(addGraph, localEventManager) {
					protected void notifyAdd(Triple t) {
						baseGraphCopy.add(t);
					}
				} );
		deleteGraph.getEventManager().register(
				new PushGraphListener(deleteGraph, localEventManager) {
					protected void notifyAdd(Triple t) {
						baseGraphCopy.delete(t);
					}
				} );
	}
	
	@Override
	protected Graph getBaseGraph() {
		return null; // Not used because baseGraphCopy is set in constructor
	}

	/* (non-Javadoc)
	 * @see org.swows.graph.DelegatingGraph#getEventManager()
	 */
	@Override
	public GraphEventManager getEventManager() {
		return localEventManager;
	}

}

package org.swows.graph;

import com.hp.hpl.jena.graph.Graph;

public class RecursionGraph extends BuildingGraph {
	
//	private GraphListener listener;
	
//	private void setupListener() {
//		listener =
//				new PushGraphListener(baseGraphCopy, localEventManager) {
//					protected void notifyAdd(Triple t) {
//						baseGraphCopy.add(t);
//					}
//					protected void notifyDelete(Triple t) {
//						baseGraphCopy.delete(t);
//					}
//				};
//		
//	}

//	private void removeListener() {
//		
//	}

	public RecursionGraph(Graph baseGraph) {
		super(baseGraph);
//		setupListener();
	}
	
	/**
	 * Sets the base graph.
	 *
	 * @param newGraph the new graph
	 * @param sourceGraph the source graph
	 */
//	@Override
//	public synchronized void setBaseGraph(Graph newGraph, Graph sourceGraph) {
//		removeListener();
//		super.setBaseGraph(newGraph, sourceGraph);
//		setupListener();
//	}	
	
}

package org.swows.source;

import com.hp.hpl.jena.graph.Graph;

public interface GraphSource {

	public Graph lastGraph();
	public GraphChanges changesFromPrevGraph();
	public void currNotNeeded(GraphSourceListener l);
	public void advance();
	public void registerListener(GraphSourceListener l);
	public void unregisterListener(GraphSourceListener l);
	
}

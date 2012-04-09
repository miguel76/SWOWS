package org.swows.graph.events;

import com.hp.hpl.jena.graph.Graph;

public interface GraphUpdate {
	
	public Graph getAddedGraph();
	public Graph getDeletedGraph();
//	public Set<Triple> getAddedTriples();
//	public Set<Triple> getRemovedTriples();
//	public Transaction getTransaction();

}

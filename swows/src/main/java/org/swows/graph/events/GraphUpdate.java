package org.swows.graph.events;

import java.util.Set;

import com.hp.hpl.jena.graph.Triple;

public interface GraphUpdate {
	
	public Set<Triple> getAddedTriples();
	public Set<Triple> getRemovedTriples();
//	public Transaction getTransaction();

}

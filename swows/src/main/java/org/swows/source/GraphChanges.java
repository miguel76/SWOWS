package org.swows.source;

import java.util.Set;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleIterator;

public interface GraphChanges {
	
	public boolean isEmpty();
	
	public Graph getAddedAsGraph();
	public Graph getDeletedAsGraph();

	public Set<Triple> getAddedAsSet();
	public Set<Triple> getDeletedAsSet();
	
	public TripleIterator getAddedAsIterator();
	public TripleIterator getDeletedAsIterator();
	
}

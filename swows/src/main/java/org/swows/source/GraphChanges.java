package org.swows.source;

import java.util.Set;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.TripleIterator;

public interface GraphChanges {
	
	public boolean isEmpty();
	
	public Graph getAddedAsGraph();
	public Graph getDeletedAsGraph();

	public Set<Triple> getAddedAsSet();
	public Set<Triple> getDeletedAsSet();
	
	public TripleIterator getAddedAsIterator();
	public TripleIterator getDeletedAsIterator();
	
}

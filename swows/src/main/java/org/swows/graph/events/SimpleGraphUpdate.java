package org.swows.graph.events;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.graph.Triple;

public class SimpleGraphUpdate implements GraphUpdate {
	
	private Set<Triple> addedTriples = new HashSet<Triple>();
	private Set<Triple> removedTriples = new HashSet<Triple>();

	public void putAddedTriple(Triple triple) {
		if (removedTriples.contains(triple))
			removedTriples.remove(triple);
		else
			addedTriples.add(triple);
	}
	
	public void putRemovedTriple(Triple triple) {
		if (addedTriples.contains(triple))
			addedTriples.remove(triple);
		else
			removedTriples.add(triple);
	}
	
	public boolean isEmpty() {
		return addedTriples.isEmpty() && removedTriples.isEmpty();
	}
	
	@Override
	public Set<Triple> getAddedTriples() {
		return addedTriples;
	}

	@Override
	public Set<Triple> getRemovedTriples() {
		return removedTriples;
	}

}

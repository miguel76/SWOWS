package org.swows.graph.events;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

public abstract class SimpleListener implements Listener {

	protected abstract void beginNotify(Graph source);
	protected abstract void endNotify(Graph source);
	protected abstract void notifyDelete(Graph source, Triple triple);
	protected abstract void notifyAdd(Graph source, Triple triple);

	@Override
	public synchronized void notifyUpdate(Graph source, GraphUpdate update) {
		beginNotify(source);
		Iterator<Triple> addedTriples = update.getAddedGraph().find(Node.ANY, Node.ANY, Node.ANY);
		while( addedTriples.hasNext() )
			notifyAdd( source, addedTriples.next() );
		Iterator<Triple> deletedTriples = update.getDeletedGraph().find(Node.ANY, Node.ANY, Node.ANY);
		while( deletedTriples.hasNext() )
			notifyDelete( source, deletedTriples.next() );
		endNotify(source);
	}

}

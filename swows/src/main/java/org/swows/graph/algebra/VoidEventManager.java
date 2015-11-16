package org.swows.graph.algebra;

import java.util.Iterator;
import java.util.List;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphEventManager;
import org.apache.jena.graph.GraphListener;
import org.apache.jena.graph.Triple;

public class VoidEventManager implements GraphEventManager {

	private static final GraphEventManager instance =
			new VoidEventManager();
	
	public static GraphEventManager getInstance() {
		return instance;
	}
	
	private VoidEventManager() {
		
	}
	
	public void notifyAddTriple(Graph g, Triple t) {
		// TODO Auto-generated method stub

	}

	public void notifyAddArray(Graph g, Triple[] triples) {
		// TODO Auto-generated method stub

	}

	public void notifyAddList(Graph g, List<Triple> triples) {
		// TODO Auto-generated method stub

	}

	public void notifyAddIterator(Graph g, Iterator<Triple> it) {
		// TODO Auto-generated method stub

	}

	public void notifyAddGraph(Graph g, Graph added) {
		// TODO Auto-generated method stub

	}

	public void notifyDeleteTriple(Graph g, Triple t) {
		// TODO Auto-generated method stub

	}

	public void notifyDeleteList(Graph g, List<Triple> L) {
		// TODO Auto-generated method stub

	}

	public void notifyDeleteArray(Graph g, Triple[] triples) {
		// TODO Auto-generated method stub

	}

	public void notifyDeleteIterator(Graph g, Iterator<Triple> it) {
		// TODO Auto-generated method stub

	}

	public void notifyDeleteGraph(Graph g, Graph removed) {
		// TODO Auto-generated method stub

	}

	public void notifyEvent(Graph source, Object value) {
		// TODO Auto-generated method stub

	}

	public GraphEventManager register(GraphListener listener) {
		// TODO Auto-generated method stub
		return null;
	}

	public GraphEventManager unregister(GraphListener listener) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean listening() {
		// TODO Auto-generated method stub
		return false;
	}

	public void notifyAddIterator(Graph g, List<Triple> triples) {
		// TODO Auto-generated method stub

	}

	public void notifyDeleteIterator(Graph g, List<Triple> triples) {
		// TODO Auto-generated method stub

	}

}

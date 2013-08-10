package org.swows.graph.algebra;

import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphListener;
import com.hp.hpl.jena.graph.Triple;

public class VoidEventManager implements GraphEventManager {

	private static final GraphEventManager instance =
			new VoidEventManager();
	
	public static GraphEventManager getInstance() {
		return instance;
	}
	
	private VoidEventManager() {
		
	}
	
	@Override
	public void notifyAddTriple(Graph g, Triple t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyAddArray(Graph g, Triple[] triples) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyAddList(Graph g, List<Triple> triples) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyAddIterator(Graph g, Iterator<Triple> it) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyAddGraph(Graph g, Graph added) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyDeleteTriple(Graph g, Triple t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyDeleteList(Graph g, List<Triple> L) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyDeleteArray(Graph g, Triple[] triples) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyDeleteIterator(Graph g, Iterator<Triple> it) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyDeleteGraph(Graph g, Graph removed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyEvent(Graph source, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public GraphEventManager register(GraphListener listener) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphEventManager unregister(GraphListener listener) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean listening() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void notifyAddIterator(Graph g, List<Triple> triples) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyDeleteIterator(Graph g, List<Triple> triples) {
		// TODO Auto-generated method stub

	}

}

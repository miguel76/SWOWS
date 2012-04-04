package org.swows.graph;

import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEvents;
import com.hp.hpl.jena.graph.GraphListener;
import com.hp.hpl.jena.graph.Triple;

public abstract class FinishGraphListener implements GraphListener {

	@Override
	public void notifyAddTriple(Graph g, Triple t) {
	}

	@Override
	public void notifyAddArray(Graph g, Triple[] triples) {
	}

	@Override
	public void notifyAddList(Graph g, List<Triple> triples) {
	}

	@Override
	public void notifyAddIterator(Graph g, Iterator<Triple> it) {
	}

	@Override
	public void notifyAddGraph(Graph g, Graph added) {
	}

	@Override
	public void notifyDeleteTriple(Graph g, Triple t) {
	}

	@Override
	public void notifyDeleteList(Graph g, List<Triple> L) {
	}

	@Override
	public void notifyDeleteArray(Graph g, Triple[] triples) {
	}

	@Override
	public void notifyDeleteIterator(Graph g, Iterator<Triple> it) {
	}

	@Override
	public void notifyDeleteGraph(Graph g, Graph removed) {
	}

    @Override
    public void notifyEvent(Graph source, Object value)
    {
        if ( value.equals(GraphEvents.finishRead) )
            finishRead(source) ;
    }

    protected abstract void finishRead(Graph source);

}

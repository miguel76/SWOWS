package org.swows.graph.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.hp.hpl.jena.graph.Graph;

public class SimpleEventManager implements EventManager {

    protected Graph graph;
    protected List<Listener>  listeners;
    
    public SimpleEventManager( Graph graph ) 
        { 
        this.graph = graph;
        this.listeners = new CopyOnWriteArrayList<Listener>(); 
/* Implementation note: Jeremy Carroll
 * 
 * Use of CopyOnWriteArray is unnecessarily inefficient, in that
 * a copy is only needed when the register or unregister
 * is concurrent with an iteration over the list.
 * Since this list is not public we can either make it private
 * or provide methods for iterating, so that we know when
 * it is necessary to copy the array of listeners and when it 
 * isn't.
 * This is a fair bit of code, and would need either a lock or
 * an atomic integer or something from the concurrent package.
 * Until and unless the high cost of registering and unregistering
 * is an issue I think the current code is elegant and clean.
 * In practice, most graphs have no more than 10 listeners
 * so the 10 registrations take 55 word copy operations - nothing
 * to get upset about.
 */
        }
    
	@Override
	public void register(Listener listener) {
        listeners.add( listener );
	}

	@Override
	public void unregister(Listener listener) {
        listeners.remove( listener ); 
	}

	@Override
	public boolean listening() {
		return listeners.size() > 0;
	}

	@Override
	public void notifyUpdate(GraphUpdate update) {
	    notifyUpdate(graph, update); 
	}

	@Override
	public void notifyUpdate(Graph source, GraphUpdate update) {
		for (Listener l:listeners) 
			l.notifyUpdate( source, update ); 
	}

}

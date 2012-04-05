package org.swows.graph.events;

import com.hp.hpl.jena.graph.Graph;

public interface EventManager extends Listener {

    /**
    Attached <code>listener</code> to this manager; notification events
    sent to the manager are sent to all registered listeners. A listener may
    be registered multiple times, in which case it's called multiple times per
    event.
    
    A listener will be notified of an event if it is registered
    before the Graph method call that initiated the event, and 
    was not unregistered before that method call returned.
    In addition, a listener <em>may</em> (or may not) be notified 
    of an event if it is registered
    before such a method returns or is unregistered after such
    a method is called. For example, it may unregister itself
    in response to the event.
    
    If the registration and/or unregistration occur on different
    threads the usual thread uncertainties in such statements apply.
    
    @param listener a listener to be fed events
    */
	public void register( Listener listener );

	/**
    If <code>listener</code> is attached to this manager, detach it, otherwise
    do nothing. Only a single registration is removed.
    @param listener the listener to be detached from the graph
 	*/
	public void unregister( Listener listener );

	/**
    Answer true iff there is at least one attached listener.
	@return true iff there is at least one attached listener
	*/
	boolean listening();
	
    public void notifyUpdate( GraphUpdate update );

}

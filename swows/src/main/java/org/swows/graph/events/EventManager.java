/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open Web Server (SWOWS).

 * SWOWS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * SWOWS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General
 * Public License along with SWOWS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.swows.graph.events;

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

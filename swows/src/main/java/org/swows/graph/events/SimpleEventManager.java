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

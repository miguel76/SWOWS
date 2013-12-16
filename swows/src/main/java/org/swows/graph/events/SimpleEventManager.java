/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open datatafloW System (SWOWS).

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

import org.apache.log4j.Logger;
import org.swows.util.Utils;

import com.hp.hpl.jena.graph.Graph;

public class SimpleEventManager implements EventManager {

    protected Graph graph;
    protected List<Listener>  listeners;

    private static final Logger logger = Logger.getLogger(SimpleEventManager.class);
    
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
    
	public void register(Listener listener) {
		logger.debug("registering listener " + Utils.standardStr(listener) + " for " + Utils.standardStr(graph));
        listeners.add( listener );
	}

	public void unregister(Listener listener) {
		logger.debug("unregistering listener " + Utils.standardStr(listener) + " for " + Utils.standardStr(graph));
        listeners.remove( listener ); 
	}

	public boolean listening() {
		return listeners.size() > 0;
	}

	@Override
	public void startTransaction(Transaction transaction) {
		for (Listener l:listeners) {
			logger.debug("start transaction " + transaction + " to " + Utils.standardStr(l));
			l.startTransaction(transaction);
		}
	}

	@Override
	public void notifyUpdate(Transaction transaction) {
		for (Listener l:listeners) {
			logger.debug("notifying update for " + transaction + " to " + Utils.standardStr(l));
			l.notifyUpdate( transaction ); 
		}
//		logger.debug("Ended notifying update " + update + " from " + Utils.standardStr(source) + " in " + this);
	}

	@Override
	public void commit(Transaction transaction) {
		for (Listener l:listeners) {
			logger.debug("commit " + transaction + " to " + Utils.standardStr(l));
			l.commit(transaction);
		}
	}

}

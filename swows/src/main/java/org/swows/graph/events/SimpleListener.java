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

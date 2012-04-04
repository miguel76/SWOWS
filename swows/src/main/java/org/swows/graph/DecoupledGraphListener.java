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
package org.swows.graph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEvents;
import com.hp.hpl.jena.graph.GraphMaker;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.SimpleGraphMaker;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This graph listener routes the incoming events to a
 * specified event manager.
 * The events {@code GraphEvents.startRead} and {@code GraphEvents.finishRead}}
 * are used to start and finish an event block (a "transaction").
 * Incoming Events are cached until the block ends and then sent altogether
 * to the event manager.
 * This class is used (with inner private extensions) in
 * the classes of the same package to manage events.
 */
public class DecoupledGraphListener extends PushGraphListener {
	
	private static GraphMaker graphMaker = new SimpleGraphMaker(); 
	
	protected Graph decoupledAddEvents = null;
	protected Graph decoupledDeleteEvents = null;
	
	private boolean somethingToCopy = false;
	private boolean somethingToNotify = false;
	private boolean copying = false;
	private boolean notifying = false;

//	private Runnable copyingRunnable = new Runnable() {
	private Thread copyingThread = new Thread() {
		@Override
		public void run() {
			while (true) {
				while (true) {
					synchronized(this) {
						if (somethingToCopy && !notifying) {
							copying = true;
							break;
						}
					}
				}
				decoupledAddEvents = graphMaker.createGraph();
				synchronized (addEvents) {
					ExtendedIterator<Triple> addEventsIter =
							addEvents.find(Node.ANY, Node.ANY, Node.ANY);
					while (addEventsIter.hasNext())
						decoupledAddEvents.add(addEventsIter.next());
				}
				decoupledDeleteEvents = graphMaker.createGraph();
				synchronized (deleteEvents) {
					ExtendedIterator<Triple> deleteEventsIter =
							deleteEvents.find(Node.ANY, Node.ANY, Node.ANY);
					while (deleteEventsIter.hasNext())
						decoupledDeleteEvents.add(deleteEventsIter.next());
				}
				somethingToCopy = false;
				somethingToNotify = true;
				copying = false;
			}
		}
	}; 

	private Thread notifyingThread = new Thread() {
		@Override
		public void run() {
			while (true) {
				while (true) {
					synchronized(this) {
						if (somethingToNotify && !copying) {
							notifying = true;
							break;
						}
					}
				}
				if (eventManager != null)
					eventManager.notifyEvent(sourceGraph, GraphEvents.startRead);
				ExtendedIterator<Triple> addEventsIter =
						decoupledAddEvents.find(Node.ANY, Node.ANY, Node.ANY);
				while (addEventsIter.hasNext())
					sourceGraph.add(addEventsIter.next());
				ExtendedIterator<Triple> deleteEventsIter =
						decoupledDeleteEvents.find(Node.ANY, Node.ANY, Node.ANY);
				while (deleteEventsIter.hasNext())
					sourceGraph.delete(deleteEventsIter.next());
				if (eventManager != null)
					eventManager.notifyEvent(sourceGraph, GraphEvents.finishRead);
				somethingToNotify = false;
				notifying = false;
			}
		}
	}; 

	/**
	 * Instantiates a new decoupled graph listener.
	 *
	 * @param destinationGraph the destination graph
	 */
	public DecoupledGraphListener(Graph destinationGraph) {
		super(destinationGraph);
		copyingThread.start();
		notifyingThread.start();
	}
	
	/**
	 * It's called when an event block is finished, if
	 * there are events. If and only if it returns true the
	 * events are notified.
	 *
	 * @return true
	 */
	protected boolean changed() { return true; }

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.util.graph.GraphListenerBase#finishRead()
	 */
	@Override
    protected synchronized void finishRead() {
		somethingToCopy = true;
    }
}

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
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphEvents;
import com.hp.hpl.jena.graph.GraphMaker;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.compose.Difference;
import com.hp.hpl.jena.graph.compose.Union;
import com.hp.hpl.jena.graph.impl.SimpleGraphMaker;
import com.hp.hpl.jena.sparql.util.graph.GraphListenerBase;
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
public abstract class PushGraphListener extends GraphListenerBase {
	
	private static GraphMaker graphMaker = new SimpleGraphMaker(); 

	/** The source graph. */
	protected Graph sourceGraph;
	
	/** The event manager. */
	protected GraphEventManager eventManager = null;

    //	protected boolean changed = false;
	

	/** The add events cache. */
	protected Graph addEvents = null;
	
	/** The delete events cache. */
	protected Graph deleteEvents = null;
	
	private Graph updatedGraph;

	//protected Map<Node, Map<Node, Map<Node,EventType>>> eventsTree = null;

	/**
	 * Instantiates a new push graph listener.
	 *
	 * @param sourceGraph the source graph
	 * @param eventManager the event manager
	 */
	public PushGraphListener(Graph sourceGraph, GraphEventManager eventManager) {
		this.sourceGraph = sourceGraph;
		this.eventManager = eventManager;
		updatedGraph = sourceGraph;
	}

	/**
	 * Instantiates a new push graph listener.
	 *
	 * @param sourceGraph the source graph
	 * @param eventManager the event manager
	 */
	public PushGraphListener(Graph sourceGraph) {
		this( sourceGraph, sourceGraph.getEventManager() );
	}
	
	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.util.graph.GraphListenerBase#addEvent(com.hp.hpl.jena.graph.Triple)
	 */
	@Override
	protected synchronized void addEvent(Triple t) {
		if (addEvents == null || deleteEvents == null)
			startRead();
		if (deleteEvents.contains(t))
			deleteEvents.delete(t);
		else if (!addEvents.contains(t))
			addEvents.add(t);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.util.graph.GraphListenerBase#deleteEvent(com.hp.hpl.jena.graph.Triple)
	 */
	@Override
	protected synchronized void deleteEvent(Triple t) {
		if (addEvents == null || deleteEvents == null)
			startRead();
		if (addEvents.contains(t))
			addEvents.delete(t);
		else if (!deleteEvents.contains(t))
			deleteEvents.add(t);
		//recordEvent(EventType.DELETE, t);
	}

	public Graph getUpdatedGraph() {
		return updatedGraph;
	}
	
	/**
	 * Notify events.
	 *
	 * @param add the add
	 * @param delete the delete
	 */
	/*
	private void notifyEvents(Iterator<Triple> add, Iterator<Triple> delete) {
		while (add.hasNext()) {
			notifyAdd(add.next());
		}
		while (delete.hasNext()) {
			notifyDelete(delete.next());
		}
	}
	*/

	/**
	 * Notify events.
	 *
	 * @param add the add
	 * @param delete the delete
	 */
	protected void notifyEvents() {

//		System.out.println(this + ": *** ADD EVENTS ******");
//		ModelFactory.createModelForGraph(addEvents).write(System.out,"N3");
//		System.out.println(this + ": *********************");
//		System.out.println(this + ": *** DELETE EVENTS ***");
//		ModelFactory.createModelForGraph(deleteEvents).write(System.out,"N3");
//		System.out.println(this + ": *********************");

		ExtendedIterator<Triple> addEventsIter = addEvents.find(Node.ANY, Node.ANY, Node.ANY);
		while (addEventsIter.hasNext()) {
			notifyAdd(addEventsIter.next());
		}
		ExtendedIterator<Triple> deleteEventsIter = deleteEvents.find(Node.ANY, Node.ANY, Node.ANY);
		while (deleteEventsIter.hasNext()) {
			notifyDelete(deleteEventsIter.next());
		}
	}

	/**
	 * Notify a triple add.
	 *
	 * @param t the triple added
	 */
	protected void notifyAdd(Triple t) {}
/*
	protected void notifyAdd(Triple t) {
		if (eventManager != null)
			eventManager.notifyAddTriple(sourceGraph, t);
	}
	*/
	
	/**
	 * Notify a triple delete.
	 *
	 * @param t the triple deleted
	 */
	protected void notifyDelete(Triple t) {}
	/*
	protected void notifyDelete(Triple t) {
		if (eventManager != null)
			eventManager.notifyDeleteTriple(sourceGraph, t);
	}
	*)

	/**
	 * It's called when an event block is finished, if
	 * there are events. If and only if it returns true the
	 * events are notified.
	 *
	 * @return true
	 */
	protected boolean changed() { return true; }

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.util.graph.GraphListenerBase#startRead()
	 */
	@Override
    protected synchronized void startRead() {
		//System.out.println("Begin of startRead() in " + this);
		addEvents = graphMaker.createGraph();
		deleteEvents = graphMaker.createGraph();
		updatedGraph = new Difference(new Union(sourceGraph, addEvents), deleteEvents);
		//System.out.println("End of startRead() in " + this);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.util.graph.GraphListenerBase#finishRead()
	 */
	@Override
    protected synchronized void finishRead() {
//		System.out.println("Begin of finishRead() in " + this);
		if ((!addEvents.isEmpty() || !deleteEvents.isEmpty()) && changed()) {
			if (eventManager != null)
				eventManager.notifyEvent(sourceGraph, GraphEvents.startRead);
			notifyEvents();
			if (eventManager != null)
				eventManager.notifyEvent(sourceGraph, GraphEvents.finishRead);
			updatedGraph = sourceGraph;
			addEvents = null;
			deleteEvents = null;
		}
//		System.out.println("End of finishRead() in " + this);
    }

}

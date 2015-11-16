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
package org.swows.tuio;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.GraphMaker;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.SimpleGraphMaker;
import org.apache.jena.util.iterator.Filter;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.runnable.RunnableContext;
import org.swows.runnable.RunnableContextFactory;
import org.swows.vocabulary.TUIO;
import org.swows.xmlinrdf.DomEventListener;
import org.w3c.dom.events.Event;

import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioPoint;
import TUIO.TuioTime;

public class TuioGateway implements TuioListener, DomEventListener {

	private static GraphMaker graphMaker = new SimpleGraphMaker(); 

    private TuioClient tuioClient;
    
    private DynamicGraphFromGraph tuioGraph;
    
    private Node tuioSourceNode;
    private Map<TuioPoint, Node> point2nodeMapping = new HashMap<TuioPoint, Node>();
    private Map<TuioPoint, Set<Node>> point2domNodesMapping = new HashMap<TuioPoint, Set<Node>>();
    private Set<Node> currPointDomNodesMapping = null;
    private boolean isReceiving = false;
    private Logger logger;
    private Vector<TuioListener> listenerList = new Vector<TuioListener>();
    
    @SuppressWarnings("unused")
	private RunnableContext runnableContext = null;
    // TODO: is runnableContext useful? the thread system must be reviewed
    
    private TimerTask localTimerTask = new TimerTask() {
		@Override
		public void run() {
			tuioGraph.sendUpdateEvents();
		}
	};
    
    private void startReceiving() {
    	isReceiving = true;
    }
    
    private void stopReceiving() {
    	if (isReceiving) {
    		RunnableContextFactory.getDefaultRunnableContext().run(localTimerTask);
//    		if (runnableContext != null)
//    			runnableContext.run(localTimerTask);
//    		else
//    			LocalTimer.get().schedule(localTimerTask, 0);
    	}
    	isReceiving = false;
    }
    
    /**
     * Adds the provided TuioListener to the list of registered TUIO event listeners
     *
     * @param  listener  the TuioListener to add
     */
    public void addTuioListener(TuioListener listener) {
    	listenerList.addElement(listener);
    }

    /**
     * Removes the provided TuioListener from the list of registered TUIO event listeners
     *
     * @param  listener  the TuioListener to remove
     */
    public void removeTuioListener(TuioListener listener) {	
    	listenerList.removeElement(listener);
    }

    /**
     * Removes all TuioListener from the list of registered TUIO event listeners
     */
    public void removeAllTuioListeners() {	
    	listenerList.clear();
    }

    private void setup() {
    	logger = Logger.getLogger(getClass());
		tuioClient.addTuioListener(new TuioSmoother(this));
    }
    
	public TuioGateway() {
		tuioClient = new TuioClient();
		setup();
	}

	public TuioGateway(int port) {
		tuioClient = new TuioClient(port);
		setup();
	}

	public TuioGateway(boolean autoRefesh) {
		tuioClient = new TuioClient(autoRefesh);
		setup();
	}

	public TuioGateway(int port, boolean autoRefesh) {
		tuioClient = new TuioClient(port,autoRefesh);
		setup();
	}
	
	public TuioGateway(RunnableContext runnableContext) {
		tuioClient = new TuioClient();
		this.runnableContext = runnableContext;
		setup();
	}

	public TuioGateway(int port, RunnableContext runnableContext) {
		tuioClient = new TuioClient(port);
		this.runnableContext = runnableContext;
		setup();
	}

	public TuioGateway(boolean autoRefesh, RunnableContext runnableContext) {
		tuioClient = new TuioClient(autoRefesh);
		this.runnableContext = runnableContext;
		setup();
	}

	public TuioGateway(int port, boolean autoRefesh, RunnableContext runnableContext) {
		tuioClient = new TuioClient(port,autoRefesh);
		this.runnableContext = runnableContext;
		setup();
	}
	
	

	public DynamicGraph getGraph() {
		if (tuioGraph == null) {
			tuioGraph = new DynamicGraphFromGraph( graphMaker.createGraph() );
			tuioSourceNode = TUIO.defaultSource.asNode();
			tuioGraph.add(new Triple(tuioSourceNode, RDF.type.asNode(), TUIO.Source.asNode()));
			tuioGraph.add(new Triple(tuioSourceNode, TUIO.updateTime.asNode(), T0));
			tuioGraph.sendUpdateEvents();
		}
		return tuioGraph;
	}
	
	public void connect() {
		tuioClient.connect();
	}
	
	@SuppressWarnings("unused")
	private static Node tuioTime2XSDduration(TuioTime tuioTime) {
		return NodeFactory.createLiteral(
				String.format("PT%d.%06dS", tuioTime.getSeconds(), tuioTime.getMicroseconds()),
				(String) null, XSDDatatype.XSDduration);
	}
	
	private static Node tuioTime2XSDdecimal(TuioTime tuioTime) {
		return NodeFactory.createLiteral(
				String.format("%d.%06d", tuioTime.getSeconds(), tuioTime.getMicroseconds()),
				(String) null, XSDDatatype.XSDdecimal);
	}
	
	private static float tuioTime2float(TuioTime tuioTime) {
		return (float) (tuioTime.getSeconds() + tuioTime.getMicroseconds() / 1E6);
	}
	
	private static final Node T0 =
			NodeFactory.createLiteral( "0", (String) null, XSDDatatype.XSDdecimal );
	
	private void updateDomNodes(TuioPoint point, Node tuioNode) {
		Set<Node> prevPointDomNodesMapping = point2domNodesMapping.get(point);
		if (prevPointDomNodesMapping == null) {
			for (Node currDomNode : currPointDomNodesMapping) {
				tuioGraph.add( new Triple(tuioNode, TUIO.isOn.asNode(), currDomNode));
			}
//			prevPointDomNodesMapping = new HashSet<Node>();
//			point2domNodesMapping.put(point, prevPointDomNodesMapping);
		} else {
			for (Node prevDomNode : prevPointDomNodesMapping) {
				if (!currPointDomNodesMapping.contains(prevDomNode))
					tuioGraph.delete( new Triple(tuioNode, TUIO.isOn.asNode(), prevDomNode));
			}
			for (Node currDomNode : currPointDomNodesMapping) {
				if (!prevPointDomNodesMapping.contains(currDomNode))
					tuioGraph.add( new Triple(tuioNode, TUIO.isOn.asNode(), currDomNode));
			}
		}
		point2domNodesMapping.put(point, currPointDomNodesMapping);
		currPointDomNodesMapping = null;
	}
	
	private Node addTuioPoint(TuioPoint point) {
		startReceiving();
		currPointDomNodesMapping = new HashSet<Node>();
		Node pointNode = NodeFactory.createURI(TUIO.getInstanceURI() + "point_" + point.hashCode());
		tuioGraph.add( new Triple( pointNode, RDF.type.asNode(), TUIO.Tracked.asNode() ) );
		tuioGraph.add( new Triple(pointNode, TUIO.source.asNode(), tuioSourceNode));
		//Node positionNode = Node.createAnon();
		Node positionNode = NodeFactory.createURI(TUIO.getInstanceURI() + "pointPosition_" + point.hashCode());
		Node xNode = NodeFactory.createLiteral(Float.toString(point.getX()), (String) null, XSDDatatype.XSDdecimal);
		Node yNode = NodeFactory.createLiteral(Float.toString(point.getY()), (String) null, XSDDatatype.XSDdecimal);
		tuioGraph.add( new Triple(pointNode, TUIO.position.asNode(), positionNode));
		tuioGraph.add( new Triple(positionNode, RDF.type.asNode(), TUIO.Point2D.asNode()));
		tuioGraph.add( new Triple(positionNode, TUIO.x.asNode(), xNode));
		tuioGraph.add( new Triple(positionNode, TUIO.y.asNode(), yNode));
		Node creationTimeNode =	tuioTime2XSDdecimal(point.getStartTime());
		tuioGraph.add( new Triple(pointNode, TUIO.creationTime.asNode(), creationTimeNode));
		Node updateTimeNode =	tuioTime2XSDdecimal(point.getTuioTime());
		tuioGraph.add( new Triple(pointNode, TUIO.updateTime.asNode(), updateTimeNode));
		point2nodeMapping.put(point, pointNode);
		return pointNode;
	}

	public synchronized void addTuioCursor(TuioCursor cursor) {
		logger.debug("Adding cursor " + cursor + " in TUIO gateway");
		Node cursorNode = addTuioPoint(cursor);
		tuioGraph.add( new Triple(cursorNode, RDF.type.asNode(), TUIO.Cursor.asNode()));
		for (int i=0;i<listenerList.size();i++) {
			TuioListener listener = (TuioListener) listenerList.elementAt(i);
			if (listener!=null) listener.addTuioCursor(cursor);
		}								
		updateDomNodes(cursor, cursorNode);
		logger.debug("Added cursor " + cursor + " (" + cursorNode + ") in TUIO gateway");
		//debugSubtreeExceptSource(cursorNode);
	}

	public synchronized void addTuioObject(TuioObject object) {
		logger.debug(
				"TUIO Gateway: adding object " + object
						+ " ( id: " + object.getSymbolID() + " x:" + object.getX() + ", y:" + object.getY() + ", angle:" + object.getAngle() + ")");
		Node objectNode = addTuioPoint(object);
		tuioGraph.add( new Triple(objectNode, RDF.type.asNode(), TUIO.Object.asNode()));
		Node angleNode = NodeFactory.createLiteral(Float.toString(object.getAngle()), (String) null, XSDDatatype.XSDdecimal);
		tuioGraph.add( new Triple(objectNode, TUIO.angle.asNode(), angleNode));
		Node symbolNode = NodeFactory.createLiteral(Integer.toString(object.getSymbolID()), (String) null, XSDDatatype.XSDinteger);
		tuioGraph.add( new Triple(objectNode, TUIO.markerId.asNode(), symbolNode));
		for (int i=0;i<listenerList.size();i++) {
			TuioListener listener = (TuioListener) listenerList.elementAt(i);
			if (listener!=null) listener.addTuioObject(object);
		}								
		updateDomNodes(object, objectNode);
		logger.debug("Added object " + object + " (" + objectNode + ") in TUIO gateway");
		//debugSubtreeExceptSource(objectNode);
	}

	private void debugTriple(Triple triple) {
		logger.trace(triple.getSubject() + " " + triple.getPredicate().getURI() + " " + triple.getObject());
	}
		
	@SuppressWarnings("unused")
	private void debugAllTracked() {
		logger.trace("Begin of tracked debugging");
		Iterator<Node> trackedIterator =
				tuioGraph.find(Node.ANY, RDF.type.asNode(), TUIO.Tracked.asNode())
				.mapWith(t -> t.getSubject());
		while (trackedIterator.hasNext()) {
			debugSubtreeExceptSource(trackedIterator.next());
		}
		logger.trace("End of tracked debugging");
	}
		
	private void debugSubtree(Node node, Filter<Triple> passThroughFilter) {
		logger.trace("Subtree of " + node + "...");
		Iterator<Triple> triples =
				tuioGraph.find(node, Node.ANY, Node.ANY);
		while (triples.hasNext()) {
			Triple triple = triples.next();
			if (passThroughFilter.accept(triple))
				debugSubtree(triple.getObject(), passThroughFilter);
			debugTriple(triple);
		}
		logger.trace("End of the Subtree of " + node);
	}
		
	private void debugSubtreeExceptSource(Node node) {
		debugSubtree(node, new Filter<Triple>() {
			@Override
			public boolean accept(Triple triple) {
				return !triple.getPredicate().equals(TUIO.source.asNode());
			}
		});
	}

	public synchronized void refresh(TuioTime time) {
		//debugAllTracked();
//		logger.debug(this + ": begin of refresh");
		startReceiving();
		changeObjectDecimal(tuioSourceNode, TUIO.updateTime.asNode(), tuioTime2float(time) );
//		changeObject(
//				tuioSourceNode,
//				TUIO.updateTime.asNode(),
//				tuioTime2XSDdecimal(time) );
//		debugAllTracked();
		stopReceiving();
//		logger.debug(this + ": end of refresh");
		//debugAllTracked();
	}

	private void deleteSubtree(Node node, Filter<Triple> passThroughFilter) {
		Iterator<Triple> triples =
				tuioGraph.find(node, Node.ANY, Node.ANY);
		List<Triple> tripleList = new Vector<Triple>();
		while (triples.hasNext())
			tripleList.add(triples.next());
		triples = tripleList.iterator();
		while (triples.hasNext()) {
			Triple triple = triples.next();
			if (passThroughFilter.accept(triple))
				deleteSubtree(triple.getObject(), passThroughFilter);
			tuioGraph.delete( triple);
		}
	}
		
	private void deleteSubtreeExceptSource(Node node) {
		deleteSubtree(node, new Filter<Triple>() {
			@Override
			public boolean accept(Triple triple) {
				return !triple.getPredicate().equals(TUIO.source.asNode());
			}
		});
	}

	private void removeTuioPoint(TuioPoint point) {
		startReceiving();
		currPointDomNodesMapping = new HashSet<Node>();
		deleteSubtreeExceptSource(point2nodeMapping.get(point));
		point2nodeMapping.remove(point);
	}

	public synchronized void removeTuioCursor(TuioCursor cursor) {
		logger.debug("Removing cursor " + cursor + " in TUIO gateway");
		removeTuioPoint(cursor);
		for (int i=0;i<listenerList.size();i++) {
			TuioListener listener = (TuioListener) listenerList.elementAt(i);
			if (listener!=null) listener.removeTuioCursor(cursor);
		}								
		logger.debug("Removed cursor " + cursor + " in TUIO gateway");
	}

	public synchronized void removeTuioObject(TuioObject object) {
		logger.debug("Removing object " + object + " in TUIO gateway");
		removeTuioPoint(object);
		for (int i=0;i<listenerList.size();i++) {
			TuioListener listener = (TuioListener) listenerList.elementAt(i);
			if (listener!=null) listener.removeTuioObject(object);
		}								
		logger.debug("Removed object " + object + " in TUIO gateway");
	}

	private void changeObjectDecimal(Node subject, Node predicate, float newObjectValue) {
		Triple oldTriple = tuioGraph.find(subject, predicate, Node.ANY).next();
		float oldObjectValue =
				Float.parseFloat( oldTriple.getObject().getLiteralLexicalForm() );
		if (newObjectValue != oldObjectValue) {
			Node newObject =
					NodeFactory.createLiteral(Float.toString(newObjectValue), (String) null, XSDDatatype.XSDdecimal);
			tuioGraph.add( new Triple(subject, predicate, newObject) );
			tuioGraph.delete(oldTriple);
		}
			
	}
	
	@SuppressWarnings("unused")
	private void changeObject(Node subject, Node predicate, Node newObject) {
		Triple oldTriple = tuioGraph.find(subject, predicate, Node.ANY).next();
		tuioGraph.add( new Triple(subject, predicate, newObject) );
		tuioGraph.delete(oldTriple);
	}
	
	private Node updateTuioPoint(TuioPoint point) {
		startReceiving();
		currPointDomNodesMapping = new HashSet<Node>();
		Node pointNode = point2nodeMapping.get(point);
		Node positionNode = tuioGraph.find(pointNode, TUIO.position.asNode(), Node.ANY).next().getObject();
		changeObjectDecimal(positionNode, TUIO.x.asNode(), point.getX());
		changeObjectDecimal(positionNode, TUIO.y.asNode(), point.getY());
		changeObjectDecimal(pointNode, TUIO.updateTime.asNode(), tuioTime2float(point.getTuioTime()) );
//		changeObject(
//				pointNode,
//				TUIO.updateTime.asNode(),
//				tuioTime2XSDdecimal(point.getTuioTime()) );
		return pointNode;
	}

	public synchronized void updateTuioCursor(TuioCursor cursor) {
		logger.debug(
				"TUIO Gateway: updating cursor " + cursor
				        //+ " " + point2nodeMapping.get(cursor)
						+ " ( x:" + cursor.getX() + ", y:" + cursor.getY() + ")");
		Node cursorNode = updateTuioPoint(cursor);
		for (int i=0;i<listenerList.size();i++) {
			TuioListener listener = (TuioListener) listenerList.elementAt(i);
			if (listener!=null) listener.updateTuioCursor(cursor);
		}								
		updateDomNodes(cursor, cursorNode);
		logger.debug(
				"TUIO Gateway: updated cursor " + cursor);
//						+ " " + point2nodeMapping.get(cursor));
	}

	public synchronized void updateTuioObject(TuioObject object) {
		logger.debug(
				"TUIO Gateway: updating object " + object
				        //+ " " + point2nodeMapping.get(cursor)
						+ " ( x:" + object.getX() + ", y:" + object.getY() + ", angle:" + object.getAngle() + ")");
		Node objectNode = updateTuioPoint(object);
		changeObjectDecimal(objectNode, TUIO.angle.asNode(), object.getAngle());
		for (int i=0;i<listenerList.size();i++) {
			TuioListener listener = (TuioListener) listenerList.elementAt(i);
			if (listener!=null) listener.updateTuioObject(object);
		}								
		updateDomNodes(object, objectNode);
		logger.debug(
				"TUIO Gateway: updated object " + object);
	}

	public void handleEvent(Event event, Node targetNode, Node currTargetNode) {
//		TuioEvent tuioEvent = (TuioEvent) event;
//		TuioPoint tuioPoint = tuioEvent.getTuioPoint();
		if (currPointDomNodesMapping != null)
			currPointDomNodesMapping.add(targetNode);
//		Set<Node> domNodes = point2domNodesMapping.get(tuioPoint);
//		if (domNodes == null) {
//			domNodes = new HashSet<Node>();
//			point2domNodesMapping.put(tuioPoint, domNodes);
//		}
//		domNodes.add(graphNode);
	}

}

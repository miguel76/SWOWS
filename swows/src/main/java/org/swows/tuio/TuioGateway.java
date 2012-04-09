package org.swows.tuio;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.runnable.LocalTimer;
import org.swows.vocabulary.TUIO;

import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioPoint;
import TUIO.TuioTime;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphMaker;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.SimpleGraphMaker;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.vocabulary.RDF;

public class TuioGateway implements TuioListener {

	private static GraphMaker graphMaker = new SimpleGraphMaker(); 

    private TuioClient tuioClient;
    
    private DynamicGraphFromGraph tuioGraph;
    
    private Node tuioSourceNode;
    private Map<TuioPoint, Node> point2nodeMapping = new HashMap<TuioPoint, Node>();
    private boolean isReceiving = false;
    private Logger logger;
    
    private void startReceiving() {
    	isReceiving = true;
    }
    
    private void stopReceiving() {
    	if (isReceiving) {
    		LocalTimer.get().schedule(new TimerTask() {
				@Override
				public void run() {
					tuioGraph.sendUpdateEvents();
				}
			}, 0);
    	}
    	isReceiving = false;
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

	public DynamicGraph getGraph() {
		if (tuioGraph == null) {
			tuioGraph = new DynamicGraphFromGraph( graphMaker.createGraph() );
			tuioSourceNode = TUIO.defaultSource.asNode();
			tuioGraph.add(new Triple(tuioSourceNode, RDF.type.asNode(), TUIO.Source.asNode()));
			tuioGraph.add(new Triple(tuioSourceNode, TUIO.updateTime.asNode(), T0));
		}
		return tuioGraph;
	}
	
	public void connect() {
		tuioClient.connect();
	}
	
	private static Node tuioTime2XSDduration(TuioTime tuioTime) {
		return Node.createLiteral(
				String.format("PT%d.%06dS", tuioTime.getSeconds(), tuioTime.getMicroseconds()),
				(String) null, XSDDatatype.XSDduration);
	}
	
	private static Node tuioTime2XSDdecimal(TuioTime tuioTime) {
		return Node.createLiteral(
				String.format("%d.%06d", tuioTime.getSeconds(), tuioTime.getMicroseconds()),
				(String) null, XSDDatatype.XSDdecimal);
	}
	
	private static float tuioTime2float(TuioTime tuioTime) {
		return (float) (tuioTime.getSeconds() + tuioTime.getMicroseconds() / 1E6);
	}
	
	private static final Node T0 =
			Node.createLiteral( "0", (String) null, XSDDatatype.XSDdecimal );
	
	private Node addTuioPoint(TuioPoint point) {
		startReceiving();
		Node pointNode = Node.createURI(TUIO.getInstanceURI() + "point_" + point.hashCode());
		tuioGraph.add( new Triple( pointNode, RDF.type.asNode(), TUIO.Tracked.asNode() ) );
		tuioGraph.add( new Triple(pointNode, TUIO.source.asNode(), tuioSourceNode));
		//Node positionNode = Node.createAnon();
		Node positionNode = Node.createURI(TUIO.getInstanceURI() + "pointPosition_" + point.hashCode());
		Node xNode = Node.createLiteral(Float.toString(point.getX()), (String) null, XSDDatatype.XSDdecimal);
		Node yNode = Node.createLiteral(Float.toString(point.getY()), (String) null, XSDDatatype.XSDdecimal);
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

	@Override
	public synchronized void addTuioCursor(TuioCursor cursor) {
		logger.debug("Adding cursor " + cursor + " in TUIO gateway");
		Node cursorNode = addTuioPoint(cursor);
		tuioGraph.add( new Triple(cursorNode, RDF.type.asNode(), TUIO.Cursor.asNode()));
		logger.debug("Added cursor " + cursor + " (" + cursorNode + ") in TUIO gateway");
		//debugSubtreeExceptSource(cursorNode);
	}

	@Override
	public synchronized void addTuioObject(TuioObject object) {
		logger.debug("Adding object " + object + " in TUIO gateway");
		Node objectNode = addTuioPoint(object);
		tuioGraph.add( new Triple(objectNode, RDF.type.asNode(), TUIO.Object.asNode()));
		Node angleNode = Node.createLiteral(Float.toString(object.getAngle()), (String) null, XSDDatatype.XSDdecimal);
		tuioGraph.add( new Triple(objectNode, TUIO.angle.asNode(), angleNode));
		logger.debug("Added object " + object + " (" + objectNode + ") in TUIO gateway");
		//debugSubtreeExceptSource(objectNode);
	}

	private void debugTriple(Triple triple) {
		logger.trace(triple.getSubject() + " " + triple.getPredicate().getURI() + " " + triple.getObject());
	}
		
	private void debugAllTracked() {
		logger.trace("Begin of tracked debugging");
		Iterator<Node> trackedIterator =
				tuioGraph.find(Node.ANY, RDF.type.asNode(), TUIO.Tracked.asNode())
				.mapWith(new Map1<Triple, Node>() {
					@Override
					public Node map1(Triple t) {
						return t.getSubject();
					}
				});
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

	@Override
	public synchronized void refresh(TuioTime time) {
		//debugAllTracked();
		logger.debug(this + ": begin of refresh");
		startReceiving();
		changeObjectDecimal(tuioSourceNode, TUIO.updateTime.asNode(), tuioTime2float(time) );
//		changeObject(
//				tuioSourceNode,
//				TUIO.updateTime.asNode(),
//				tuioTime2XSDdecimal(time) );
//		debugAllTracked();
		stopReceiving();
		logger.debug(this + ": end of refresh");
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
		deleteSubtreeExceptSource(point2nodeMapping.get(point));
		point2nodeMapping.remove(point);
	}

	@Override
	public synchronized void removeTuioCursor(TuioCursor cursor) {
		logger.debug("Removing cursor " + cursor + " in TUIO gateway");
		removeTuioPoint(cursor);
		logger.debug("Removed cursor " + cursor + " in TUIO gateway");
	}

	@Override
	public synchronized void removeTuioObject(TuioObject object) {
		logger.debug("Removing object " + object + " in TUIO gateway");
		removeTuioPoint(object);
		logger.debug("Removed object " + object + " in TUIO gateway");
	}

	private void changeObjectDecimal(Node subject, Node predicate, float newObjectValue) {
		Triple oldTriple = tuioGraph.find(subject, predicate, Node.ANY).next();
		float oldObjectValue =
				Float.parseFloat( oldTriple.getObject().getLiteralLexicalForm() );
		if (newObjectValue != oldObjectValue) {
			Node newObject =
					Node.createLiteral(Float.toString(newObjectValue), (String) null, XSDDatatype.XSDdecimal);
			tuioGraph.add( new Triple(subject, predicate, newObject) );
			tuioGraph.delete(oldTriple);
		}
			
	}
	
	private void changeObject(Node subject, Node predicate, Node newObject) {
		Triple oldTriple = tuioGraph.find(subject, predicate, Node.ANY).next();
		tuioGraph.add( new Triple(subject, predicate, newObject) );
		tuioGraph.delete(oldTriple);
	}
	
	private Node updateTuioPoint(TuioPoint point) {
		startReceiving();
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

	@Override
	public synchronized void updateTuioCursor(TuioCursor cursor) {
		logger.debug(
				"TUIO Gateway: updating cursor " + cursor
				        //+ " " + point2nodeMapping.get(cursor)
						+ " ( x:" + cursor.getX() + ", y:" + cursor.getY() + ")");
		updateTuioPoint(cursor);
		logger.debug(
				"TUIO Gateway: updated cursor " + cursor);
//						+ " " + point2nodeMapping.get(cursor));
	}

	@Override
	public synchronized void updateTuioObject(TuioObject object) {
		logger.debug(
				"TUIO Gateway: updating object " + object
				        //+ " " + point2nodeMapping.get(cursor)
						+ " ( x:" + object.getX() + ", y:" + object.getY() + ")");
		Node objectNode = updateTuioPoint(object);
		changeObjectDecimal(objectNode, TUIO.angle.asNode(), object.getAngle());
		logger.debug(
				"TUIO Gateway: updated object " + object);
	}

}

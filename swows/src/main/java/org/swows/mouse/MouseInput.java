package org.swows.mouse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.runnable.LocalTimer;
import org.swows.runnable.RunnableContext;
import org.swows.runnable.RunnableContextFactory;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DOMEvents;
import org.swows.vocabulary.TUIO;
import org.swows.xmlinrdf.DomEventListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.MouseEvent;

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
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.vocabulary.RDF;

public class MouseInput implements DomEventListener {

	private static GraphMaker graphMaker = new SimpleGraphMaker(); 

    private DynamicGraphFromGraph mouseEventGraph;
    
    private boolean isReceiving = false;
    private Logger logger = Logger.getLogger(getClass());
    
    private RunnableContext runnableContext = null;
    
    private Map<MouseEvent,Set<Node>> event2domNodes = new HashMap<MouseEvent, Set<Node>>();
    
    private TimerTask localTimerTask = new TimerTask() {
		@Override
		public void run() {
			mouseEventGraph.sendUpdateEvents();
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
    
	public DynamicGraph getGraph() {
		if (mouseEventGraph == null) {
			mouseEventGraph = new DynamicGraphFromGraph( graphMaker.createGraph() );
		}
		return mouseEventGraph;
	}
	
	@Override
	public void handleEvent(Event event, Node graphNode) {
		MouseEvent mouseEvent = (MouseEvent) event;
		Set<Node> domNodes = event2domNodes.get(mouseEvent);
		if (event.getTarget() instanceof Element) {
			if (domNodes == null) {
				domNodes = new HashSet<Node>();
				event2domNodes.put(mouseEvent, domNodes);
			}
			domNodes.add(graphNode);
		} else if (event.getTarget() instanceof Document) {
			
			Node eventNode = Node.createURI(DOMEvents.getInstanceURI() + "event_" + event.hashCode());
			mouseEventGraph.add( new Triple( eventNode, RDF.type.asNode(), DOMEvents.Event.asNode() ) );
			mouseEventGraph.add( new Triple( eventNode, RDF.type.asNode(), DOMEvents.UIEvent.asNode() ) );
			mouseEventGraph.add( new Triple( eventNode, RDF.type.asNode(), DOMEvents.MouseEvent.asNode() ) );

			for (Node targetNode : domNodes)
				mouseEventGraph.add( new Triple( eventNode, DOMEvents.target.asNode(), targetNode ));

			GraphUtils.addIntegerProperty(
					mouseEventGraph, eventNode,
					DOMEvents.timeStamp.asNode(), event.getTimeStamp());
			
			GraphUtils.addIntegerProperty(
					mouseEventGraph, eventNode,
					DOMEvents.detail.asNode(), mouseEvent.getDetail());
			
//		    public static final Property target = property( "target" );
//		    public static final Property currentTarget = property( "currentTarget" );

//		    public static final Property button = property( "button" );
//		    public static final Property relatedTarget = property( "relatedTarget" );
			
			GraphUtils.addDecimalProperty(
					mouseEventGraph, eventNode,
					DOMEvents.screenX.asNode(), mouseEvent.getScreenX());
			GraphUtils.addDecimalProperty(
					mouseEventGraph, eventNode,
					DOMEvents.screenY.asNode(), mouseEvent.getScreenY());
			GraphUtils.addDecimalProperty(
					mouseEventGraph, eventNode,
					DOMEvents.clientX.asNode(), mouseEvent.getClientX());
			GraphUtils.addDecimalProperty(
					mouseEventGraph, eventNode,
					DOMEvents.clientY.asNode(), mouseEvent.getClientY());

			GraphUtils.addBooleanProperty(
					mouseEventGraph, eventNode,
					DOMEvents.ctrlKey.asNode(), mouseEvent.getCtrlKey());
			GraphUtils.addBooleanProperty(
					mouseEventGraph, eventNode,
					DOMEvents.shiftKey.asNode(), mouseEvent.getShiftKey());
			GraphUtils.addBooleanProperty(
					mouseEventGraph, eventNode,
					DOMEvents.altKey.asNode(), mouseEvent.getAltKey());
			GraphUtils.addBooleanProperty(
					mouseEventGraph, eventNode,
					DOMEvents.metaKey.asNode(), mouseEvent.getMetaKey());

			GraphUtils.addIntegerProperty(
					mouseEventGraph, eventNode,
					DOMEvents.button.asNode(), mouseEvent.getButton());

		}
	}

}

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
package org.swows.mouse;

import java.util.TimerTask;

import org.apache.jena.graph.GraphMaker;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.SimpleGraphMaker;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.runnable.LocalTimer;
import org.swows.runnable.RunnableContextFactory;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DOMEvents;
import org.swows.xmlinrdf.DomEventListener;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.MouseEvent;

public class MouseInput implements DomEventListener {

	private static GraphMaker graphMaker = new SimpleGraphMaker(); 

    private DynamicGraphFromGraph mouseEventGraph;
    
//    private boolean isReceiving = false;
//    private Logger logger = Logger.getLogger(getClass());
//    
//    private RunnableContext runnableContext = null;
    
//    private Map<MouseEvent,Set<Node>> event2domNodes = new HashMap<MouseEvent, Set<Node>>();
    
	private Logger logger = Logger.getRootLogger();
	
//    private TimerTask localTimerTask = new TimerTask() {
//		@Override
//		public void run() {
//			logger.debug("Sending update events ... ");
//			mouseEventGraph.sendUpdateEvents();
//			logger.debug("Update events sent!");
//		}
//	};
    
//    private void startReceiving() {
//    	isReceiving = true;
//    }
//    
//    private void stopReceiving() {
//    	if (isReceiving) {
//    		RunnableContextFactory.getDefaultRunnableContext().run(localTimerTask);
////    		if (runnableContext != null)
////    			runnableContext.run(localTimerTask);
////    		else
////    			LocalTimer.get().schedule(localTimerTask, 0);
//    	}
//    	isReceiving = false;
//    }
//    

	public void buildGraph() {
		if (mouseEventGraph == null) {
			mouseEventGraph = new DynamicGraphFromGraph( graphMaker.createGraph() );
		} else {
			mouseEventGraph.clear();
		}
	}
	
	public synchronized DynamicGraph getGraph() {
		buildGraph();
		return mouseEventGraph;
	}
	
	public synchronized void handleEvent(Event event, Node currentTargetGraphNode , Node targetGraphNode ) {
//		logger.debug("In " + this + " received event " + event);
		MouseEvent mouseEvent = (MouseEvent) event;
//		Set<Node> domNodes = event2domNodes.get(mouseEvent);
////		logger.debug("domNodes: " + domNodes);
//		if (event.getCurrentTarget() instanceof Element) {
//			if (domNodes == null) {
//				domNodes = new HashSet<Node>();
//				event2domNodes.put(mouseEvent, domNodes);
//			}
//			domNodes.add(graphNode);
//		} else if (event.getCurrentTarget() instanceof Document && domNodes != null) {
			
			buildGraph();
			

			Node eventNode = NodeFactory.createURI(DOMEvents.getInstanceURI() + "event_" + event.hashCode());
			mouseEventGraph.add( new Triple( eventNode, RDF.type.asNode(), DOMEvents.Event.asNode() ) );
			mouseEventGraph.add( new Triple( eventNode, RDF.type.asNode(), DOMEvents.UIEvent.asNode() ) );
			mouseEventGraph.add( new Triple( eventNode, RDF.type.asNode(), DOMEvents.MouseEvent.asNode() ) );

			mouseEventGraph.add( new Triple( eventNode, DOMEvents.currentTarget.asNode(), currentTargetGraphNode ));
			mouseEventGraph.add( new Triple( eventNode, DOMEvents.target.asNode(), targetGraphNode ));
//			for (Node targetNode : domNodes)
//				mouseEventGraph.add( new Triple( eventNode, DOMEvents.target.asNode(), targetNode ));

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

			logger.debug("Launching update thread... ");
			LocalTimer.get().schedule(
					new TimerTask() {
						@Override
						public void run() {
							RunnableContextFactory.getDefaultRunnableContext().run(
									new Runnable() {
										public void run() {
											logger.debug("Sending update events ... ");
											mouseEventGraph.sendUpdateEvents();
											logger.debug("Update events sent!");
										}
									} );
						}
					}, 0 );
			logger.debug("Update thread launched!");
//		}
	}

}

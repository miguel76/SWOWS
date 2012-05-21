package org.swows.mouse;

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
import org.swows.runnable.RunnableContext;
import org.swows.runnable.RunnableContextFactory;
import org.swows.vocabulary.TUIO;
import org.swows.xmlinrdf.DomEventListener;
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
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.vocabulary.RDF;

public class MouseInput implements DomEventListener {

	private static GraphMaker graphMaker = new SimpleGraphMaker(); 

    private DynamicGraphFromGraph mouseEventGraph;
    
    private boolean isReceiving = false;
    private Logger logger = Logger.getLogger(getClass());
    
    private RunnableContext runnableContext = null;
    
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
		// TODO: set graph with mouse event info
	}

}

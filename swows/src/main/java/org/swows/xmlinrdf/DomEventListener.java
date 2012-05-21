package org.swows.xmlinrdf;

import org.w3c.dom.events.Event;

import com.hp.hpl.jena.graph.Node;

public interface DomEventListener {
	
	public void handleEvent(Event event, Node graphNode );

}

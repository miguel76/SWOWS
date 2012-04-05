package org.swows.graph.events;

import com.hp.hpl.jena.graph.Graph;

public interface DynamicGraph extends Graph {
	
	public EventManager getEventManager2();

}

package org.swows.xmlinrdf;

import org.w3c.dom.Node;
import org.w3c.dom.events.EventListener;

public interface EventManager {

	public void addEventListener(
			com.hp.hpl.jena.graph.Node nodeTarget,
			Node target,
			String type,
			EventListener listener,
			boolean useCapture);

	public void removeEventListener(
			com.hp.hpl.jena.graph.Node nodeTarget,
			Node target,
			String type,
			EventListener listener,
			boolean useCapture);

}

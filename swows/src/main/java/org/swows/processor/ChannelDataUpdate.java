package org.swows.processor;

import org.swows.graph.events.GraphUpdate;

public interface ChannelDataUpdate {
	
	public GraphUpdate asGraphUpdate();

}

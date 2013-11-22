package org.swows.processor;

import org.swows.graph.events.GraphUpdate;

public class ChannelDataGraphUpdate implements ChannelDataUpdate {
	
	private GraphUpdate graphUpdate;
	
	public ChannelDataGraphUpdate(GraphUpdate graphUpdate) {
		this.graphUpdate = graphUpdate;
	}

	@Override
	public GraphUpdate asGraphUpdate() {
		return graphUpdate;
	}

}

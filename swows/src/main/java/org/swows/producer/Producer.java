package org.swows.producer;

import org.swows.graph.events.DynamicDataset;

public interface Producer<T> {
	
	public T create(DynamicDataset inputDataset);

}

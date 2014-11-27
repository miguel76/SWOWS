package org.swows.producer;

import org.swows.graph.events.DynamicDataset;

public class ConstantProducerFactory<T> {

	public Producer<T> createProducer(final T constant) {
		return new Producer<T>() {

			@Override
			public T create(DynamicDataset inputDataset) {
				return constant;
			}
		};
	}
	
}

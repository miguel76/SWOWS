package org.swows.producer;

import org.swows.source.DatasetSource;

public class ConstantProducerFactory<T> {

	public Producer<T> createProducer(final T constant) {
		return new Producer<T>() {

			@Override
			public T create(DatasetSource inputDatasetSource) {
				return constant;
			}
		};
	}
	
}

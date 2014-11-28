package org.swows.producer;

import org.swows.source.DatasetSource;

public interface Producer<T> {
	
	public T create(DatasetSource inputDatasetSource);

}

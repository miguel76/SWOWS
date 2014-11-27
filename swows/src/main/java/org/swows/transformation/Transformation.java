package org.swows.transformation;

import org.swows.source.DatasetSource;

public interface Transformation {
	
	public DatasetSource apply(DatasetSource inputDataset);

}

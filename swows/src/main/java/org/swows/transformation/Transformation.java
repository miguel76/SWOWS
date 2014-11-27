package org.swows.transformation;

import com.hp.hpl.jena.sparql.core.DatasetGraph;

public interface Transformation {
	
	public DatasetGraph apply(DatasetGraph inputDataset);

}

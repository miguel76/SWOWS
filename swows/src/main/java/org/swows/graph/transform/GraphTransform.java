package org.swows.graph.transform;

import com.hp.hpl.jena.sparql.core.DatasetGraph;

public interface GraphTransform {
	
//	public boolean isQuery();
//	public Query getQuery();
//	
//	public boolean isUpdateRequest();
//	public UpdateRequest getUpdateRequest();
	
	public DatasetGraph apply(DatasetGraph inputDataset);

}

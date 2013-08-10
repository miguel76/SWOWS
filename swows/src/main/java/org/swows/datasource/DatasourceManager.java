package org.swows.datasource;

import org.apache.jena.riot.system.StreamRDF;

public interface DatasourceManager {

	public void toStreamRDF(Datasource ds, StreamRDF streamRDF);
	
}

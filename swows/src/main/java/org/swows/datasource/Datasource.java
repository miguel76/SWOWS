package org.swows.datasource;

import org.apache.jena.riot.system.StreamRDF;

public interface Datasource {
	
	public DatasourceFactory getDatasourceFactory();
	public DatasourceManager getDatasourceManager();
	void toStreamRDF(StreamRDF streamRDF);
	
}

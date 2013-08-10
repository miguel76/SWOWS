package org.swows.datasource;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.riot.system.StreamRDF;

public class DatasourceManagerImpl implements DatasourceManager {
	
	private Map<Datasource, StreamRDF> openStreams = new HashMap<Datasource, StreamRDF>();

	@Override
	public void toStreamRDF(Datasource ds, StreamRDF streamRDF) {
		if (openStreams.containsKey(ds))
			return openStreams.get(ds);
	}

}

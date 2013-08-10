package org.swows.datasource;

import java.util.Set;

public interface DatasourceFactory {

	public Datasource union(Set<Datasource> dsSet);

}

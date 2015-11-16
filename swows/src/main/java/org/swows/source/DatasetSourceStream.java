package org.swows.source;

import org.apache.jena.sparql.core.DatasetGraph;

public class DatasetSourceStream extends DatasetSourceFromDatasets {
	
	public synchronized void inject(DatasetGraph newDataset) {
		setNewDataset(newDataset);
	}
	
	@Override
	public synchronized void advance() {
		setNewDataset(EMPTY_DATASET);
	}

	@Override
	protected void readyForExecution() { }

}

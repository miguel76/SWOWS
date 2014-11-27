package org.swows.source;

import com.hp.hpl.jena.sparql.core.DatasetGraph;

public class DatasetSourceTemporal extends DatasetSourceFromDatasets {
	
	public DatasetSourceTemporal() { }

	public DatasetSourceTemporal(DatasetGraph initialDataset) {
		lastDataset = initialDataset;
	}
	
	public synchronized void change(DatasetGraph newDataset) {
		setNewDataset(newDataset);
	}
	
	@Override
	public synchronized void advance() {
		setNewDataset(prevDataset);
	}

	@Override
	protected void readyForExecution() { }

}

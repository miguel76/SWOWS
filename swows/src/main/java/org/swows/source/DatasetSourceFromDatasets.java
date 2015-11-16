package org.swows.source;

import org.apache.jena.sparql.core.DatasetGraph;

public abstract class DatasetSourceFromDatasets extends DatasetSourceBase {

	public DatasetSourceFromDatasets() {
		super();
	}

	public DatasetSourceFromDatasets(DatasetGraph initialDataset) {
		super(initialDataset);
	}

	@Override
	public synchronized DatasetGraph lastDataset() {
		return lastDataset;
	}

	@Override
	public synchronized DatasetChanges changesFromPrevDataset() {
		if (changesFromPrev == null)
			changesFromPrev = DatasetChangesFactory.fromDatasetDiff(prevDataset, lastDataset);
		return changesFromPrev;
	}
	
	protected void setNewDataset(DatasetGraph newDataset) {
		waitForDelivery();
		synchronized (this) {
			lastDataset = newDataset;
			if (lastDataset == newDataset) {
				changesFromPrev = DatasetChanges.EMPTY_CHANGES;
				hasChanged = false;
			} else {
				changesFromPrev = null;
				hasChanged = true;
			}
		}
		notifyAdvanced();
	}

	@Override
	protected void allSnapshotListenersReady() { }

	@Override
	protected void allChangesListenersReady() {
		changesFromPrev = null;
	}

	@Override
	protected void allListenersReady() {
		prevDataset = lastDataset;
		lastDataset = null;
		super.allListenersReady();	
	}


}

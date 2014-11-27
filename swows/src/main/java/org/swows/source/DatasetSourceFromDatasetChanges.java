package org.swows.source;

import java.util.Iterator;

import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Quad;

public abstract class DatasetSourceFromDatasetChanges extends DatasetSourceBase {
	
	private void updateDataset() {
		Iterator<Quad> addedIterator = changesFromPrev.getAddedAsIterator();
		while (addedIterator.hasNext())
			lastDataset.add(addedIterator.next());
		Iterator<Quad> deletedIterator = changesFromPrev.getDeletedAsIterator();
		while (deletedIterator.hasNext())
			lastDataset.delete(deletedIterator.next());
	}
	
	protected synchronized void applyChanges(
			DatasetChanges changes, DatasetGraph newDataset) {
		waitForDelivery();
		changesFromPrev = changes;
		if (	changesFromPrev == DatasetChanges.EMPTY_CHANGES
				|| changesFromPrev.isEmpty()) {
			changesFromPrev = DatasetChanges.EMPTY_CHANGES;
			hasChanged = false;
		} else {
			if (!snapshotListeners.isEmpty()) {
				if (newDataset != null)
					lastDataset = newDataset;
				else
					updateDataset();
			}
			hasChanged = true;
		}
		notifyAdvanced();
	}

	@Override
	public synchronized DatasetGraph lastDataset() {
		return lastDataset;
	}

	@Override
	public synchronized DatasetChanges changesFromPrevDataset() {
		return changesFromPrev;
	}

	@Override
	protected void allSnapshotListenersReady() { }

	@Override
	protected void allChangesListenersReady() {
		changesFromPrev = null;
	}

}

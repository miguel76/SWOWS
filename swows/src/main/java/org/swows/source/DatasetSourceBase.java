package org.swows.source;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.sparql.core.DatasetGraph;

public abstract class DatasetSourceBase implements DatasetSource, DatasetSourceListener {
	
	protected boolean advanceRequested = false;
	protected Set<DatasetSourceListener> snapshotListeners = new HashSet<DatasetSourceListener>();
	protected Set<DatasetSourceListener> readySnapshotListeners = new HashSet<DatasetSourceListener>();
	protected Set<DatasetSourceListener> changesListeners = new HashSet<DatasetSourceListener>();
	protected Set<DatasetSourceListener> readyChangesListeners = new HashSet<DatasetSourceListener>();

	protected DatasetGraph lastDataset = DatasetSource.EMPTY_DATASET;
	protected DatasetGraph prevDataset = DatasetSource.EMPTY_DATASET;
	protected DatasetChanges changesFromPrev = DatasetChanges.EMPTY_CHANGES;
	
	protected boolean hasChanged = false;
	protected boolean delivered = true;
	
	protected abstract void allSnapshotListenersReady();
	protected abstract void allChangesListenersReady();
//	protected abstract void allListenersReady();
	
	protected DatasetSourceBase() {}
	
	protected DatasetSourceBase(DatasetGraph initialDataset) {
		lastDataset = initialDataset;
	}
	
	protected void allListenersReady() {
		delivered = true;
	}

	protected void waitForDelivery() {
		while (!delivered);
	}
	
	@Override
	public synchronized void currNotNeeded(DatasetSourceListener l) {
		if (snapshotListeners.contains(l)) {
			readySnapshotListeners.add(l);
			if (readySnapshotListeners.size() == snapshotListeners.size())
				allSnapshotListenersReady();
		}
		if (changesListeners.contains(l)) {
			readyChangesListeners.add(l);
			if (readyChangesListeners.size() == changesListeners.size())
				allChangesListenersReady();
		}
		if (	readySnapshotListeners.size() == snapshotListeners.size()
				&& readyChangesListeners.size() == changesListeners.size() )
			allListenersReady();
	}
	
	@Override
	public void advance() {
		advanceRequested = true;
	}

	@Override
	public synchronized void registerSnapshotListener(DatasetSourceListener l) {
		snapshotListeners.add(l);
	}

	@Override
	public synchronized void unregisterSnapshotListener(DatasetSourceListener l) {
		snapshotListeners.remove(l);
		readySnapshotListeners.remove(l);
	}
	
	@Override
	public synchronized void registerChangesListener(DatasetSourceListener l) {
		changesListeners.add(l);
	}

	@Override
	public synchronized void unregisterChangesListener(DatasetSourceListener l) {
		changesListeners.remove(l);
		readyChangesListeners.remove(l);
	}
	
	protected void notifyAdvanced() {
		for (DatasetSourceListener l: snapshotListeners) {
			l.advanced(this);
			readySnapshotListeners.remove(l);
		}
		for (DatasetSourceListener l: changesListeners) {
			l.advanced(this);
			readyChangesListeners.remove(l);
		}
	}

	@Override
	public boolean hasChanged() {
		return hasChanged;
	}
	
	protected Set<DatasetSource> sources = new HashSet<DatasetSource>();
	protected Set<DatasetSource> readySources = new HashSet<DatasetSource>();
	
	protected synchronized void registerAsSnapshotListenerTo(DatasetSource source) {
		source.registerSnapshotListener(this);
		sources.add(source);
	}
	
	protected synchronized void registerAsChangesListenerTo(DatasetSource source) {
		source.registerChangesListener(this);
		sources.add(source);
	}
	
	protected synchronized void unregisterAsListenerTo(DatasetSource source) {
		source.unregisterSnapshotListener(this);
		source.unregisterChangesListener(this);
		sources.remove(source);
	}
	
	@Override
	public void advanced(DatasetSource s) {
		if (sources.contains(s)) {
			readySources.add(s);
			if (readySources.size() == sources.size())
				allSourcesAdvanced();
			else if (delivered)
				waitForSources();
		}
	}

	protected void waitForSources() {
		for (DatasetSource source: sources) {
			synchronized (this) {
				if (!readySources.contains(source))
					source.advance();
			}
		}
	}
	
	protected void allSourcesAdvanced() {
		waitForDelivery();
		readyForExecution();
	}

	protected abstract void readyForExecution();

}

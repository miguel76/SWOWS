package org.swows.source;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;

public interface DatasetSource {

	public static final DatasetGraph EMPTY_DATASET =
			DatasetGraphFactory.createOneGraph(Graph.emptyGraph);

	public DatasetGraph lastDataset();
	public DatasetChanges changesFromPrevDataset();
	public void currNotNeeded(DatasetSourceListener l);
	public void advance();
	public boolean hasChanged();
	public void registerSnapshotListener(DatasetSourceListener l);
	public void unregisterSnapshotListener(DatasetSourceListener l);
	public void registerChangesListener(DatasetSourceListener l);
	public void unregisterChangesListener(DatasetSourceListener l);
	
}

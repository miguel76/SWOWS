package org.swows.source;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Quad;

public interface DatasetChanges {
	
	public final static DatasetChanges EMPTY_CHANGES =
			new DatasetChanges() {
				
				@Override
				public Set<Quad> getDeletedAsSet() {
					return Collections.emptySet();
				}
				
				@Override
				public Iterator<Quad> getDeletedAsIterator() {
					return Collections.emptyIterator();
				}
				
				@Override
				public DatasetGraph getDeletedAsDataset() {
					return DatasetSource.EMPTY_DATASET;
				}
				
				@Override
				public Set<Quad> getAddedAsSet() {
					return Collections.emptySet();
				}
				
				@Override
				public Iterator<Quad> getAddedAsIterator() {
					return Collections.emptyIterator();
				}
				
				@Override
				public DatasetGraph getAddedAsDataset() {
					return DatasetSource.EMPTY_DATASET;
				}

				@Override
				public boolean isEmpty() {
					return true;
				}
	};
	
	public boolean isEmpty();
			
	public DatasetGraph getAddedAsDataset();
	public DatasetGraph getDeletedAsDataset();

	public Set<Quad> getAddedAsSet();
	public Set<Quad> getDeletedAsSet();
	
	public Iterator<Quad> getAddedAsIterator();
	public Iterator<Quad> getDeletedAsIterator();
	
}

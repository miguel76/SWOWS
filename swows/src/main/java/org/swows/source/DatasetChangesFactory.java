package org.swows.source;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Quad;

public class DatasetChangesFactory {
	
	private static class FromSets implements DatasetChanges {
	
		Set<Quad> addedQuads, deletedQuads;
		DatasetGraph addedDataset = null, deletedDataset = null;
		
		public FromSets(Set<Quad> addedQuads, Set<Quad> deletedQuads) {
			this.addedQuads = addedQuads;
			this.deletedQuads = deletedQuads;
		}	

		@Override
		public DatasetGraph getAddedAsDataset() {
			if (addedDataset == null) {
				addedDataset = DatasetGraphFactory.createMem();
				for (Quad quad : addedQuads) addedDataset.add(quad);
			}
			return addedDataset;
		}

		@Override
		public DatasetGraph getDeletedAsDataset() {
			if (deletedDataset == null) {
				deletedDataset = DatasetGraphFactory.createMem();
				for (Quad quad : deletedQuads) deletedDataset.add(quad);
			}
			return deletedDataset;
		}

		@Override
		public Set<Quad> getAddedAsSet() {
			return addedQuads;
		}

		@Override
		public Set<Quad> getDeletedAsSet() {
			return deletedQuads;
		}

		@Override
		public Iterator<Quad> getAddedAsIterator() {
			return addedQuads.iterator();
		}

		@Override
		public Iterator<Quad> getDeletedAsIterator() {
			return deletedQuads.iterator();
		}

		@Override
		public boolean isEmpty() {
			return addedQuads.isEmpty() && deletedQuads.isEmpty();
		}

	}
	
	private static class FromDatasets implements DatasetChanges {
		
		Set<Quad> addedQuads = null, deletedQuads = null;
		DatasetGraph addedDataset, deletedDataset;
		
		public FromDatasets(DatasetGraph addedDataset, DatasetGraph deletedDataset) {
			this.addedDataset = addedDataset;
			this.deletedDataset = deletedDataset;
		}	

		@Override
		public DatasetGraph getAddedAsDataset() {
			return addedDataset;
		}

		@Override
		public DatasetGraph getDeletedAsDataset() {
			return deletedDataset;
		}

		@Override
		public Set<Quad> getAddedAsSet() {
			if (addedQuads == null) {
				addedQuads = new HashSet<Quad>();
				Iterator<Quad> iter = getAddedAsIterator();
				while (iter.hasNext()) 
					addedQuads.add(iter.next());
			}
			return addedQuads;
		}

		@Override
		public Set<Quad> getDeletedAsSet() {
			if (deletedQuads == null) {
				deletedQuads = new HashSet<Quad>();
				Iterator<Quad> iter = getDeletedAsIterator();
				while (iter.hasNext()) 
					deletedQuads.add(iter.next());
			}
			return deletedQuads;
		}

		@Override
		public Iterator<Quad> getAddedAsIterator() {
			return addedDataset.find();
		}

		@Override
		public Iterator<Quad> getDeletedAsIterator() {
			return deletedDataset.find();
		}

		@Override
		public boolean isEmpty() {
			return addedDataset.isEmpty() && deletedDataset.isEmpty();
		}

	}
	
	public static DatasetChanges fromSets(Set<Quad> addedQuads, Set<Quad> deletedQuads) {
		return new FromSets(addedQuads, deletedQuads);
	}
	
	public static DatasetChanges fromDatasets(
			DatasetGraph addedDataset, DatasetGraph deletedDataset) {
		return new FromDatasets(addedDataset, deletedDataset);
	}
	
	public static DatasetChanges fromDatasetDiff(
			DatasetGraph prevDataset, DatasetGraph newDataset) {
		if (prevDataset == DatasetSource.EMPTY_DATASET || prevDataset.isEmpty())
			return new FromDatasets(newDataset, DatasetSource.EMPTY_DATASET);
		if (newDataset == DatasetSource.EMPTY_DATASET || newDataset.isEmpty())
			return new FromDatasets(DatasetSource.EMPTY_DATASET, prevDataset);
		Set<Quad> addedQuads = new HashSet<Quad>(), deletedQuads = new HashSet<Quad>();
		Iterator<Quad> newIter = newDataset.find();
		while (newIter.hasNext()) {
			Quad quad = newIter.next();
			if (!prevDataset.contains(quad))
				addedQuads.add(quad);
		}
		Iterator<Quad> oldIter = newDataset.find();
		while (oldIter.hasNext()) {
			Quad quad = oldIter.next();
			if (!newDataset.contains(quad))
				deletedQuads.add(quad);
		}
		return new FromSets(addedQuads, deletedQuads);
	}
	
	
}

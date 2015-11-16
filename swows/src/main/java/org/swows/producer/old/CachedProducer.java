/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open datatafloW System (SWOWS).

 * SWOWS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * SWOWS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General
 * Public License along with SWOWS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.swows.producer.old;

import java.util.HashMap;
import java.util.Map;

import org.swows.graph.SingleGraphDataset;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;

/**
 * The Class CachedProducer connects to another producer
 * caching created graphs or datasets.
 * Cached data is maintained in association with the
 * corresponding input datasets. 
 * It's never used directly in a dataflow so doesn't
 * implement the standard Producer constructor.
 */
public class CachedProducer implements RDFProducer {

	/** The connected producer. */
	private RDFProducer connectedProducer;

	/** The cached graphs. */
	private Map<DynamicDataset,DynamicGraph> cachedGraphs = new HashMap<DynamicDataset, DynamicGraph>();
	
	/** The cached datasets. */
	private Map<DynamicDataset,DynamicDataset> cachedDatasets = new HashMap<DynamicDataset, DynamicDataset>();

	/**
	 * Instantiates a new cached producer.
	 *
	 * @param connectedProducer the connected producer
	 */
	public CachedProducer(RDFProducer connectedProducer) {
		this.connectedProducer = connectedProducer;
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
	 */
	public boolean dependsFrom(RDFProducer producer) {
		return (producer == connectedProducer || connectedProducer.dependsFrom(producer));
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#createGraph(org.apache.jena.sparql.core.DatasetGraph)
	 */
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
		if (cachedGraphs.containsKey(inputDataset)) {
			return cachedGraphs.get(inputDataset);
		} else {
			if (cachedDatasets.containsKey(inputDataset)) {
				return cachedDatasets.get(inputDataset).getDefaultGraph();
			} else {
				DynamicGraph newGraph = connectedProducer.createGraph(inputDataset);
				cachedGraphs.put(inputDataset, newGraph);
				return newGraph;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#createDataset(org.apache.jena.sparql.core.DatasetGraph)
	 */
	public DynamicDataset createDataset(DynamicDataset inputDataset) {
		if (cachedDatasets.containsKey(inputDataset)) {
			return cachedDatasets.get(inputDataset);
		} else {
			if (cachedGraphs.containsKey(inputDataset)) {
				return new SingleGraphDataset(cachedGraphs.get(inputDataset));
			} else {
				DynamicDataset newDataset = connectedProducer.createDataset(inputDataset);
				cachedDatasets.put(inputDataset, newDataset);
				return newDataset;
			}
		}
	}

}

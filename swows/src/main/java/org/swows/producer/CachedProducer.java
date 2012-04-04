/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open Web Server (SWOWS).

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
package org.swows.producer;

import java.util.HashMap;
import java.util.Map;

import org.swows.graph.SingleGraphDataset;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

/**
 * The Class CachedProducer connects to another producer
 * caching created graphs or datasets.
 * Cached data is maintained in association with the
 * corresponding input datasets. 
 * It's never used directly in a dataflow so doesn't
 * implement the standard Producer constructor.
 */
public class CachedProducer implements Producer {

	/** The connected producer. */
	private Producer connectedProducer;

	/** The cached graphs. */
	private Map<DatasetGraph,Graph> cachedGraphs = new HashMap<DatasetGraph, Graph>();
	
	/** The cached datasets. */
	private Map<DatasetGraph,DatasetGraph> cachedDatasets = new HashMap<DatasetGraph, DatasetGraph>();

	/**
	 * Instantiates a new cached producer.
	 *
	 * @param connectedProducer the connected producer
	 */
	public CachedProducer(Producer connectedProducer) {
		this.connectedProducer = connectedProducer;
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
	 */
	@Override
	public boolean dependsFrom(Producer producer) {
		return (producer == connectedProducer || connectedProducer.dependsFrom(producer));
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#createGraph(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public Graph createGraph(DatasetGraph inputDataset) {
		if (cachedGraphs.containsKey(inputDataset)) {
			return cachedGraphs.get(inputDataset);
		} else {
			if (cachedDatasets.containsKey(inputDataset)) {
				return cachedDatasets.get(inputDataset).getDefaultGraph();
			} else {
				Graph newGraph = connectedProducer.createGraph(inputDataset);
				cachedGraphs.put(inputDataset, newGraph);
				return newGraph;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#createDataset(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public DatasetGraph createDataset(DatasetGraph inputDataset) {
		if (cachedDatasets.containsKey(inputDataset)) {
			return cachedDatasets.get(inputDataset);
		} else {
			if (cachedGraphs.containsKey(inputDataset)) {
				return new SingleGraphDataset(cachedGraphs.get(inputDataset));
			} else {
				DatasetGraph newDataset = connectedProducer.createDataset(inputDataset);
				cachedDatasets.put(inputDataset, newDataset);
				return newDataset;
			}
		}
	}

}

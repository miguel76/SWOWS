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
package org.swows.producer;

import java.util.Iterator;

import org.swows.graph.UpdatableGraph;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.vocabulary.DF;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

public class UpdatableProducer extends GraphProducer {
	
	private RDFProducer
			baseGraphProducer = EmptyGraphProducer.getInstance(),
			addGraphProducer = EmptyGraphProducer.getInstance(),
			deleteGraphProducer = EmptyGraphProducer.getInstance();

	/**
	 * Instantiates a new updatable producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see RDFProducer
	 */
	public UpdatableProducer(Graph conf, Node confRoot, ProducerMap map) {
		Iterator<Triple> baseGraphTriples = conf.find(confRoot, DF.baseGraph.asNode(), Node.ANY);
		if (baseGraphTriples.hasNext())
			baseGraphProducer = map.getProducer(baseGraphTriples.next().getObject());
		Iterator<Triple> addGraphTriples = conf.find(confRoot, DF.addGraph.asNode(), Node.ANY);
		if (addGraphTriples.hasNext())
			addGraphProducer = map.getProducer(addGraphTriples.next().getObject());
		Iterator<Triple> deleteGraphTriples = conf.find(confRoot, DF.deleteGraph.asNode(), Node.ANY);
		if (deleteGraphTriples.hasNext())
			deleteGraphProducer = map.getProducer(deleteGraphTriples.next().getObject());
	}

	public boolean dependsFrom(RDFProducer producer) {
		return
				producer.equals(baseGraphProducer) || baseGraphProducer.dependsFrom(producer)
				|| producer.equals(addGraphProducer) || addGraphProducer.dependsFrom(producer)
				|| producer.equals(deleteGraphProducer) || deleteGraphProducer.dependsFrom(producer);
	}

	@Override
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
		return new UpdatableGraph(
				baseGraphProducer.createGraph(inputDataset),
				addGraphProducer.createGraph(inputDataset),
				deleteGraphProducer.createGraph(inputDataset) );
	}

}

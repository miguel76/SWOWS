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
import java.util.Iterator;
import java.util.Map;

import org.swows.graph.DynamicDatasetMap;
import org.swows.graph.events.DynamicDataset;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

public class InlineDatasetProducer extends DatasetProducer {
	
	Producer inputProducer = null;
	Map<Node,Producer> namedInputProducers = new HashMap<Node, Producer>();

	/**
	 * Instantiates a new inline dataset producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public InlineDatasetProducer(Graph conf, Node confRoot, final ProducerMap map) {
		inputProducer = map.getProducer( GraphUtils.getSingleValueProperty(conf, confRoot, SPINX.input.asNode()) );
		Iterator<Node> namedInputNodes = GraphUtils.getPropertyValues(conf, confRoot, SPINX.namedInput.asNode());
		while (namedInputNodes.hasNext()) {
			Node namedInputNode = namedInputNodes.next();
			Node graphNode = GraphUtils.getSingleValueProperty(conf, namedInputNode, SPINX.input.asNode());
			Node nameNode = GraphUtils.getSingleValueProperty(conf, namedInputNode, SPINX.id.asNode());
			Producer producer = map.getProducer(graphNode);
			if (producer == null) throw new RuntimeException(this + ": input graph " + graphNode + " not found ");
			namedInputProducers.put(nameNode, producer);
		}
	}
	
	@Override
	public boolean dependsFrom(Producer producer) {
		if ( producer == inputProducer || inputProducer.dependsFrom(producer) )
			return true;
		for (Producer currProducer : namedInputProducers.values())
			if ( producer == currProducer || currProducer.dependsFrom(producer) )
				return true;
		return false;
	}

	@Override
	public DynamicDataset createDataset(DynamicDataset inputDataset) {
		DynamicDataset dataset = new DynamicDatasetMap(inputProducer.createGraph(inputDataset));
		for (Node currNameNode : namedInputProducers.keySet())
			dataset.addGraph( currNameNode,	namedInputProducers.get(currNameNode).createGraph(inputDataset) );
		return dataset;
	}

}

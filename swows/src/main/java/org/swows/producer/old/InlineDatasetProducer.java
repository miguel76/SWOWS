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
import java.util.Iterator;
import java.util.Map;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.util.iterator.Map1;
import org.swows.graph.DynamicDatasetMap;
import org.swows.graph.events.DynamicDataset;
import org.swows.producer.ProducerMap;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;

public class InlineDatasetProducer extends DatasetProducer {
	
	RDFProducer inputProducer = null;
//	Set<Producer> inputProducers = new HashSet<Producer>();
	Map<Node,RDFProducer> namedInputProducers = new HashMap<Node, RDFProducer>();

	private UnionFunction getInputUnion(
			Graph conf, Node confRoot, final ProducerMap map, Node inputProperty) {
		return new UnionFunction(
				GraphUtils
					.getPropertyValues(conf, confRoot, inputProperty)
					.mapWith(new Map1<Node, RDFProducer>() {
						public RDFProducer map1(Node graphNode) {
							RDFProducer producer = map.getProducer(graphNode);
							if (producer == null) throw new RuntimeException(this + ": input graph " + graphNode + " not found ");
								return producer;
						}
					}));
	}
	
	/**
	 * Instantiates a new inline dataset producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see RDFProducer
	 */
	public InlineDatasetProducer(Graph conf, Node confRoot, final ProducerMap map) {
//		final Iterator<Node> inputNodes = GraphUtils.getPropertyValues(conf, confRoot, DF.input.asNode());
		inputProducer = getInputUnion(conf, confRoot, map, DF.defaultInput.asNode());

//		Iterator<Node> inputNodes = GraphUtils.getPropertyValues(conf, confRoot, DF.input.asNode());
//		while (inputNodes.hasNext()) {
//			Node inputNode = inputNodes.next();
//			Node graphNode = GraphUtils.getSingleValueProperty(conf, inputNode, DF.input.asNode());
//			Producer producer = map.getProducer(graphNode);
//			if (producer == null) throw new RuntimeException(this + ": input graph " + graphNode + " not found ");
//			inputProducers.add(producer);
//		}
		
		Iterator<Node> namedInputNodes = GraphUtils.getPropertyValues(conf, confRoot, DF.namedInput.asNode());
		while (namedInputNodes.hasNext()) {
			Node namedInputNode = namedInputNodes.next();
			Node nameNode = GraphUtils.getSingleValueProperty(conf, namedInputNode, DF.name.asNode());
			RDFProducer producer = getInputUnion(conf, namedInputNode, map, DF.input.asNode());
			namedInputProducers.put(nameNode, producer);
		}
	}
	
	public boolean dependsFrom(RDFProducer producer) {
		if ( producer == inputProducer || inputProducer.dependsFrom(producer) )
			return true;
//		for (Producer currProducer : inputProducers)
//			if ( producer == currProducer || currProducer.dependsFrom(producer) )
//				return true;
		for (RDFProducer currProducer : namedInputProducers.values())
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

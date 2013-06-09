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

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

public class SelectGraphProducer extends GraphProducer {

	private Producer inputProducer;
	private Node graphNameNode;

	/**
	 * Instantiates a new select graph producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public SelectGraphProducer(Graph conf, Node confRoot, ProducerMap map) {
//		this(
//				null,
//				GraphUtils.getSingleValueProperty(conf, confRoot, DF.id.asNode()) );
		this(
				null,
				GraphUtils.getSingleValueOptProperty(conf, confRoot, DF.id.asNode()) );
		Node inputNode = GraphUtils.getSingleValueOptProperty(conf, confRoot, DF.input.asNode());
		if (inputNode != null)
			this.inputProducer = map.getProducer( inputNode );
	}

	/**
	 * Instantiates a new select graph producer.
	 *
	 * @param inputProd the producer of the input dataset
	 * @param graphName uri identifying the graph in the dataset
	 */
	public SelectGraphProducer(Producer inputProd, Node graphNameNode) {
		this.inputProducer = inputProd;
		this.graphNameNode = graphNameNode;
	}

	@Override
	public boolean dependsFrom(Producer producer) {
		return ( inputProducer != null && ( producer == inputProducer || inputProducer.dependsFrom(producer) ) );
	}

	@Override
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
		DynamicDataset dataset =
				((inputProducer == null) ?
						inputDataset :
						inputProducer.createDataset(inputDataset));
		return
				(graphNameNode == null) ?
						dataset.getDefaultGraph() :
						dataset.getGraph(graphNameNode);
	}

}

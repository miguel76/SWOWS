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

public class CoalesceGraphProducer extends GraphProducer {

	private RDFProducer prioritaryProducer;
	private RDFProducer secondaryProducer;

//	/**
//	 * Instantiates a new coalesce graph producer.
//	 *
//	 * @param conf the graph with dataflow definition
//	 * @param confRoot the specific node in the graph representing the producer configuration
//	 * @param map the map to access the other defined producers
//	 * @see Producer
//	 */
//	public CoalesceGraphProducer(Graph conf, Node confRoot, ProducerMap map) {
//		this(
//				null,
//				GraphUtils.getSingleValueOptProperty(conf, confRoot, DF.id.asNode()) );
//		Node inputNode = GraphUtils.getSingleValueOptProperty(conf, confRoot, DF.input.asNode());
//		if (inputNode != null)
//			this.inputProducer = map.getProducer( inputNode );
//	}
//
	/**
	 * Instantiates a new coalesce graph producer.
	 *
	 * @param inputProd the producer of the input dataset
	 * @param graphName uri identifying the graph in the dataset
	 */
	public CoalesceGraphProducer(RDFProducer prioritaryProducer, RDFProducer secondaryProducer) {
		this.prioritaryProducer = prioritaryProducer;
		this.secondaryProducer = secondaryProducer;
	}

	public boolean dependsFrom(RDFProducer producer) {
		return 		( prioritaryProducer != null && ( producer == prioritaryProducer || prioritaryProducer.dependsFrom(producer) ) )
				||
					( secondaryProducer != null && ( producer == secondaryProducer || secondaryProducer.dependsFrom(producer) ) );
	}

	@Override
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
		DynamicGraph resultGraph = null;
		if (prioritaryProducer != null)
			resultGraph = prioritaryProducer.createGraph(inputDataset);
		if (resultGraph != null)
			return resultGraph;
		if (secondaryProducer != null)
			return secondaryProducer.createGraph(inputDataset);
		return null;
	}

}

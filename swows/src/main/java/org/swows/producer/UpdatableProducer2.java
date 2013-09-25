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

import org.swows.graph.UpdatableGraph2;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.spinx.QueryFactory;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;
import org.swows.vocabulary.SWI;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

public class UpdatableProducer2 extends GraphProducer {
	
	private String queryTxt = null;
	private String baseURI;
	// TODO: find a better way
	
	private Producer
//				baseGraphProducer = EmptyGraphProducer.getInstance(),
				configProducer = null,
				inputProducer;

	/**
	 * Instantiates a new updatable from events producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public UpdatableProducer2(Graph conf, Node confRoot, ProducerMap map) {
		Node inputNode = GraphUtils.getSingleValueOptProperty(conf, confRoot, DF.input.asNode());
		inputProducer =
				(inputNode != null) ?
						map.getProducer( inputNode ) :
						EmptyGraphProducer.getInstance();
		Node queryNode = GraphUtils.getSingleValueOptProperty(conf, confRoot, DF.configTxt.asNode());
		if (queryNode != null) {
			queryTxt = queryNode.getLiteralLexicalForm();
			baseURI = confRoot.getURI().split("#")[0]; // TODO definitely find a better way 
		}
		else
			configProducer = map.getProducer( GraphUtils.getSingleValueProperty(conf, confRoot, DF.config.asNode()) );
	}

	public boolean dependsFrom(Producer producer) {
		return
//				baseGraphProducer.equals(producer)
//				|| baseGraphProducer.dependsFrom(producer)
				(inputProducer != null && inputProducer.dependsFrom(producer))
				|| (configProducer != null && configProducer.dependsFrom(producer));
	}

	@Override
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
//		DynamicGraph baseGraph = (baseGraphProducer == null) ? null : baseGraphProducer.createGraph(inputDataset); 
		UpdateRequest updateRequest =
				(queryTxt != null) ?
						UpdateFactory.create(queryTxt, baseURI) :
						QueryFactory.toUpdateRequest(configProducer.createGraph(inputDataset), SWI.GraphRoot.asNode());
		return new UpdatableGraph2(
//				baseGraph,
				updateRequest,
				inputProducer.createDataset(inputDataset) );
	}

}

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

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;

/**
 * The Class InferenceGraphProducer executes inference on
 * an input graph, based on an ontology graph.
 */
public class InferenceGraphProducer extends GraphProducer {

	private Producer inputProd;
	//private String reasonerTypeUri;
	//private Resource config;
	private Producer schemaProd;
	private Reasoner reasoner;

	/**
	 * Instantiates a new data flow.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public InferenceGraphProducer(Graph conf, Node confRoot, ProducerMap map) {
		Model confModel = ModelFactory.createModelForGraph(conf);
		Resource confRootRes =
			confModel
				.getRDFNode(confRoot)
				.asResource();
		inputProd =
			map.getProducer(
				confRootRes
					.getPropertyResourceValue(SPINX.input)
					.asNode());
		String reasonerTypeUri =
			confRootRes
				.getPropertyResourceValue(SPINX.reasonerType)
				.getURI();
		Resource config =
			confRootRes
				.getPropertyResourceValue(SPINX.reasonerConfig);
		schemaProd =
			map.getProducer(
				confRootRes
					.getPropertyResourceValue(SPINX.schema)
					.asNode());
		reasoner = ReasonerRegistry.theRegistry().create(reasonerTypeUri, config);
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.GraphProducer#createGraph(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
		return
			new DynamicGraphFromGraph(
					reasoner
					.bindSchema(schemaProd.createGraph(inputDataset))
					.bind(inputProd.createGraph(inputDataset)));
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
	 */
	@Override
	public boolean dependsFrom(Producer producer) {
		return
			(producer == schemaProd
					|| producer == inputProd
					|| schemaProd.dependsFrom(producer)
					|| inputProd.dependsFrom(producer));
	}

}

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

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.producer.ProducerMap;
import org.swows.vocabulary.DF;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
/**
 * The Abstract Class DatasetFunction is the common ancestor
 * of producer classes that operate on an input dataset
 * and generate a dataset in output.
 * Concrete classes must implement the {@code exec} method.
 */
public abstract class DatasetFunction extends DatasetProducer {

	private DatasetProducer inputProd;

	/**
	 * Instantiates a new dataset function.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see RDFProducer
	 */
	public DatasetFunction(DynamicGraph conf, Node confRoot, ProducerMap map) {
		Model confModel = ModelFactory.createModelForGraph(conf);
		inputProd =
			(DatasetProducer) map.getProducer(
				confModel
					.getRDFNode(confRoot)
					.asResource()
					.getPropertyResourceValue(DF.input)
					.asNode());
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
	 */
	public boolean dependsFrom(RDFProducer producer) {
		return (producer == inputProd || inputProd.dependsFrom(producer));
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.DatasetProducer#createDataset(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public DynamicDataset createDataset(DynamicDataset inputDataset) {
		return exec(inputProd.createDataset(inputDataset));
	}

	/**
	 * Executes the desired operation.
	 *
	 * @param input the input dataset
	 * @return the output dataset
	 */
	public abstract DynamicDataset exec(DynamicDataset input);

}

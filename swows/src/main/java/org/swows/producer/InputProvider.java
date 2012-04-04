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

import java.util.Iterator;

import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

/**
 * The Class InputProvider produces a graph reading it from
 * an input of the dataflow.
 */
public class InputProvider extends GraphProducer {

	private String inputGraphName = null;

	/**
	 * Instantiates a new input provider.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public InputProvider(Graph conf, Node confRoot, ProducerMap map) {
		Iterator<Node> ids =
			conf.queryHandler().objectsFor(confRoot, SPINX.id.asNode());
		if (ids.hasNext())
//			inputGraphNode = parentConf.getResource(ids.next().getURI()).asNode();
			inputGraphName = ids.next().getURI();
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.GraphProducer#createGraph(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public Graph createGraph(DatasetGraph inputDataset) {
		if (inputGraphName == null)
			return inputDataset.getDefaultGraph();
		else {
			return inputDataset.getGraph(Node.createURI(inputGraphName));
		}
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
	 */
	@Override
	public boolean dependsFrom(Producer producer) {
		return false;
	}

}

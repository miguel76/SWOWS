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

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.SP;
import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

/**
 * The Class InlineGraphProducer generates a graph whose
 * content is described in the dataflow itself.
 */
public class InlineGraphProducer extends GraphProducer {

	private DynamicGraph inlineGraph;

	/**
	 * Instantiates a new inline graph producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public InlineGraphProducer(Graph conf, Node confRoot, ProducerMap map) {
		inlineGraph = new DynamicGraphFromGraph(GraphFactory.createGraphMem());
		Iterator<Node> tripleIter = GraphUtils.getPropertyValues(conf, confRoot, SPINX.triple.asNode());
		while(tripleIter.hasNext()) {
			Node tripleNode = tripleIter.next();
			Node subject = GraphUtils.getSingleValueProperty( conf, tripleNode, SP.subject.asNode() );
			Node predicate = GraphUtils.getSingleValueProperty( conf, tripleNode, SP.predicate.asNode() );
			Node object = GraphUtils.getSingleValueProperty( conf, tripleNode, SP.object.asNode() );
			inlineGraph.add(new Triple(subject, predicate, object));
		}

	}

	/* (non-Javadoc)
	 * @see org.swows.producer.GraphProducer#createGraph(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
		return inlineGraph;
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
	 */
	@Override
	public boolean dependsFrom(Producer producer) {
		return false;
	}

}

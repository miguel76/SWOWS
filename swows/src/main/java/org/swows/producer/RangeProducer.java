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

import java.math.BigInteger;
import java.util.Iterator;

import org.swows.vocabulary.DF;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

public class RangeProducer extends SetProducer {
	
	private BigInteger start;
	private BigInteger end;

	/**
	 * Instantiates a new range producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public RangeProducer(Graph conf, Node confRoot, ProducerMap map) {
		Iterator<Triple> startIter =
				conf.find(confRoot, DF.intervalStart.asNode(), Node.ANY);
		if (startIter.hasNext()) {
			Node startNode = startIter.next().getObject();
			if (startNode.isLiteral() && startNode.getLiteralDatatype().equals(XSDDatatype.XSDinteger)) {
				start = new BigInteger( startNode.getLiteralLexicalForm() );
			} else
				throw new ProducerException("Parameter startNode has wrong type. expected literal with " + XSDDatatype.XSDinteger.getURI() + " type");
		} else
			throw new ProducerException("Parameter startNode not found");
		Iterator<Triple> endIter =
				conf.find(confRoot, DF.intervalEnd.asNode(), Node.ANY);
		if (endIter.hasNext()) {
			Node endNode = endIter.next().getObject();
			if (endNode.isLiteral() && endNode.getLiteralDatatype().equals(XSDDatatype.XSDinteger)) {
				end = new BigInteger( endNode.getLiteralLexicalForm() );
			} else
				throw new ProducerException("Parameter endNode has wrong type. expected literal with " + XSDDatatype.XSDinteger.getURI() + " type");
		} else
			throw new ProducerException("Parameter endNode not found");
	}

	@Override
	public boolean dependsFrom(Producer producer) {
		return false;
	}

	@Override
	public Iterator<Node> createIterator(DatasetGraph inputDataset) {
		return new Iterator<Node>() {
			private BigInteger index = start;
			@Override
			public boolean hasNext() {
				return index.compareTo(end) <= 0;
			}
			@Override
			public Node next() {
				Node currNode = NodeFactory.createLiteral(index.toString(),XSDDatatype.XSDinteger);
				index = index.add(BigInteger.ONE);
				return currNode;
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException("Read-only Iterator");
			}
		};
	}

}

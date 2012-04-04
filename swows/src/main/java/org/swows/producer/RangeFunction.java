package org.swows.producer;

import java.math.BigInteger;
import java.util.Iterator;

import org.swows.vocabulary.Instance;
import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

public class RangeFunction extends GraphToSetFunction {
	
	/**
	 * Instantiates a new range producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public RangeFunction(Graph conf, Node confRoot, ProducerMap map) {
		super(conf,confRoot,map);
	}

	@Override
	public Iterator<Node> createIterator(Graph inputGraph) {
		final BigInteger start, end;
		Iterator<Triple> startIter =
				inputGraph.find(Instance.GraphRoot.asNode(), SPINX.intervalStart.asNode(), Node.ANY);
		if (startIter.hasNext()) {
			Node startNode = startIter.next().getObject();
			if (startNode.isLiteral() && startNode.getLiteralDatatype().equals(XSDDatatype.XSDinteger)) {
				start = new BigInteger( startNode.getLiteralLexicalForm() );
			} else
				throw new ProducerException("Parameter intervalStart has wrong type. expected literal with " + XSDDatatype.XSDinteger.getURI() + " type");
		} else
			throw new ProducerException("Parameter intervalStart not found");
		Iterator<Triple> endIter =
				inputGraph.find(Instance.GraphRoot.asNode(), SPINX.intervalEnd.asNode(), Node.ANY);
		if (endIter.hasNext()) {
			Node endNode = endIter.next().getObject();
			if (endNode.isLiteral() && endNode.getLiteralDatatype().equals(XSDDatatype.XSDinteger)) {
				end = new BigInteger( endNode.getLiteralLexicalForm() );
			} else
				throw new ProducerException("Parameter intervalEnd has wrong type. expected literal with " + XSDDatatype.XSDinteger.getURI() + " type");
		} else
			throw new ProducerException("Parameter intervalEnd not found");
//		final BigInteger start, end;
		return new Iterator<Node>() {
			private BigInteger index = start;
			@Override
			public boolean hasNext() {
				return index.compareTo(end) <= 0;
			}
			@Override
			public Node next() {
				Node currNode = Node.createLiteral(index.toString(),XSDDatatype.XSDinteger);
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

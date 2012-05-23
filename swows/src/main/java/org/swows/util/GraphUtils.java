package org.swows.util;

import java.util.Iterator;

import org.swows.vocabulary.DOMEvents;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Map1;

public class GraphUtils {
	
	public static Node getSingleValueOptProperty(Graph graph, Node subject, Node predicate) {
		Iterator<Triple> triples = graph.find(subject, predicate, Node.ANY);
		if (!triples.hasNext())
			return null;
		Node object = triples.next().getObject();
		if (triples.hasNext())
			throw new RuntimeException("Too much values of property " + predicate + " found for node " + subject);
		return object;
	}
	
	public static Node getSingleValueProperty(Graph graph, Node subject, Node predicate) {
		Iterator<Triple> triples = graph.find(subject, predicate, Node.ANY);
		if (!triples.hasNext())
			throw new RuntimeException("Property " + predicate + " not found for node " + subject);
		Node object = triples.next().getObject();
		if (triples.hasNext())
			throw new RuntimeException("Too much values of property " + predicate + " found for node " + subject);
		return object;
	}
	
	public static ExtendedIterator<Node> getPropertyValues(Graph graph, Node subject, Node predicate) {
		return graph.find(subject, predicate, Node.ANY).mapWith(new Map1<Triple, Node>() {
			@Override
			public Node map1(Triple t) {
				return t.getObject();
			}
		});
	}

	public static void addBooleanProperty(Graph graph, Node subject, Node predicate, boolean value) {
		Node litNode =
				Node.createLiteral(
						"" + value,
						(String) null, XSDDatatype.XSDboolean);
		graph.add( new Triple(subject, predicate, litNode));
	}
	
	public static void addIntegerProperty(Graph graph, Node subject, Node predicate, long value) {
		Node litNode =
				Node.createLiteral(
						"" + value,
						(String) null, XSDDatatype.XSDinteger);
		graph.add( new Triple(subject, predicate, litNode));
	}
	
	public static void addDecimalProperty(Graph graph, Node subject, Node predicate, double value) {
		Node litNode =
				Node.createLiteral(
						"" + value,
						(String) null, XSDDatatype.XSDdecimal);
		graph.add( new Triple(subject, predicate, litNode));
	}
	
}

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
package org.swows.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.util.iterator.Map1;

public class GraphUtils {
	
	public static Node getSingleValueOptProperty(Graph graph, Node subject, Node predicate, Set<Triple> tripleSet) {
		Iterator<Triple> triples = graph.find(subject, predicate, Node.ANY);
		if (!triples.hasNext())
			return null;
		Triple firstTriple = triples.next();
		Node object = firstTriple.getObject();
		if (tripleSet != null)
			tripleSet.add(firstTriple);
		if (triples.hasNext())
			throw new RuntimeException("Too much values of property " + predicate + " found for node " + subject);
		return object;
	}
	
	public static Node getSingleValueProperty(Graph graph, Node subject, Node predicate, Set<Triple> tripleSet) {
		Iterator<Triple> triples = graph.find(subject, predicate, Node.ANY);
		if (!triples.hasNext())
			throw new RuntimeException("Property " + predicate + " not found for node " + subject);
		Triple firstTriple = triples.next();
		Node object = firstTriple.getObject();
		if (tripleSet != null)
			tripleSet.add(firstTriple);
		if (triples.hasNext())
			throw new RuntimeException("Too much values of property " + predicate + " found for node " + subject);
		return object;
	}
	
	public static ExtendedIterator<Node> getPropertyValues(Graph graph, Node subject, Node predicate, final Set<Triple> tripleSet) {
		return graph.find(subject, predicate, Node.ANY).mapWith(new Map1<Triple, Node>() {
			@Override
			public Node map1(Triple t) {
				if (tripleSet != null)
					tripleSet.add(t);
				return t.getObject();
			}
		});
	}

	public static Node getSingleValueOptProperty(Graph graph, Node subject, Node predicate) {
		return getSingleValueOptProperty(graph, subject, predicate, null);
	}
	
	public static Node getSingleValueProperty(Graph graph, Node subject, Node predicate) {
		return getSingleValueProperty(graph, subject, predicate, null);
	}
	
	public static ExtendedIterator<Node> getPropertyValues(Graph graph, Node subject, Node predicate) {
		return getPropertyValues(graph, subject, predicate, null);
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
	
	public static void deletePropertyBasedOn(final Graph graph, final String baseUri) {
//		List<Triple> triplesToDelete = new Vector<Triple>();
		Set<Triple> triplesToDelete =
				graph
				.find(Node.ANY, Node.ANY, Node.ANY)
				.filterKeep(new Filter<Triple>() {
					@Override
					public boolean accept(Triple triple) {
						Node p = triple.getPredicate();
						return p.isURI() && p.getURI().startsWith(baseUri);
					}
				})
				.toSet();
		graph.getBulkUpdateHandler().delete(triplesToDelete.iterator());
	}
	
}

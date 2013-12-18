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
package org.swows.graph.events;

import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

public class SimpleGraphUpdate implements GraphUpdate {
	
	private Graph baseGraph;
	private Graph addedGraph = GraphFactory.createGraphMem();
	private Graph deletedGraph = GraphFactory.createGraphMem();
	
	public SimpleGraphUpdate() {
		this(null);
	}

	public SimpleGraphUpdate(Graph baseGraph) {
		this.baseGraph = baseGraph;
	}

	public void putAddedTriple(Triple triple) {
		if (deletedGraph.contains(triple))
			deletedGraph.delete(triple);
		else if (baseGraph == null || !baseGraph.contains(triple))
			addedGraph.add(triple);
	}
	
	public void putDeletedTriple(Triple triple) {
		if (addedGraph.contains(triple))
			addedGraph.delete(triple);
		else if (baseGraph == null || baseGraph.contains(triple))
			deletedGraph.add(triple);
	}
	
	public void putAddedTriple(Node s, Node p, Node o) {
		putAddedTriple(new Triple(s, p, o));
	}
	
	public void putDeletedTriple(Node s, Node p, Node o) {
		putDeletedTriple(new Triple(s, p, o));
	}
	
	public void putAddedTriples(Triple[] triples) {
		for (Triple t: triples)
			putAddedTriple(t);
	}

	public void putDeletedTriples(Triple[] triples) {
		for (Triple t: triples)
			putDeletedTriple(t);
	}

	public void putAddedTriples(Iterator<Triple> triples) {
		while (triples.hasNext())
			putAddedTriple(triples.next());
	}

	public void putDeletedTriples(Iterator<Triple> triples) {
		while (triples.hasNext())
			putDeletedTriple(triples.next());
	}

	public void putAddedTriples(List<Triple> triples) {
		putAddedTriples(triples.iterator());
	}

	public void putDeletedTriples(List<Triple> triples) {
		putDeletedTriples(triples.iterator());
	}

	public void putAddedTriples(Graph graph) {
		putAddedTriples(graph.find(Node.ANY, Node.ANY, Node.ANY));
	}

	public void putDeletedTriples(Graph graph) {
		putDeletedTriples(graph.find(Node.ANY, Node.ANY, Node.ANY));
	}

	public boolean isEmpty() {
		return addedGraph.isEmpty() && deletedGraph.isEmpty();
	}
	
	public Graph getAddedGraph() {
		return addedGraph;
	}

	public Graph getDeletedGraph() {
		return deletedGraph;
	}

}

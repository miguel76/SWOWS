package org.swows.origin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.swows.vocabulary.PROV;

public class GraphOriginManager extends OriginManagerBase {
	
	Graph graph;
	Map<Quad, Node> reifiedQuadMap = new HashMap<Quad, Node>();
	
	public GraphOriginManager(Graph graph) {
		this.graph = graph;
	}

    private void addReifiedQuadWorker(Node node, Quad quad) {
    	graph.add(new Triple(node, RDF.type.asNode(), RDF.Statement.asNode()));
    	graph.add(new Triple(node, RDF.subject.asNode(), quad.getSubject()));
    	graph.add(new Triple(node, RDF.predicate.asNode(), quad.getPredicate()));
    	graph.add(new Triple(node, RDF.object.asNode(), quad.getObject()));
    	graph.add(new Triple(node, RDFS.isDefinedBy.asNode(), quad.getGraph()));
    }
    
    private Node addReifiedQuad(Quad quad) {
    	Node quadNode = reifiedQuadMap.get(quad);
    	if (quadNode == null) {
        	quadNode = NodeFactory.createBlankNode();
        	addReifiedQuadWorker(quadNode, quad);
        	reifiedQuadMap.put(quad, quadNode);
    	}
    	return quadNode;
    }
    
	@Override
	public void addOrigin(Quad newQuad, Iterator<Quad> originQuads) {
		Node provNode = NodeFactory.createBlankNode();
		graph.add(new Triple(
				addReifiedQuad(newQuad), PROV.wasDerivedFrom.asNode(), provNode));
		originQuads.forEachRemaining(originQuad -> {
			graph.add(new Triple(
					provNode, PROV.hadMember.asNode(), addReifiedQuad(originQuad))); } );

	}

}

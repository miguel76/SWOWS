package org.swows.origin;

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
	
	public GraphOriginManager(Graph graph) {
		this.graph = graph;
	}

    private void addReifiedQuad(Node node, Quad quad) {
    	graph.add(new Triple(node, RDF.type.asNode(), RDF.Statement.asNode()));
    	graph.add(new Triple(node, RDF.subject.asNode(), quad.getSubject()));
    	graph.add(new Triple(node, RDF.predicate.asNode(), quad.getPredicate()));
    	graph.add(new Triple(node, RDF.object.asNode(), quad.getObject()));
    	graph.add(new Triple(node, RDFS.isDefinedBy.asNode(), quad.getGraph()));
    }
    
    private Node addReifiedQuad(Quad quad) {
    	Node quadNode = NodeFactory.createBlankNode();
    	addReifiedQuad(quadNode, quad);
    	return quadNode;
    }
    
	@Override
	public void addOrigin(Quad newQuad, Quad originQuad) {
		// TODO manage and/or structure
		graph.add(new Triple(
				addReifiedQuad(newQuad), PROV.wasDerivedFrom.asNode(), addReifiedQuad(originQuad)));

	}

}

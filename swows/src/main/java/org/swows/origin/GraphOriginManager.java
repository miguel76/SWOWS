package org.swows.origin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphUtil;
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
    
    private Quad getQuad(Node node) {
    	return new Quad(
    			GraphUtil.listObjects(graph, node, RDFS.isDefinedBy.asNode()).next(),
    			GraphUtil.listObjects(graph, node, RDF.subject.asNode()).next(),
    			GraphUtil.listObjects(graph, node, RDF.predicate.asNode()).next(),
    			GraphUtil.listObjects(graph, node, RDF.object.asNode()).next());
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

	@Override
	public Iterator<Iterator<Quad>> getOrigin(Quad quad) {
		Node quadNode = reifiedQuadMap.get(quad);
		if (quadNode == null) {
			return null;
		} else {
			return
					GraphUtil.listObjects(graph, quadNode, PROV.wasDerivedFrom.asNode())
						.mapWith(provNode ->
								GraphUtil.listObjects(graph, provNode, PROV.hadMember.asNode())
									.mapWith(statNode -> getQuad(statNode)));
		}
	}

}

package org.swows.graph.events;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.util.Context;

public class DynamicDatasetFromDataset extends DynamicDataset {
	
	DatasetGraph baseDataset;
	
	public DynamicDatasetFromDataset(DatasetGraph baseDataset) {
		this.baseDataset = baseDataset;
	}

	@Override
	public boolean containsGraph(Node graphNode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeGraph(Node graphName) {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterator<Node> listGraphNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(Quad quad) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Quad quad) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(Node g, Node s, Node p, Node o) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Node g, Node s, Node p, Node o) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAny(Node g, Node s, Node p, Node o) {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterator<Quad> find() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Quad> find(Quad quad) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Quad> find(Node g, Node s, Node p, Node o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Quad> findNG(Node g, Node s, Node p, Node o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(Node g, Node s, Node p, Node o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Quad quad) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Lock getLock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public DynamicGraph getDefaultGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DynamicGraph getGraph(Node graphNode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDefaultGraph(DynamicGraph g) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addGraph(Node graphName, DynamicGraph graph) {
		// TODO Auto-generated method stub

	}

}

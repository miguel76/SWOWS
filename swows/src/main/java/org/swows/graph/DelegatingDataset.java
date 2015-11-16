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
package org.swows.graph;

import java.util.Iterator;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.shared.Lock;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.util.Context;

/**
 * The Class DelegatingDataset is a dataset that delegates
 * the execution of each method to a "base dataset" (the
 * one returned by the method {@code DelegatingDataset}.
 * This method is called just once: the returned dataset is
 * cached for later method calls. 
 */
/**
 * The Class DelegatingDataset.
 */
public abstract class DelegatingDataset implements DatasetGraph {

	/**
	 * Gets the base dataset.
	 *
	 * @return the base dataset
	 */
	protected abstract DatasetGraph getBaseDataset();

	private DatasetGraph baseDatasetCopy;

	private DatasetGraph getLocalDataset() {
		if (baseDatasetCopy == null)
			baseDatasetCopy = getBaseDataset();
		return baseDatasetCopy;
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#add(org.apache.jena.sparql.core.Quad)
	 */
	public void add(Quad quad) {
		getLocalDataset().add(quad);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#addGraph(org.apache.jena.graph.Node, org.apache.jena.graph.Graph)
	 */
	public void addGraph(Node graphName, Graph graph) {
		getLocalDataset().addGraph(graphName, graph);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#close()
	 */
	public void close() {
		getLocalDataset().close();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#contains(org.apache.jena.sparql.core.Quad)
	 */
	public boolean contains(Quad quad) {
		return getLocalDataset().contains(quad);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#contains(org.apache.jena.graph.Node, org.apache.jena.graph.Node, org.apache.jena.graph.Node, org.apache.jena.graph.Node)
	 */
	public boolean contains(Node g, Node s, Node p, Node o) {
		return getLocalDataset().contains(g, s, p, o);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#containsGraph(org.apache.jena.graph.Node)
	 */
	public boolean containsGraph(Node graphNode) {
		return getLocalDataset().containsGraph(graphNode);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#delete(org.apache.jena.sparql.core.Quad)
	 */
	public void delete(Quad quad) {
		getLocalDataset().delete(quad);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#deleteAny(org.apache.jena.graph.Node, org.apache.jena.graph.Node, org.apache.jena.graph.Node, org.apache.jena.graph.Node)
	 */
	public void deleteAny(Node g, Node s, Node p, Node o) {
		getLocalDataset().deleteAny(g, s, p, o);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#find()
	 */
	public Iterator<Quad> find() {
		return getLocalDataset().find();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#find(org.apache.jena.sparql.core.Quad)
	 */
	public Iterator<Quad> find(Quad quad) {
		return getLocalDataset().find(quad);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#find(org.apache.jena.graph.Node, org.apache.jena.graph.Node, org.apache.jena.graph.Node, org.apache.jena.graph.Node)
	 */
	public Iterator<Quad> find(Node g, Node s, Node p, Node o) {
		return getLocalDataset().find(g, s, p, o);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#findNG(org.apache.jena.graph.Node, org.apache.jena.graph.Node, org.apache.jena.graph.Node, org.apache.jena.graph.Node)
	 */
	public Iterator<Quad> findNG(Node g, Node s, Node p, Node o) {
		return getLocalDataset().findNG(g, s, p, o);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#getContext()
	 */
	public Context getContext() {
		return getLocalDataset().getContext();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#getDefaultGraph()
	 */
	public Graph getDefaultGraph() {
		return getLocalDataset().getDefaultGraph();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#getGraph(org.apache.jena.graph.Node)
	 */
	public Graph getGraph(Node graphNode) {
		return getLocalDataset().getGraph(graphNode);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#getLock()
	 */
	public Lock getLock() {
		return getLocalDataset().getLock();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#isEmpty()
	 */
	public boolean isEmpty() {
		return getLocalDataset().isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#listGraphNodes()
	 */
	public Iterator<Node> listGraphNodes() {
		return getLocalDataset().listGraphNodes();
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#removeGraph(org.apache.jena.graph.Node)
	 */
	public void removeGraph(Node graphName) {
		getLocalDataset().removeGraph(graphName);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#setDefaultGraph(org.apache.jena.graph.Graph)
	 */
	public void setDefaultGraph(Graph g) {
		getLocalDataset().setDefaultGraph(g);
	}

	/* (non-Javadoc)
	 * @see org.apache.jena.sparql.core.DatasetGraph#size()
	 */
	public long size() {
		return getLocalDataset().size();
	}

}

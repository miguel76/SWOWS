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

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.util.Context;

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
public abstract class DelegatingDynamicDataset extends DynamicDataset {

	/**
	 * Gets the base dataset.
	 *
	 * @return the base dataset
	 */
	protected abstract DynamicDataset getBaseDataset();

	protected DynamicDataset baseDatasetCopy;

	protected DynamicDataset getLocalDataset() {
		if (baseDatasetCopy == null)
			baseDatasetCopy = getBaseDataset();
		return baseDatasetCopy;
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#add(com.hp.hpl.jena.sparql.core.Quad)
	 */
	@Override
	public void add(Quad quad) {
		getLocalDataset().add(quad);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#addGraph(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Graph)
	 */
	@Override
	public void addGraph(Node graphName, DynamicGraph graph) {
		getLocalDataset().addGraph(graphName, graph);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#close()
	 */
	@Override
	public void close() {
		getLocalDataset().close();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#contains(com.hp.hpl.jena.sparql.core.Quad)
	 */
	@Override
	public boolean contains(Quad quad) {
		return getLocalDataset().contains(quad);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#contains(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node)
	 */
	@Override
	public boolean contains(Node g, Node s, Node p, Node o) {
		return getLocalDataset().contains(g, s, p, o);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#containsGraph(com.hp.hpl.jena.graph.Node)
	 */
	@Override
	public boolean containsGraph(Node graphNode) {
		return getLocalDataset().containsGraph(graphNode);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#delete(com.hp.hpl.jena.sparql.core.Quad)
	 */
	@Override
	public void delete(Quad quad) {
		getLocalDataset().delete(quad);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#deleteAny(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node)
	 */
	@Override
	public void deleteAny(Node g, Node s, Node p, Node o) {
		getLocalDataset().deleteAny(g, s, p, o);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#find()
	 */
	@Override
	public Iterator<Quad> find() {
		return getLocalDataset().find();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#find(com.hp.hpl.jena.sparql.core.Quad)
	 */
	@Override
	public Iterator<Quad> find(Quad quad) {
		return getLocalDataset().find(quad);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#find(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node)
	 */
	@Override
	public Iterator<Quad> find(Node g, Node s, Node p, Node o) {
		return getLocalDataset().find(g, s, p, o);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#findNG(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node)
	 */
	@Override
	public Iterator<Quad> findNG(Node g, Node s, Node p, Node o) {
		return getLocalDataset().findNG(g, s, p, o);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#getContext()
	 */
	@Override
	public Context getContext() {
		return getLocalDataset().getContext();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#getDefaultGraph()
	 */
	@Override
	public DynamicGraph getDefaultGraph() {
		return getLocalDataset().getDefaultGraph();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#getGraph(com.hp.hpl.jena.graph.Node)
	 */
	@Override
	public DynamicGraph getGraph(Node graphNode) {
		return getLocalDataset().getGraph(graphNode);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#getLock()
	 */
	@Override
	public Lock getLock() {
		return getLocalDataset().getLock();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getLocalDataset().isEmpty();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#listGraphNodes()
	 */
	@Override
	public Iterator<Node> listGraphNodes() {
		return getLocalDataset().listGraphNodes();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#removeGraph(com.hp.hpl.jena.graph.Node)
	 */
	@Override
	public void removeGraph(Node graphName) {
		getLocalDataset().removeGraph(graphName);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#setDefaultGraph(com.hp.hpl.jena.graph.Graph)
	 */
	@Override
	public void setDefaultGraph(DynamicGraph g) {
		getLocalDataset().setDefaultGraph(g);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#size()
	 */
	@Override
	public long size() {
		return getLocalDataset().size();
	}

	@Override
	public void add(Node g, Node s, Node p, Node o) {
		getLocalDataset().add(g, s, p, o);
	}

	@Override
	public void delete(Node g, Node s, Node p, Node o) {
		getLocalDataset().delete(g, s, p, o);
	}

}

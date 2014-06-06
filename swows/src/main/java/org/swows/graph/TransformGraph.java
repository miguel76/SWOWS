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

import org.apache.log4j.Logger;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;
import org.swows.graph.transform.GraphTransform;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

/**
 * The Class TransformGraph it's the result of a
 * Graph Transform on an input dataset.
 * If the dataset changes the transform is executed again and
 * the result updated.
 */
public class TransformGraph extends DynamicChangingGraph {

	private GraphTransform graphTransform;
	private DynamicDataset queryDataset;
    private static final Logger logger = Logger.getLogger(TransformGraph.class);
	
	private Listener listener = new Listener() {
		public void notifyUpdate(Graph source, GraphUpdate update) {
			update();
		}
	};

	private void registerListener() {
		queryDataset.getDefaultGraph()
			.getEventManager2()
			.register(listener);
		Iterator<Node> graphNodes = queryDataset.listGraphNodes();
//		for (String namedGraphURI : query.getNamedGraphURIs() ) {
		while (graphNodes.hasNext()) {
			DynamicGraph currGraph = queryDataset.getGraph( graphNodes.next() );
//			Node graphNode = NodeFactory.createURI(namedGraphURI);
//			DynamicGraph currGraph = queryDataset.getGraph( graphNode );
			currGraph.getEventManager2().register(listener);
		}
	}

	/**
	 * Instantiates a new sparql construct graph.
	 *
	 * @param query the query
	 * @param queryDataset the input dataset
	 */
	public TransformGraph(GraphTransform graphTransform, DynamicDataset queryDataset) {
		super();
		this.graphTransform = graphTransform;
		this.queryDataset = queryDataset;
		baseGraph = exec();
		registerListener();
	}

	private synchronized void update() {
		Graph newGraph = exec();
		logger.debug("Setting new graph in " + hashCode());
		setBaseGraph(newGraph);
	}

	private Graph exec() {
		return graphTransform.apply(queryDataset).getDefaultGraph();
	}

/*
	@Override
	protected Graph getBaseGraph() {
		return exec();
	}
*/

}

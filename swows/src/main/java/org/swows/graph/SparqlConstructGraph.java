/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open Web Server (SWOWS).

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

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

/**
 * The Class SparqlConstructGraph it's the result of a
 * Construct SPARQL query on an input dataset.
 * If the dataset changes the query is executed again and
 * the result updated.
 */
public class SparqlConstructGraph extends BuildingGraph {

	private class GraphListener extends PushGraphListener {

		public GraphListener(Graph sourceGraph) {
			super(sourceGraph, getEventManager());
		}

		@Override
		protected boolean changed() {
			// bloody programming style, "state checking" method changed() used for side effect
			update(sourceGraph);
			return false;
		}

/*
		@Override
		protected void notifyEvents() {
			update(sourceGraph);
		}
*/

	};

	private Query query;
	private DatasetGraph queryDataset;

	private void registerListeners() {
		queryDataset.getDefaultGraph()
			.getEventManager()
			.register(new GraphListener(queryDataset.getDefaultGraph()));
		Iterator<Node> graphNodes = queryDataset.listGraphNodes();
		while (graphNodes.hasNext()) {
			Graph currGraph = queryDataset.getGraph( graphNodes.next() );
			currGraph
			.getEventManager()
			.register(new GraphListener(currGraph));
		}
	}

	/**
	 * Instantiates a new sparql construct graph.
	 *
	 * @param query the query
	 * @param queryDataset the input dataset
	 */
	public SparqlConstructGraph(Query query, DatasetGraph queryDataset) {
		super();
		this.query = query;
		this.queryDataset = queryDataset;
		baseGraphCopy = exec();
		baseGraphCopy.getEventManager().register(localEventManager);
		registerListeners();
	}

	private synchronized void update(Graph sourceGraph) {
		Graph newGraph = exec();
		/*
		Graph addedGraph = new Difference(newGraph, baseGraphCopy);
		Graph deletedGraph = new Difference(baseGraphCopy, newGraph);
		if (!addedGraph.isEmpty() || !deletedGraph.isEmpty()) {
			GraphEventManager eventManager = getEventManager();
			eventManager.notifyEvent(sourceGraph, GraphEvents.startRead);
			eventManager.notifyAddIterator(
					this,
					addedGraph.find(Node.ANY, Node.ANY, Node.ANY));
			eventManager.notifyDeleteIterator(
					this,
					deletedGraph.find(Node.ANY, Node.ANY, Node.ANY));
			baseGraphCopy = newGraph;
			eventManager.notifyEvent(sourceGraph, GraphEvents.finishRead);

		}
		*/
		setBaseGraph(newGraph, sourceGraph);
	}

	private Graph exec() {
//		Dataset dataset = DatasetFactory.create(queryDataset);
//		System.out.println("Input Default Graph: ");
//		dataset.getDefaultModel().write(System.out,"N3");
//		System.out.println();
//		Iterator<String> nameList = dataset.listNames();
//		while (nameList.hasNext()) {
//			String uri = nameList.next();
//			System.out.println("Input Named Graph (" + uri + "): ");
//			dataset.getNamedModel(uri).write(System.out,"N3");
//			System.out.println();
//		}
		
		QueryExecution queryExecution =
				QueryExecutionFactory.create(query, DatasetFactory.create(queryDataset));
		return queryExecution.execConstruct().getGraph();
	}

/*
	@Override
	protected Graph getBaseGraph() {
		return exec();
	}
*/

}

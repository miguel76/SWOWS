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

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

/**
 * The Class SparqlConstructGraph it's the result of a
 * Construct SPARQL query on an input dataset.
 * If the dataset changes the query is executed again and
 * the result updated.
 */
public class SparqlConstructGraph extends DynamicChangingGraph {

	private Query query;
	private DynamicDataset queryDataset;
	
	private Listener listener = new Listener() {
		@Override
		public void notifyUpdate(Graph source, GraphUpdate update) {
			update();
		}
	};

	private void registerListener() {
		queryDataset.getDefaultGraph()
			.getEventManager2()
			.register(listener);
//		Iterator<Node> graphNodes = queryDataset.listGraphNodes();
		for (String namedGraphURI : query.getNamedGraphURIs() ) {
//		while (graphNodes.hasNext()) {
//			DynamicGraph currGraph = queryDataset.getGraph( graphNodes.next() );
			Node graphNode = Node.createURI(namedGraphURI);
			DynamicGraph currGraph = queryDataset.getGraph( graphNode );
			currGraph.getEventManager2().register(listener);
		}
	}

	/**
	 * Instantiates a new sparql construct graph.
	 *
	 * @param query the query
	 * @param queryDataset the input dataset
	 */
	public SparqlConstructGraph(Query query, DynamicDataset queryDataset) {
		super();
		this.query = query;
		this.queryDataset = queryDataset;
		baseGraph = exec();
		registerListener();
	}

	private synchronized void update() {
		Graph newGraph = exec();
		setBaseGraph(newGraph);
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
//        Graph resGraph = GraphFactory.createGraphMem();
//        resGraph.getBulkUpdateHandler().add(queryExecution.execConstruct().getGraph());
        Graph resGraph = queryExecution.execConstruct().getGraph();
		queryExecution.close();
		return resGraph;
	}

/*
	@Override
	protected Graph getBaseGraph() {
		return exec();
	}
*/

}

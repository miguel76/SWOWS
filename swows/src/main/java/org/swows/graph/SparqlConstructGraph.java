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

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.sparql.resultset.RDFOutput;

/**
 * The Class SparqlConstructGraph it's the result of a
 * Construct SPARQL query on an input dataset.
 * If the dataset changes the query is executed again and
 * the result updated.
 */
public class SparqlConstructGraph extends DynamicChangingGraph {

	private Query query;
	private DynamicDataset queryDataset;
    private static final Logger logger = Logger.getLogger(SparqlConstructGraph.class);
	
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
	public SparqlConstructGraph(Query query, DynamicDataset queryDataset) {
		super();
		this.query = query;
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
		
		logger.debug("Begin new graph construction in " + hashCode());
		QueryExecution queryExecution =
				QueryExecutionFactory.create(
						query,
						DatasetFactory.create(new DynamicDatasetMap( queryDataset) ));
//        Graph resGraph = GraphFactory.createGraphMem();
//        resGraph.getBulkUpdateHandler().add(queryExecution.execConstruct().getGraph());

//		System.out.println("**** QUERY " + queryExecution.hashCode() + " START ***");
//		System.out.println("Executing the following query...");
//		System.out.println(query);
//		System.out.println("...on the following dataset...");
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
//		long queryStart = System.currentTimeMillis();
		
		Graph resGraph = null;
		
		if (query.isConstructType())
			resGraph = queryExecution.execConstruct().getGraph();
		else if (query.isDescribeType())
			resGraph = queryExecution.execDescribe().getGraph();
		else if (query.isSelectType()) {
			RDFOutput rdfOutput = new RDFOutput();
			resGraph = rdfOutput.toModel(queryExecution.execSelect()).getGraph();
		} else if (query.isAskType()) {
			RDFOutput rdfOutput = new RDFOutput();
			resGraph = rdfOutput.toModel(queryExecution.execAsk()).getGraph();
		}
		queryExecution.close();

//		System.out.println("The result of the query is the following graph...");
//		Model model = ModelFactory.createModelForGraph(resGraph);
//		model.write(System.out,"N3");
//		long queryEnd = System.currentTimeMillis();
//		System.out.println("Query execution time: " + (queryEnd - queryStart) );
//		System.out.println("**** QUERY " + queryExecution.hashCode() + " END ***");

		logger.trace("Query Result: " + resGraph);
		
		logger.debug("End new graph construction in " + hashCode());
		return resGraph;
	}

/*
	@Override
	protected Graph getBaseGraph() {
		return exec();
	}
*/

}

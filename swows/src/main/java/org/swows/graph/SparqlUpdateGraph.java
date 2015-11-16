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
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.update.GraphStore;
import org.apache.jena.update.GraphStoreFactory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.log4j.Logger;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;
import org.swows.util.GraphUtils;

/**
 * The Class SparqlUpdateGraph it's the result of a
 * Update SPARQL query on an input dataset.
 * If the dataset changes the query is executed again and
 * the result updated.
 */
public class SparqlUpdateGraph extends DynamicChangingGraph {

	private UpdateRequest updateRequest;
	private DynamicDataset queryDataset;
//	private GraphStore graphStore;
//	private UpdateProcessor updateProcessor;

	private static final Logger logger = Logger.getLogger(SparqlUpdateGraph.class);
	
//	private Listener defaultGraphListener = new Listener() {
//		public void notifyUpdate(Graph source, GraphUpdate update) {
//			ExtendedIterator<Triple> addedTriples = update.getAddedGraph().find(Node.ANY, Node.ANY, Node.ANY);
//			while(addedTriples.hasNext())
//				graphStore.getDefaultGraph().add(addedTriples.next());
//			ExtendedIterator<Triple> deletedTriples = update.getAddedGraph().find(Node.ANY, Node.ANY, Node.ANY);
//			while(deletedTriples.hasNext())
//				graphStore.getDefaultGraph().delete(deletedTriples.next());
//			update();
//		}
//	};

	private Listener listener = new Listener() {
		public void notifyUpdate(Graph source, GraphUpdate update) {
			update();
		}
	};

	private void registerListener(DynamicDataset queryDataset) {
		queryDataset.getDefaultGraph()
			.getEventManager2()
			.register(listener);
		Iterator<Node> graphNodes = queryDataset.listGraphNodes();
		while (graphNodes.hasNext()) {
			DynamicGraph currGraph = queryDataset.getGraph( graphNodes.next() );
			currGraph.getEventManager2().register(listener);
		}
	}

	/**
	 * Instantiates a new sparql construct graph.
	 *
	 * @param query the query
	 * @param queryDataset the input dataset
	 */
	public SparqlUpdateGraph(UpdateRequest updateRequest, DynamicDataset queryDataset) {
		super(GraphFactory.createDefaultGraph());
		this.updateRequest = updateRequest;
		this.queryDataset = queryDataset;

		update();
		registerListener(queryDataset);
	}

	private synchronized void update() {
		logger.debug("Begin graph update in " + hashCode());
		
		DatasetGraph datasetForUpdate = GraphUtils.cloneDatasetGraph(queryDataset);
		GraphUtils.makeDynamic(datasetForUpdate);
		GraphStore graphStore = GraphStoreFactory.create(datasetForUpdate);
		
		UpdateProcessor updateProcessor = UpdateExecutionFactory.create(updateRequest, graphStore);

		//		UpdateProcessor updateProcessor = UpdateExecutionFactory.create(updateRequest, graphStore);
		do {
			logger.debug("Single update start in " + hashCode());
			updateProcessor.execute();
			logger.debug("Single update end in " + hashCode());
		} while (((DynamicGraphFromGraph) datasetForUpdate.getDefaultGraph()).sendUpdateEvents());
//		graphStore.removeGraph(SWI.ThisGraph.asNode());
		
//		System.out.println("Graph after update");
//		ModelFactory.createModelForGraph(baseGraphCopy).write(System.out, "N3");
		
		logger.debug("Update block end in " + hashCode());
		
//		System.out.println("The result of the query is the following graph...");
//		Model model = ModelFactory.createModelForGraph(resGraph);
//		model.write(System.out,"N3");
//		long queryEnd = System.currentTimeMillis();
//		System.out.println("Query execution time: " + (queryEnd - queryStart) );
//		System.out.println("**** QUERY " + queryExecution.hashCode() + " END ***");

		logger.trace("Query Result: " + baseGraph);
		
		logger.debug("End new graph construction in " + hashCode());
//		baseGraph = graphStore.getDefaultGraph();
		setBaseGraph(graphStore.getDefaultGraph());
	}

}

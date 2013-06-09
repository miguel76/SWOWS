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
import java.util.List;

import org.swows.graph.events.DelegatingDynamicGraph;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;
import org.swows.vocabulary.SWI;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.DatasetGraphMap;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.sparql.modify.GraphStoreBasic;
import com.hp.hpl.jena.sparql.modify.request.UpdateWithUsing;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.Update;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

public class UpdatableGraph2 extends DelegatingDynamicGraph {
	
	private DynamicDataset originalInputDataset;
	private UpdateRequest updateRequest;
	
	public UpdatableGraph2(
			final DynamicGraph baseGraph,
			final UpdateRequest updateRequest,
			final DynamicDataset inputDataset ) {
		Graph innerGraph = GraphFactory.createGraphMem();
		if (baseGraph != null)
			innerGraph.getBulkUpdateHandler().add(baseGraph);
		baseGraphCopy = new DynamicGraphFromGraph( innerGraph );
		this.updateRequest = updateRequest;
		this.originalInputDataset = inputDataset;

		Iterator<Node> graphNodes = inputDataset.listGraphNodes();
		Node nextGraphNode = null;
		do {
			nextGraphNode = graphNodes.hasNext() ? graphNodes.next() : null;
			final DynamicGraph eventGraph =
					nextGraphNode == null ?
							inputDataset.getDefaultGraph() :
							inputDataset.getGraph(nextGraphNode);
			eventGraph.getEventManager2().register(
					new Listener() {
						@Override
						public synchronized void notifyUpdate(Graph source, final GraphUpdate updateEvent) {
//							System.out.println("Start Add notifyUpdate");
//							System.out.println("This graph: " + baseGraphCopy);
//							System.out.println("Added graph: " +  updateEvent.getAddedGraph());
//							System.out.println("Deleted graph: " +  updateEvent.getDeletedGraph());
//							System.out.println("Default input graph: " + originalInputDataset.getDefaultGraph());
//							Iterator<Node> graphNodes = originalInputDataset.listGraphNodes();
//							while (graphNodes.hasNext()) {
//								Node graphNode = graphNodes.next();
//								System.out.println("Named input graph (" + graphNode.getURI() + "):");
//								ModelFactory.createModelForGraph(originalInputDataset.getGraph(graphNode)).write(System.out, "N3");
//							}

							update(updateEvent);
							
//							System.out.println("End of Add notifyUpdate");
						}
					} );
		} while(nextGraphNode != null);
		update();
	}
	
	private void update(GraphUpdate updateEvent) {
		DatasetGraphMap inputDataset = new DatasetGraphMap(originalInputDataset);
		GraphStore graphStore = new GraphStoreBasic(inputDataset);
		
//		Graph thisGraph = GraphFactory.createGraphMem();
//		thisGraph.getBulkUpdateHandler().add(baseGraphCopy);
		graphStore.addGraph(SWI.ThisGraph.asNode(), baseGraphCopy);
		
		if (updateEvent != null) {
//		Graph addedGraph = GraphFactory.createGraphMem();
//		addedGraph.getBulkUpdateHandler().add(updateEvent.getAddedGraph());
			graphStore.addGraph(SWI.AddedGraph.asNode(), updateEvent.getAddedGraph());
		
//		Graph deletedGraph = GraphFactory.createGraphMem();
//		deletedGraph.getBulkUpdateHandler().add(updateEvent.getDeletedGraph());
			graphStore.addGraph(SWI.DeletedGraph.asNode(), updateEvent.getDeletedGraph());
		}
		
//		GraphStore graphStore = new GraphStoreBasic(inputDataset);
		for (Update update : updateRequest.getOperations()) {
			if (update instanceof UpdateWithUsing)
				((UpdateWithUsing) update).setWithIRI(SWI.ThisGraph.asNode());
		}
		UpdateProcessor updateProcessor = UpdateExecutionFactory.create(updateRequest, graphStore);
		updateProcessor.execute();
		((DynamicGraphFromGraph) baseGraphCopy).sendUpdateEvents();
	}
	
	private void update() {
		update(null);
	}
	
	@Override
	protected DynamicGraph getBaseGraph() {
		return null; // Not used because baseGraphCopy is set in constructor
	}

}

package org.swows.graph;

import java.util.Iterator;
import java.util.List;

import org.swows.graph.events.DelegatingDynamicGraph;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;
import org.swows.vocabulary.Instance;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraphMap;
import com.hp.hpl.jena.sparql.core.DatasetGraphOne;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.sparql.modify.GraphStoreBasic;
import com.hp.hpl.jena.sparql.modify.request.UpdateAdd;
import com.hp.hpl.jena.sparql.modify.request.UpdateClear;
import com.hp.hpl.jena.sparql.modify.request.UpdateCopy;
import com.hp.hpl.jena.sparql.modify.request.UpdateCreate;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataDelete;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataInsert;
import com.hp.hpl.jena.sparql.modify.request.UpdateDeleteWhere;
import com.hp.hpl.jena.sparql.modify.request.UpdateDrop;
import com.hp.hpl.jena.sparql.modify.request.UpdateLoad;
import com.hp.hpl.jena.sparql.modify.request.UpdateModify;
import com.hp.hpl.jena.sparql.modify.request.UpdateMove;
import com.hp.hpl.jena.sparql.modify.request.UpdateVisitor;
import com.hp.hpl.jena.sparql.modify.request.UpdateWithUsing;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.Update;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

public class UpdatableFromEventsGraph2 extends DelegatingDynamicGraph {
	
	public UpdatableFromEventsGraph2(
			final DynamicGraph baseGraph,
			final List<DynamicGraph> eventGraphList,
			final List<UpdateRequest> updateList,
			final List<DynamicDataset> updateInputDatasetList ) {
		Graph innerGraph = GraphFactory.createGraphMem();
		if (baseGraph != null)
			innerGraph.getBulkUpdateHandler().add(baseGraph);
		baseGraphCopy = new DynamicGraphFromGraph( innerGraph );

		Iterator<UpdateRequest> updateIter = updateList.iterator();
		Iterator<DynamicDataset> inputDatasetIter = updateInputDatasetList.iterator();
		for (final DynamicGraph eventGraph : eventGraphList) {
			final UpdateRequest updateRequest = updateIter.next();
			final DynamicDataset originalInputDataset = inputDatasetIter.next();
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

							DatasetGraphMap inputDataset = new DatasetGraphMap(originalInputDataset);
							GraphStore graphStore = new GraphStoreBasic(inputDataset);
							
//							Graph thisGraph = GraphFactory.createGraphMem();
//							thisGraph.getBulkUpdateHandler().add(baseGraphCopy);
							graphStore.addGraph(Instance.ThisGraph.asNode(), baseGraphCopy);
							
//							Graph addedGraph = GraphFactory.createGraphMem();
//							addedGraph.getBulkUpdateHandler().add(updateEvent.getAddedGraph());
							graphStore.addGraph(Instance.AddedGraph.asNode(), updateEvent.getAddedGraph());
							
//							Graph deletedGraph = GraphFactory.createGraphMem();
//							deletedGraph.getBulkUpdateHandler().add(updateEvent.getDeletedGraph());
							graphStore.addGraph(Instance.DeletedGraph.asNode(), updateEvent.getDeletedGraph());
							
//							GraphStore graphStore = new GraphStoreBasic(inputDataset);
							for (Update update : updateRequest.getOperations()) {
								if (update instanceof UpdateWithUsing)
									((UpdateWithUsing) update).setWithIRI(Instance.ThisGraph.asNode());
							}
							UpdateProcessor updateProcessor = UpdateExecutionFactory.create(updateRequest, graphStore);
							updateProcessor.execute();
							((DynamicGraphFromGraph) baseGraphCopy).sendUpdateEvents();

//							System.out.println("End of Add notifyUpdate");
						}
					} );
		}
	}
	
	@Override
	protected DynamicGraph getBaseGraph() {
		return null; // Not used because baseGraphCopy is set in constructor
	}

}

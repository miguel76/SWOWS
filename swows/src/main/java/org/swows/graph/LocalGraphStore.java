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
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.modify.GraphStoreBasic;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

public class LocalGraphStore extends DelegatingDynamicDataset {
	
//	private DynamicDataset originalInputDataset;
	private GraphStore graphStore;
	private UpdateRequest updateRequest;
	
	// TODO: to be tested, possibly not working right now
	// TODO: baseDatasetCopy must be set (depending from management) and then check event flow
	
	public LocalGraphStore(
			final UpdateRequest updateRequest,
			final DynamicDataset inputDataset ) {
		graphStore = new GraphStoreBasic(inputDataset);
		this.updateRequest = updateRequest;

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
							update(updateEvent);
						}
					} );
		} while(nextGraphNode != null);
		update();
	}
	
	private void update(GraphUpdate updateEvent) {
		UpdateProcessor updateProcessor = UpdateExecutionFactory.create(updateRequest, graphStore);
		updateProcessor.execute();
		
		// TODO: possibly DynamicDatasetFromDataset class must be created
//		((DynamicDatasetFromDataset) baseDatasetCopy).sendUpdateEvents();
	}
	
	private void update() {
		update(null);
	}
	
	@Override
	protected DynamicDataset getBaseDataset() {
		return null; // Not used because baseGraphCopy is set in constructor
	}

}

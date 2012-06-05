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

import com.hp.hpl.jena.graph.Node;

public class RecursionDataset extends DynamicDatasetCollection {

	protected DynamicDataset ds;

	public RecursionDataset() {
		ds = new DynamicDatasetMap(new RecursionGraph(DynamicGraph.emptyGraph));
	}

	public RecursionDataset(DynamicDataset dataset) {
		ds = new DynamicDatasetMap(new RecursionGraph(dataset.getDefaultGraph()));
		Iterator<Node> graphNames = dataset.listGraphNodes();
		while (graphNames.hasNext()) {
			Node graphName = graphNames.next();
			ds.addGraph(graphName, new RecursionGraph(dataset.getGraph(graphName)));
		}
	}

	@Override
	public DynamicGraph getDefaultGraph() {
		return ds.getDefaultGraph();
	}

	public RecursionGraph getGraph(Node graphName) {
		if (ds.containsGraph(graphName))
			return (RecursionGraph) ds.getGraph(graphName);
		else {
			RecursionGraph newLocalGraph = new RecursionGraph(DynamicGraph.emptyGraph);
			ds.addGraph(graphName, newLocalGraph);
			return newLocalGraph;
		}
	}

	@Override
	public Iterator<Node> listGraphNodes() {
		return ds.listGraphNodes();
	}

	public void setBaseDataset(DynamicDataset dataset) {
//		System.out.println("New graph: " + dataset.getDefaultGraph());
		((RecursionGraph) ds.getDefaultGraph()).setBaseGraph(dataset.getDefaultGraph());
		Iterator<Node> graphNames = dataset.listGraphNodes();
		while (graphNames.hasNext()) {
			Node graphName = graphNames.next();
			if (ds.containsGraph(graphName))
				((RecursionGraph) ds.getGraph(graphName)).setBaseGraph(dataset.getGraph(graphName));
			else
				ds.addGraph(graphName, new RecursionGraph(dataset.getGraph(graphName)));
		}
	}

}

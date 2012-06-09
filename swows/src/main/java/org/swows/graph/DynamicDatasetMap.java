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
import com.hp.hpl.jena.sparql.core.DatasetGraphMap;

public class DynamicDatasetMap extends DynamicDatasetCollection {

	private DatasetGraphMap innerMap;
	
	public DynamicDatasetMap(DynamicDataset ds) {
		innerMap = new DatasetGraphMap(ds);
	}
	
	public DynamicDatasetMap(DynamicGraph g) {
		innerMap = new DatasetGraphMap(g);
	}

	@Override
	public boolean containsGraph(Node graphNode) {
		return innerMap.containsGraph(graphNode);
	}
	
	public DynamicGraph getGraph(Node graphNode) {
		return (DynamicGraph) innerMap.getGraph(graphNode);
	}

	public DynamicGraph getDefaultGraph() {
		return (DynamicGraph) innerMap.getDefaultGraph();
	}
	
	public Iterator<Node> listGraphNodes() {
		return innerMap.listGraphNodes();
	}

    public void addGraph(Node graphName, DynamicGraph graph) { 
        innerMap.addGraph(graphName, graph);
    }

    public void removeGraph(Node graphName) {
    	innerMap.removeGraph(graphName) ;
    }

    @Override
    public void setDefaultGraph(DynamicGraph g) {
    	innerMap.setDefaultGraph(g);
    }

	
}

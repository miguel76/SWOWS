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
package org.swows.producer;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.swows.graph.UpdatableFromEventsGraph2;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.spinx.QueryFactory;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;
import org.swows.vocabulary.SWI;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.update.UpdateRequest;

public class UpdatableFromEventsProducer2 extends GraphProducer {
	
	private Producer baseGraphProducer = EmptyGraphProducer.getInstance();

	private List<Producer>
			eventProducerList = new Vector<Producer>(),
			updateProducerList = new Vector<Producer>(),
			updateInputProducerList = new Vector<Producer>() ;

	/**
	 * Instantiates a new updatable from events producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public UpdatableFromEventsProducer2(Graph conf, Node confRoot, ProducerMap map) {
		Node baseGraphNode = GraphUtils.getSingleValueOptProperty(conf, confRoot, DF.baseGraph.asNode());
		if (baseGraphNode != null)
			baseGraphProducer = map.getProducer(baseGraphNode);
		Iterator<Node> inputIter = GraphUtils.getPropertyValues(conf, confRoot, DF.input.asNode());
		while( inputIter.hasNext() ) {
			Node base = inputIter.next();
			eventProducerList.add(map.getProducer(GraphUtils.getSingleValueProperty(conf, base, DF.eventsFrom.asNode())));
			updateProducerList.add(map.getProducer(GraphUtils.getSingleValueProperty(conf, base, DF.config.asNode())));
			Node inputNode = GraphUtils.getSingleValueOptProperty(conf, base, DF.input.asNode());
			updateInputProducerList.add(inputNode == null ? EmptyGraphProducer.getInstance() : map.getProducer(inputNode));
		}
	}

	@Override
	public boolean dependsFrom(Producer producer) {
		if (baseGraphProducer.equals(producer) || baseGraphProducer.dependsFrom(producer))
			return true;
		for (Producer inputProducer : eventProducerList )
			if (inputProducer.equals(producer) || inputProducer.dependsFrom(producer))
				return true;
		for (Producer inputProducer : updateProducerList )
			if (inputProducer.equals(producer) || inputProducer.dependsFrom(producer))
				return true;
		for (Producer inputProducer : updateInputProducerList )
			if (inputProducer.equals(producer) || inputProducer.dependsFrom(producer))
				return true;
		return false;
	}

	@Override
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
		DynamicGraph baseGraph = (baseGraphProducer == null) ? null : baseGraphProducer.createGraph(inputDataset); 
		List<DynamicGraph> eventGraphList = new Vector<DynamicGraph>();
		List<UpdateRequest> updateList = new Vector<UpdateRequest>();
		List<DynamicDataset> updateInputList = new Vector<DynamicDataset>();
		for (Producer eventProducer: eventProducerList) {
			eventGraphList.add(eventProducer.createGraph(inputDataset));
		}
		for (Producer updateProducer: updateProducerList) {
			updateList.add(QueryFactory.toUpdateRequest(updateProducer.createGraph(inputDataset), SWI.GraphRoot.asNode()));
		}
		for (Producer updateInputProducer: updateInputProducerList) {
			updateInputList.add(updateInputProducer.createDataset(inputDataset));
		}
		return new UpdatableFromEventsGraph2(
				baseGraph,
				eventGraphList,
				updateList,
				updateInputList );
	}

}

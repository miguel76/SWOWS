package org.swows.producer;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.swows.graph.UpdatableFromEventsGraph;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.spinx.QueryFactory;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.Instance;
import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;

public class UpdatableFromEventsProducer extends GraphProducer {
	
	private List<Producer>
			addEventProducerList = new Vector<Producer>(),
			addQueryProducerList = new Vector<Producer>(),
			addQueryInputProducerList = new Vector<Producer>(),
			deleteEventProducerList = new Vector<Producer>(),
			deleteQueryProducerList = new Vector<Producer>(),
			deleteQueryInputProducerList = new Vector<Producer>();

	/**
	 * Instantiates a new updatable from events producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public UpdatableFromEventsProducer(Graph conf, Node confRoot, ProducerMap map) {
		Iterator<Node> addConstructIter = GraphUtils.getPropertyValues(conf, confRoot, SPINX.addConstruct.asNode());
		while( addConstructIter.hasNext() ) {
			Node base = addConstructIter.next();
			addEventProducerList.add(map.getProducer(GraphUtils.getSingleValueProperty(conf, base, SPINX.eventsFrom.asNode())));
			addQueryProducerList.add(map.getProducer(GraphUtils.getSingleValueProperty(conf, base, SPINX.config.asNode())));
			Node inputNode = GraphUtils.getSingleValueOptProperty(conf, base, SPINX.input.asNode());
			addQueryInputProducerList.add(inputNode == null ? EmptyGraphProducer.getInstance() : map.getProducer(inputNode));
		}
		Iterator<Node> deleteConstructIter = GraphUtils.getPropertyValues(conf, confRoot, SPINX.deleteConstruct.asNode());
		while( deleteConstructIter.hasNext() ) {
			Node base = deleteConstructIter.next();
			deleteEventProducerList.add(map.getProducer(GraphUtils.getSingleValueProperty(conf, base, SPINX.eventsFrom.asNode())));
			deleteQueryProducerList.add(map.getProducer(GraphUtils.getSingleValueProperty(conf, base, SPINX.config.asNode())));
			Node inputNode = GraphUtils.getSingleValueOptProperty(conf, base, SPINX.input.asNode());
			deleteQueryInputProducerList.add(inputNode == null ? EmptyGraphProducer.getInstance() : map.getProducer(inputNode));
		}
	}

	@Override
	public boolean dependsFrom(Producer producer) {
		for (Producer inputProducer : addEventProducerList )
			if (inputProducer.equals(producer) || inputProducer.dependsFrom(producer))
				return true;
		for (Producer inputProducer : addQueryProducerList )
			if (inputProducer.equals(producer) || inputProducer.dependsFrom(producer))
				return true;
		for (Producer inputProducer : addQueryInputProducerList )
			if (inputProducer.equals(producer) || inputProducer.dependsFrom(producer))
				return true;
		for (Producer inputProducer : deleteEventProducerList )
			if (inputProducer.equals(producer) || inputProducer.dependsFrom(producer))
				return true;
		for (Producer inputProducer : deleteQueryProducerList )
			if (inputProducer.equals(producer) || inputProducer.dependsFrom(producer))
				return true;
		for (Producer inputProducer : deleteQueryInputProducerList )
			if (inputProducer.equals(producer) || inputProducer.dependsFrom(producer))
				return true;
		return false;
	}

	@Override
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
		List<DynamicGraph> addEventGraphList = new Vector<DynamicGraph>();
		List<Query> addQueryList = new Vector<Query>();
		List<DynamicDataset> addQueryInputList = new Vector<DynamicDataset>();
		List<DynamicGraph> deleteEventGraphList = new Vector<DynamicGraph>();
		List<Query> deleteQueryList = new Vector<Query>();
		List<DynamicDataset> deleteQueryInputList = new Vector<DynamicDataset>();
		for (Producer eventProducer: addEventProducerList) {
			addEventGraphList.add(eventProducer.createGraph(inputDataset));
		}
		for (Producer queryProducer: addQueryProducerList) {
			addQueryList.add(QueryFactory.toQuery(queryProducer.createGraph(inputDataset), Instance.GraphRoot.asNode()));
		}
		for (Producer queryInputProducer: addQueryInputProducerList) {
			addQueryInputList.add(queryInputProducer.createDataset(inputDataset));
		}
		for (Producer eventProducer: deleteEventProducerList) {
			deleteEventGraphList.add(eventProducer.createGraph(inputDataset));
		}
		for (Producer queryProducer: deleteQueryProducerList) {
			deleteQueryList.add(QueryFactory.toQuery(queryProducer.createGraph(inputDataset), Instance.GraphRoot.asNode()));
		}
		for (Producer queryInputProducer: deleteQueryInputProducerList) {
			deleteQueryInputList.add(queryInputProducer.createDataset(inputDataset));
		}
		
		return new UpdatableFromEventsGraph(
				addEventGraphList,
				addQueryList,
				addQueryInputList,
				deleteEventGraphList,
				deleteQueryList,
				deleteQueryInputList );
	}

}

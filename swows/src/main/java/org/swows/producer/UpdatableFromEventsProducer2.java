package org.swows.producer;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.swows.graph.UpdatableFromEventsGraph2;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.spinx.QueryFactory;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.Instance;
import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.update.UpdateRequest;

public class UpdatableFromEventsProducer2 extends GraphProducer {
	
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
		Iterator<Node> inputIter = GraphUtils.getPropertyValues(conf, confRoot, SPINX.input.asNode());
		while( inputIter.hasNext() ) {
			Node base = inputIter.next();
			eventProducerList.add(map.getProducer(GraphUtils.getSingleValueProperty(conf, base, SPINX.eventsFrom.asNode())));
			updateProducerList.add(map.getProducer(GraphUtils.getSingleValueProperty(conf, base, SPINX.config.asNode())));
			Node inputNode = GraphUtils.getSingleValueOptProperty(conf, base, SPINX.input.asNode());
			updateInputProducerList.add(inputNode == null ? EmptyGraphProducer.getInstance() : map.getProducer(inputNode));
		}
	}

	@Override
	public boolean dependsFrom(Producer producer) {
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
		List<DynamicGraph> eventGraphList = new Vector<DynamicGraph>();
		List<UpdateRequest> updateList = new Vector<UpdateRequest>();
		List<DynamicDataset> updateInputList = new Vector<DynamicDataset>();
		for (Producer eventProducer: eventProducerList) {
			eventGraphList.add(eventProducer.createGraph(inputDataset));
		}
		for (Producer updateProducer: updateProducerList) {
			updateList.add(QueryFactory.toUpdateRequest(updateProducer.createGraph(inputDataset), Instance.GraphRoot.asNode()));
		}
		for (Producer updateInputProducer: updateInputProducerList) {
			updateInputList.add(updateInputProducer.createDataset(inputDataset));
		}
		return new UpdatableFromEventsGraph2(
				eventGraphList,
				updateList,
				updateInputList );
	}

}

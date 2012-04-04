package org.swows.producer;

import java.util.Iterator;

import org.swows.graph.UpdatableGraph;
import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

public class UpdatableProducer extends GraphProducer {
	
	private Producer
			baseGraphProducer = EmptyGraphProducer.getInstance(),
			addGraphProducer = EmptyGraphProducer.getInstance(),
			deleteGraphProducer = EmptyGraphProducer.getInstance();

	/**
	 * Instantiates a new updatable producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public UpdatableProducer(Graph conf, Node confRoot, ProducerMap map) {
		Iterator<Triple> baseGraphTriples = conf.find(confRoot, SPINX.baseGraph.asNode(), Node.ANY);
		if (baseGraphTriples.hasNext())
			baseGraphProducer = map.getProducer(baseGraphTriples.next().getObject());
		Iterator<Triple> addGraphTriples = conf.find(confRoot, SPINX.addGraph.asNode(), Node.ANY);
		if (addGraphTriples.hasNext())
			addGraphProducer = map.getProducer(addGraphTriples.next().getObject());
		Iterator<Triple> deleteGraphTriples = conf.find(confRoot, SPINX.deleteGraph.asNode(), Node.ANY);
		if (deleteGraphTriples.hasNext())
			deleteGraphProducer = map.getProducer(deleteGraphTriples.next().getObject());
	}

	@Override
	public boolean dependsFrom(Producer producer) {
		return
				producer.equals(baseGraphProducer) || baseGraphProducer.dependsFrom(producer)
				|| producer.equals(addGraphProducer) || addGraphProducer.dependsFrom(producer)
				|| producer.equals(deleteGraphProducer) || deleteGraphProducer.dependsFrom(producer);
	}

	@Override
	public Graph createGraph(DatasetGraph inputDataset) {
		return new UpdatableGraph(
				baseGraphProducer.createGraph(inputDataset),
				addGraphProducer.createGraph(inputDataset),
				deleteGraphProducer.createGraph(inputDataset) );
	}

}

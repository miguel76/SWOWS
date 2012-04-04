package org.swows.producer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.swows.util.GraphUtils;
import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphMap;

public class InlineDatasetProducer extends DatasetProducer {
	
	Producer inputProducer = null;
	Map<Node,Producer> namedInputProducers = new HashMap<Node, Producer>();

	/**
	 * Instantiates a new inline dataset producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public InlineDatasetProducer(Graph conf, Node confRoot, final ProducerMap map) {
		inputProducer = map.getProducer( GraphUtils.getSingleValueProperty(conf, confRoot, SPINX.input.asNode()) );
		Iterator<Node> namedInputNodes = GraphUtils.getPropertyValues(conf, confRoot, SPINX.namedInput.asNode());
		while (namedInputNodes.hasNext()) {
			Node namedInputNode = namedInputNodes.next();
			Node graphNode = GraphUtils.getSingleValueProperty(conf, namedInputNode, SPINX.input.asNode());
			Node nameNode = GraphUtils.getSingleValueProperty(conf, namedInputNode, SPINX.id.asNode());
			Producer producer = map.getProducer(graphNode);
			if (producer == null) throw new RuntimeException(this + ": input graph " + graphNode + " not found ");
			namedInputProducers.put(nameNode, producer);
		}
	}
	
	@Override
	public boolean dependsFrom(Producer producer) {
		if ( producer == inputProducer || inputProducer.dependsFrom(producer) )
			return true;
		for (Producer currProducer : namedInputProducers.values())
			if ( producer == currProducer || currProducer.dependsFrom(producer) )
				return true;
		return false;
	}

	@Override
	public DatasetGraph createDataset(DatasetGraph inputDataset) {
		DatasetGraph dataset = new DatasetGraphMap(inputProducer.createGraph(inputDataset));
		for (Node currNameNode : namedInputProducers.keySet())
			dataset.addGraph( currNameNode,	namedInputProducers.get(currNameNode).createGraph(inputDataset) );
		return dataset;
	}

}

package org.swows.producer;

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

public class SelectGraphProducer extends GraphProducer {

	private Producer inputProducer;
	private Node graphNameNode;

	/**
	 * Instantiates a new select graph producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public SelectGraphProducer(Graph conf, Node confRoot, ProducerMap map) {
		this(
				map.getProducer( GraphUtils.getSingleValueProperty(conf, confRoot, SPINX.input.asNode()) ),
				GraphUtils.getSingleValueProperty(conf, confRoot, SPINX.id.asNode()) );
	}

	/**
	 * Instantiates a new select graph producer.
	 *
	 * @param inputProd the producer of the input dataset
	 * @param graphName uri identifying the graph in the dataset
	 */
	public SelectGraphProducer(Producer inputProd, Node graphNameNode) {
		this.inputProducer = inputProd;
		this.graphNameNode = graphNameNode;
	}

	@Override
	public boolean dependsFrom(Producer producer) {
		return (producer == inputProducer || inputProducer.dependsFrom(producer));
	}

	@Override
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
		return inputProducer.createDataset(inputDataset).getGraph(graphNameNode);
	}

}

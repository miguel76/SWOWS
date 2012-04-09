package org.swows.producer;

import java.util.Iterator;

import org.swows.graph.DynamicChangingGraph;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.GraphUpdate;
import org.swows.graph.events.Listener;
import org.swows.vocabulary.Instance;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

public abstract class GraphToSetFunction extends GraphFunction {

	public GraphToSetFunction(Graph conf, Node confRoot, ProducerMap map) {
		super(conf, confRoot, map);
	}

	public abstract Iterator<Node> createIterator(Graph inputGraph);

	private Graph execWorker(Graph input) {
		Iterator<Node> elements = createIterator(input);
        Graph newGraph = GraphFactory.createGraphMem();
        while (elements.hasNext()) {
        	newGraph.add(
        			new Triple(	Instance.GraphRoot.asNode(), RDFS.member.asNode(), elements.next() ) );
        }
		return newGraph;
	}

	@Override
	public DynamicGraph exec(final DynamicGraph input) {
		final DynamicChangingGraph buildingGraph = new DynamicChangingGraph(execWorker(input));
		input.getEventManager2().register(new Listener() {
			@Override
			public void notifyUpdate(Graph source, GraphUpdate update) {
				buildingGraph.setBaseGraph(execWorker(input));
			}
		} );
		return buildingGraph;
	}

}

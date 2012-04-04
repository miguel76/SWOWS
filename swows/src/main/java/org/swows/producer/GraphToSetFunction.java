package org.swows.producer;

import java.util.Iterator;

import org.swows.graph.BuildingGraph;
import org.swows.graph.PushGraphListener;
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
	public Graph exec(final Graph input) {
		final BuildingGraph buildingGraph = new BuildingGraph(execWorker(input));
		input.getEventManager().register(new PushGraphListener(input) {
			public boolean changed() {
				buildingGraph.setBaseGraph(execWorker(input), input);
				return false;
			}
		});
		return buildingGraph;
	}

}

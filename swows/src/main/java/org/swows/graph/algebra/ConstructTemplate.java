package org.swows.graph.algebra;

import org.apache.jena.graph.Capabilities;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphEventManager;
import org.apache.jena.graph.GraphStatisticsHandler;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.TransactionHandler;
import org.apache.jena.graph.Triple;
import org.apache.jena.shared.AddDeniedException;
import org.apache.jena.shared.DeleteDeniedException;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.EventManager;

public class ConstructTemplate implements DynamicGraph {
	
	DynamicGraph graph;
	BasicPattern basicPattern;
	
	public ConstructTemplate(DynamicGraph graph, BasicPattern basicPattern) {
		this.graph = graph;
		this.basicPattern = basicPattern;
	}

	public boolean dependsOn(Graph other) {
		return other == graph || graph.dependsOn(other);
	}

	public TransactionHandler getTransactionHandler() {
		throw new RuntimeException("Readonly Graph");
	}

	public Capabilities getCapabilities() {
		return ReadOnlyStreamingCapabilities.getInstance();
	}

	public GraphEventManager getEventManager() {
		return VoidEventManager.getInstance();
	}

	public GraphStatisticsHandler getStatisticsHandler() {
		return new GraphStatisticsHandler() {
					public long getStatistic(Node S, Node P, Node O) {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}

	public PrefixMapping getPrefixMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	public void add(Triple t) throws AddDeniedException {
		throw new RuntimeException("Readonly Graph");
	}

	public void delete(Triple t) throws DeleteDeniedException {
		throw new RuntimeException("Readonly Graph");
	}

	public ExtendedIterator<Triple> find(Triple m) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExtendedIterator<Triple> find(Node s, Node p, Node o) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isIsomorphicWith(Graph g) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean contains(Node s, Node p, Node o) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean contains(Triple t) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public void remove(Node s, Node p, Node o) {
		// TODO Auto-generated method stub
		
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	public EventManager getEventManager2() {
		// TODO Auto-generated method stub
		return null;
	}

}

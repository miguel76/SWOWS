package org.swows.graph.algebra;

import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.EventManager;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphStatisticsHandler;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.DeleteDeniedException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

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

	public BulkUpdateHandler getBulkUpdateHandler() {
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

	public ExtendedIterator<Triple> find(TripleMatch m) {
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

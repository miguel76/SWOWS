package org.swows.graph.events;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphStatisticsHandler;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.query.QueryHandler;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.DeleteDeniedException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class DynamicGraphFromGraph implements DynamicGraph {
	
	Graph baseGraph;
	EventManager eventManager;
	
	public DynamicGraphFromGraph(Graph graph) {
		this( graph, new SimpleEventManager(graph) );
	}

	public DynamicGraphFromGraph(Graph graph, EventManager eventManager) {
		baseGraph = graph;
		this.eventManager = eventManager;
	}

	@Override
	public boolean dependsOn(Graph other) {
		return baseGraph.dependsOn(other);
	}

	@Override
	public QueryHandler queryHandler() {
		return baseGraph.queryHandler();
	}

	@Override
	public TransactionHandler getTransactionHandler() {
		return baseGraph.getTransactionHandler();
	}

	@Override
	public BulkUpdateHandler getBulkUpdateHandler() {
		return baseGraph.getBulkUpdateHandler();
	}

	@Override
	public Capabilities getCapabilities() {
		return baseGraph.getCapabilities();
	}

	@Override
	public GraphEventManager getEventManager() {
		return baseGraph.getEventManager();
	}

	@Override
	public GraphStatisticsHandler getStatisticsHandler() {
		return baseGraph.getStatisticsHandler();
	}

	@Override
	public Reifier getReifier() {
		return baseGraph.getReifier();
	}

	@Override
	public PrefixMapping getPrefixMapping() {
		return baseGraph.getPrefixMapping();
	}

	@Override
	public void delete(Triple t) throws DeleteDeniedException {
		baseGraph.delete(t);
	}

	@Override
	public ExtendedIterator<Triple> find(TripleMatch m) {
		return baseGraph.find(m);
	}

	@Override
	public ExtendedIterator<Triple> find(Node s, Node p, Node o) {
		return baseGraph.find(s, p, o);
	}

	@Override
	public boolean isIsomorphicWith(Graph g) {
		return baseGraph.isIsomorphicWith(g);
	}

	@Override
	public boolean contains(Node s, Node p, Node o) {
		return baseGraph.contains(s, p, o);
	}

	@Override
	public boolean contains(Triple t) {
		return baseGraph.contains(t);
	}

	@Override
	public void close() {
		baseGraph.close();
	}

	@Override
	public boolean isEmpty() {
		return baseGraph.isEmpty();
	}

	@Override
	public int size() {
		return baseGraph.size();
	}

	@Override
	public boolean isClosed() {
		return baseGraph.isClosed();
	}

	@Override
	public void add(Triple t) throws AddDeniedException {
		baseGraph.add(t);
	}

	@Override
	public EventManager getEventManager2() {
		return eventManager;
	}

}

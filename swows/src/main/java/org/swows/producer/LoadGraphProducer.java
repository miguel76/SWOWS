package org.swows.producer;

import org.swows.graph.LoadGraph;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.reader.ReaderFactory;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.util.FileManager;

public class LoadGraphProducer extends GraphProducer {
	
	static {
		ReaderFactory.initialize();
	}
	
	private String filenameOrURI = null, baseURI = null, rdfSyntax = null;
	private long pollingPeriod = -1;
	
	/**
	 * Instantiates a new load graph producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public LoadGraphProducer(Graph conf, Node confRoot, ProducerMap map) {
		Node urlNode = GraphUtils.getSingleValueProperty( conf, confRoot, SPINX.url.asNode() );
		if (urlNode != null)
			filenameOrURI = urlNode.getURI();
		Node baseURINode = GraphUtils.getSingleValueOptProperty( conf, confRoot, SPINX.baseUri.asNode() );
		if (baseURINode != null)
			baseURI = baseURINode.getURI();
		Node syntaxNode = GraphUtils.getSingleValueOptProperty( conf, confRoot, SPINX.syntax.asNode() );
		if (syntaxNode != null)
			rdfSyntax = syntaxNode.getURI();
		Node pollingPeriodNode = GraphUtils.getSingleValueOptProperty( conf, confRoot, SPINX.pollingPeriod.asNode() );
		if (pollingPeriodNode != null)
			pollingPeriod = Long.parseLong(pollingPeriodNode.getLiteralLexicalForm());
	}

	public LoadGraphProducer(String filenameOrURI, String baseURI, String rdfSyntax, long pollingPeriod) {
		this.filenameOrURI = filenameOrURI;
		this.baseURI = baseURI;
		this.rdfSyntax = rdfSyntax;
		this.pollingPeriod = pollingPeriod;
	}
	
	@Override
	public boolean dependsFrom(Producer producer) {
		return false;
	}

	@Override
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
		if (pollingPeriod > 0)
			return new LoadGraph(filenameOrURI, baseURI, rdfSyntax, pollingPeriod);
		return new DynamicGraphFromGraph(
				FileManager.get().loadModel(filenameOrURI,baseURI,rdfSyntax).getGraph() );
	}

}

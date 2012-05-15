package org.swows.producer;

import org.swows.graph.JmsInputGraph;
import org.swows.graph.LoadGraph;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.reader.ReaderFactory;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.util.FileManager;

public class JmsInputGraphProducer extends GraphProducer {
	
	static {
		ReaderFactory.initialize();
	}
	
	private static final String XML_SYNTAX_URI = "http://www.swows.org/syntaxes/XML";

	private String
			url = null, user = null, password = null,
			subject = "rfid_queue", baseURI = null, syntax = XML_SYNTAX_URI;
	
	/**
	 * Instantiates a new load graph producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public JmsInputGraphProducer(Graph conf, Node confRoot, ProducerMap map) {
		Node urlNode = GraphUtils.getSingleValueProperty( conf, confRoot, SPINX.url.asNode() );
		if (urlNode != null)
			url = urlNode.getURI();
		Node userNode = GraphUtils.getSingleValueProperty( conf, confRoot, SPINX.user.asNode() );
		if (userNode != null)
			user = userNode.getURI();
		Node pwdNode = GraphUtils.getSingleValueProperty( conf, confRoot, SPINX.password.asNode() );
		if (pwdNode != null)
			password = pwdNode.getURI();
		Node subjNode = GraphUtils.getSingleValueProperty( conf, confRoot, SPINX.subject.asNode() );
		if (subjNode != null)
			subject = subjNode.getURI();
		Node baseURINode = GraphUtils.getSingleValueOptProperty( conf, confRoot, SPINX.baseUri.asNode() );
		if (baseURINode != null)
			baseURI = baseURINode.getURI();
		Node syntaxNode = GraphUtils.getSingleValueOptProperty( conf, confRoot, SPINX.syntax.asNode() );
		if (syntaxNode != null)
			syntax = syntaxNode.getURI();
	}

//	public JmsInputGraphProducer(String url, String baseURI, String rdfSyntax, long pollingPeriod) {
//		this.filenameOrURI = filenameOrURI;
//		this.baseURI = baseURI;
//		this.rdfSyntax = rdfSyntax;
//		this.pollingPeriod = pollingPeriod;
//	}
//	
	@Override
	public boolean dependsFrom(Producer producer) {
		return false;
	}

	@Override
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
		return new JmsInputGraph(url, user, password, subject, baseURI, syntax);
	}

}

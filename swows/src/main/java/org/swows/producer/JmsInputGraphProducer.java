/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open datatafloW System (SWOWS).

 * SWOWS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * SWOWS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General
 * Public License along with SWOWS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.swows.producer;

import org.swows.graph.JmsInputGraph;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.reader.ReaderFactory;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

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
		Node urlNode = GraphUtils.getSingleValueOptProperty( conf, confRoot, DF.url.asNode() );
		if (urlNode != null)
			url = urlNode.getURI();
		Node userNode = GraphUtils.getSingleValueOptProperty( conf, confRoot, DF.user.asNode() );
		if (userNode != null)
			user = userNode.getLiteralLexicalForm();
		Node pwdNode = GraphUtils.getSingleValueOptProperty( conf, confRoot, DF.password.asNode() );
		if (pwdNode != null)
			password = pwdNode.getLiteralLexicalForm();
		Node subjNode = GraphUtils.getSingleValueOptProperty( conf, confRoot, DF.subject.asNode() );
		if (subjNode != null)
			subject = subjNode.getLiteralLexicalForm();
		Node baseURINode = GraphUtils.getSingleValueOptProperty( conf, confRoot, DF.baseUri.asNode() );
		if (baseURINode != null)
			baseURI = baseURINode.getURI();
		Node syntaxNode = GraphUtils.getSingleValueOptProperty( conf, confRoot, DF.syntax.asNode() );
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

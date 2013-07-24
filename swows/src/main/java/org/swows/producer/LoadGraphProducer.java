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

import org.swows.graph.LoadGraph;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.reader.RDFDataMgr;
import org.swows.reader.ReaderFactory;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

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
		Node urlNode = GraphUtils.getSingleValueProperty( conf, confRoot, DF.url.asNode() );
		if (urlNode != null)
			filenameOrURI = urlNode.getURI();
		Node baseURINode = GraphUtils.getSingleValueOptProperty( conf, confRoot, DF.baseUri.asNode() );
		if (baseURINode != null)
			baseURI = baseURINode.getURI();
		Node syntaxNode = GraphUtils.getSingleValueOptProperty( conf, confRoot, DF.syntax.asNode() );
		if (syntaxNode != null)
			rdfSyntax = syntaxNode.getURI();
		else
			rdfSyntax = LoadGraph.guessLang(filenameOrURI);
		Node pollingPeriodNode = GraphUtils.getSingleValueOptProperty( conf, confRoot, DF.pollingPeriod.asNode() );
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
		Graph newGraph = GraphFactory.createGraphMem();
		RDFDataMgr.read(newGraph,filenameOrURI,baseURI,null);
		return new DynamicGraphFromGraph(newGraph);
		// TODO: not using rdfSyntax!
	}

}

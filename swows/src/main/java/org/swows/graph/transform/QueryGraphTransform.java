package org.swows.graph.transform;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFBase;
import org.swows.xmlinrdf.DomEncoder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.sparql.resultset.RDFOutput;
import com.hp.hpl.jena.sparql.resultset.XMLOutput;

public class QueryGraphTransform implements GraphTransform {
	
	private Query query;
	
	public QueryGraphTransform(Query query) {
		this.query = query;
	}

	@Override
	public DatasetGraph apply(DatasetGraph inputDataset) {
		QueryExecution queryExecution =
				QueryExecutionFactory.create(query, DatasetFactory.create(inputDataset));
		Graph outputGraph = null;
		
		if (query.isConstructType())
			outputGraph = queryExecution.execConstruct().getGraph();
		else if (query.isDescribeType())
			outputGraph = queryExecution.execDescribe().getGraph();
		else if (query.isSelectType()) {
			try {
//				RDFOutput rdfOutput = new RDFOutput();
				XMLOutput xmlOutput = new XMLOutput();
//				resGraph = rdfOutput.toModel(queryExecution.execSelect()).getGraph();
				final XMLReader xmlReader = XMLReaderFactory.createXMLReader();
				String baseURI = "";
				final Graph newGraph = GraphFactory.createDefaultGraph();
				StreamRDF rdfOutput = new StreamRDFBase() {
					@Override
					public void triple(Triple triple) {
						newGraph.add(triple);
					}
				};
				xmlReader.setContentHandler( DomEncoder.encode(baseURI, rdfOutput) );
//				//xmlReader.setFeature("XML 2.0", true);
				PipedInputStream pipedInputStream = new PipedInputStream();
				PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
				final InputSource xmlInputSource = new InputSource(pipedInputStream);
//				xmlInputSource.setSystemId(baseURI);
				Thread childThread = new Thread() {
					public void run() {
						try {
							xmlReader.parse(xmlInputSource);
						} catch(SAXException e) {
							e.printStackTrace();
						} catch(IOException e) {
							e.printStackTrace();
						} finally {
						}
					};
				};
				childThread.start();
				xmlOutput.format(pipedOutputStream, queryExecution.execSelect());
				while (!childThread.isAlive());
				outputGraph = newGraph;
			} catch(SAXException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}
		} else if (query.isAskType()) {
			RDFOutput rdfOutput = new RDFOutput();
			outputGraph = rdfOutput.toModel(queryExecution.execAsk()).getGraph();
		}
		queryExecution.close();
		
		if (outputGraph == null)
			throw new RuntimeException("Error in generating query result graph");
		return DatasetGraphFactory.createOneGraph(outputGraph);
	}

}

package org.swows.transformation;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFBase;
import org.swows.source.DatasetSource;
import org.swows.source.DatasetSourceFromDatasets;
import org.swows.xmlinrdf.DomEncoder;
import org.topbraid.spin.arq.ARQFactory;
import org.topbraid.spin.model.SPINFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.sparql.resultset.RDFOutput;
import com.hp.hpl.jena.sparql.resultset.XMLOutput;

public class QueryTransformation implements Transformation {
	
	private Query query;
	
	public QueryTransformation(Query query) {
		this.query = query;
	}

	public QueryTransformation(Graph queryGraph, Node queryRoot) {
		Resource rootRes = (Resource) ModelFactory.createModelForGraph(queryGraph).asRDFNode(queryRoot);
		this.query = ARQFactory.get().createQuery( SPINFactory.asQuery(rootRes) );
	}
	
	private static TransformationFactory factory =
			new TransformationFactory() {

				@Override
				public Transformation transformationFromGraph(
						Graph configGraph,
						Node configRoot) {
					return new QueryTransformation(configGraph, configRoot);
				}
		};

	public static TransformationFactory getFactory() {
		return factory;
	}
		
	public DatasetGraph exec(DatasetGraph inputDataset) {
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
		
	@Override
	public DatasetSource apply(final DatasetSource inputDatasetSource) {
		return new DatasetSourceFromDatasets(exec(inputDatasetSource.lastDataset())) {
			{
				this.registerAsSnapshotListenerTo(inputDatasetSource);
			}

			@Override
			protected void readyForExecution() {
				setNewDataset(exec(inputDatasetSource.lastDataset()));
			}
		};
	}

}

package org.swows.transformation;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.sparql.resultset.RDFOutput;
import org.apache.jena.sparql.resultset.XMLOutput;
import org.swows.source.DatasetSource;
import org.swows.source.DatasetSourceFromDatasets;
import org.swows.xmlinrdf.DomEncoder;
import org.topbraid.spin.arq.ARQFactory;
import org.topbraid.spin.model.SPINFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class Dataflow implements Transformation {
	
	private Query query;
	private DataflowComponent components[];
	
	public Dataflow(Graph configGraph, Node configRoot) {
		Resource rootRes =
				(Resource)
					ModelFactory
						.createModelForGraph(configGraph)
						.asRDFNode(configRoot);
		this.query = ARQFactory.get().createQuery( SPINFactory.asQuery(rootRes) );
	}
	
	private static TransformationFactory factory =
			new TransformationFactory() {

				@Override
				public Transformation transformationFromGraph(
						Graph configGraph,
						Node configRoot) {
					return new Dataflow(configGraph, configRoot);
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
			outputGraph = rdfOutput.asModel(queryExecution.execAsk()).getGraph();
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

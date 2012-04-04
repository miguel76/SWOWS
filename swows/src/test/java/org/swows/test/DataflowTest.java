/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open Web Server (SWOWS).

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
package org.swows.test;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.swows.datatypes.SmartFileManager;
import org.swows.producer.DataflowProducer;
import org.swows.producer.DatasetProducer;
import org.swows.producer.GraphProducer;
import org.swows.producer.Producer;
import org.swows.xmlinrdf.DomDecoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

/**
 * The Class DataflowTest is used to make some tests on  {@link DataflowProducer}.
 */
public class DataflowTest {

	/**
	 * Test1.
	 */
	void test1() {
		/*
		String baseUri = "/home/miguel/swows-test/applyOps/";

		String mainGraphUrl = baseUri + "test1.n3";
		String defaultInputUrl = baseUri + "in.n3";
		String input1Url = baseUri + "in1.n3";
		String input2Url = baseUri + "in2.n3";
		String input3Url = baseUri + "in3.n3";

	List<String> graphUris = new Vector<String>();
	graphUris.add(input1Url);
	graphUris.add(input2Url);
	graphUris.add(input3Url);
	Dataset inputDataset = DatasetFactory.create(defaultInputUrl, graphUris);

	DatasetGraph inputDatasetGraph = inputDataset.asDatasetGraph();

	Iterator<String> graphNames = inputDataset.listNames();
	System.out.println("*** Input graphs  ***");
	System.out.println("* Default Graph *");
	inputDataset.getDefaultModel().write(System.out,"N3");
	while(graphNames.hasNext()) {
		String graphName = graphNames.next();
		System.out.println("* Named Graph: " + graphName + " *");
		inputDataset.getNamedModel(graphName).write(System.out,"N3");
	}
	System.out.println("***************************************");

	Dataset wfDataset = DatasetFactory.create(mainGraphUrl);
	Graph wfGraph = wfDataset.asDatasetGraph().getDefaultGraph();

	System.out.println("*** Workflow graph  ***");
	wfDataset.getDefaultModel().write(System.out,"N3");
	System.out.println("***************************************");


		DataflowProducer applyOps = new DataflowProducer(wfGraph, null, null);

		DatasetGraph outputDatasetGraph = dsFunction.exec(inputDatasetGraph);

		Dataset outputDataset = DatasetFactory.create(outputDatasetGraph);

		graphNames = outputDataset.listNames();
		System.out.println("*** Output Graphs  ***");
		System.out.println("* Default Graph *");
		outputDataset.getDefaultModel().write(System.out,"N3");
		while(graphNames.hasNext()) {
			String graphName = graphNames.next();
			System.out.println("* Named Graph: " + graphName + " *");
			outputDataset.getNamedModel(graphName).write(System.out,"N3");
		}
		System.out.println("***************************************");
		*/
	}

	/**
	 * Test2.
	 */
	static void test2() {

		String baseUri = "/home/miguel/swows-test/applyOps/";

		String mainGraphUrl = baseUri + "test3.n3";
		String defaultInputUrl = baseUri + "test2_in.n3";
		List<String> graphUris = new Vector<String>();

		final Dataset inputDataset = DatasetFactory.create(defaultInputUrl, graphUris);
		DatasetGraph inputDatasetGraph = inputDataset.asDatasetGraph();

		Iterator<String> graphNames = inputDataset.listNames();
		System.out.println("*** Input graphs  ***");
		System.out.println("* Default Graph *");
		inputDataset.getDefaultModel().write(System.out,"N3");
		while(graphNames.hasNext()) {
			String graphName = graphNames.next();
			System.out.println("* Named Graph: " + graphName + " *");
			inputDataset.getNamedModel(graphName).write(System.out,"N3");
		}
		System.out.println("***************************************");

		Dataset wfDataset = DatasetFactory.create(mainGraphUrl);
		final Graph wfGraph = wfDataset.asDatasetGraph().getDefaultGraph();

		System.out.println("*** Workflow graph  ***");
		wfDataset.getDefaultModel().write(System.out,"N3");
		System.out.println("***************************************");

		System.out.println("*** Workflow graph in N-TRIPLE ***");
		wfDataset.getDefaultModel().write(System.out,"N-TRIPLE");
		System.out.println("***************************************");


		DataflowProducer applyOps =
			new DataflowProducer(
					new GraphProducer() {

						@Override
						public boolean dependsFrom(Producer producer) {
							return (producer == this);
						}

						@Override
						public Graph createGraph(DatasetGraph inputDs) {
							return wfGraph;
						}
					},
					new DatasetProducer() {

						@Override
						public boolean dependsFrom(Producer producer) {
							return (producer == this);
						}

						@Override
						public DatasetGraph createDataset(DatasetGraph inputDataset) {
							return inputDataset;
						}
					});

		DatasetGraph outputDatasetGraph = applyOps.createDataset(inputDatasetGraph);

		Dataset outputDataset = DatasetFactory.create(outputDatasetGraph);

		graphNames = outputDataset.listNames();
		System.out.println("*** Output Graphs  ***");
		System.out.println("* Default Graph *");
		outputDataset.getDefaultModel().write(System.out,"N3");
		while(graphNames.hasNext()) {
			String graphName = graphNames.next();
			System.out.println("* Named Graph: " + graphName + " *");
			outputDataset.getNamedModel(graphName).write(System.out,"N3");
		}
		System.out.println("***************************************");
	}

	/**
	 * Test4.
	 */
	static void test4() {

		String baseUri = "/home/miguel/swows-test/applyOps/";

		String mainGraphUrl = baseUri + "test4.n3";
		String defaultInputUrl = baseUri + "test3.n3";
		List<String> graphUris = new Vector<String>();
		String schemaUrl = "/home/miguel/TBCFreeWorkspace/Prova/spinx.n3";
		graphUris.add(schemaUrl);

		final Dataset inputDataset = DatasetFactory.create(defaultInputUrl, graphUris);
		DatasetGraph inputDatasetGraph = inputDataset.asDatasetGraph();

		Iterator<String> graphNames = inputDataset.listNames();
		System.out.println("*** Input graphs  ***");
		System.out.println("* Default Graph *");
		inputDataset.getDefaultModel().write(System.out,"N3");
		while(graphNames.hasNext()) {
			String graphName = graphNames.next();
			System.out.println("* Named Graph: " + graphName + " *");
			inputDataset.getNamedModel(graphName).write(System.out,"N3");
		}
		System.out.println("***************************************");

		Dataset wfDataset = DatasetFactory.create(mainGraphUrl);
		final Graph wfGraph = wfDataset.asDatasetGraph().getDefaultGraph();

		System.out.println("*** Workflow graph  ***");
		wfDataset.getDefaultModel().write(System.out,"N3");
		System.out.println("***************************************");

		System.out.println("*** Workflow graph in N-TRIPLE ***");
		wfDataset.getDefaultModel().write(System.out,"N-TRIPLE");
		System.out.println("***************************************");


		DataflowProducer applyOps =
			new DataflowProducer(
					new GraphProducer() {

						@Override
						public boolean dependsFrom(Producer producer) {
							return (producer == this);
						}

						@Override
						public Graph createGraph(DatasetGraph inputDs) {
							return wfGraph;
						}
					},
					new DatasetProducer() {

						@Override
						public boolean dependsFrom(Producer producer) {
							return (producer == this);
						}

						@Override
						public DatasetGraph createDataset(DatasetGraph inputDataset) {
							return inputDataset;
						}
					});

		DatasetGraph outputDatasetGraph = applyOps.createDataset(inputDatasetGraph);

		Dataset outputDataset = DatasetFactory.create(outputDatasetGraph);

		graphNames = outputDataset.listNames();
		System.out.println("*** Output Graphs  ***");
		System.out.println("* Default Graph *");
		outputDataset.getDefaultModel().write(System.out,"N3");
		while(graphNames.hasNext()) {
			String graphName = graphNames.next();
			System.out.println("* Named Graph: " + graphName + " *");
			outputDataset.getNamedModel(graphName).write(System.out,"N3");
		}
		System.out.println("***************************************");
	}

	/**
	 * Test5.
	 */
	static void test5() {

		String baseUri = "/home/miguel/TUIO/";

		String mainGraphUrl = baseUri + "test2.n3";
//		String mainGraphUrl = baseUri + "test2-1.n3";
		String defaultInputUrl = baseUri + "tuio_input.n3";
		List<String> graphUris = new Vector<String>();
		//String schemaUrl = "/home/miguel/TBCFreeWorkspace/Prova/spinx.n3";
		//graphUris.add(schemaUrl);

		final Dataset inputDataset = DatasetFactory.create(defaultInputUrl, graphUris);
		final DatasetGraph inputDatasetGraph = inputDataset.asDatasetGraph();

		Iterator<String> graphNames = inputDataset.listNames();
		System.out.println("*** Input graphs  ***");
		System.out.println("* Default Graph *");
		inputDataset.getDefaultModel().write(System.out,"N3");
		while(graphNames.hasNext()) {
			String graphName = graphNames.next();
			System.out.println("* Named Graph: " + graphName + " *");
			inputDataset.getNamedModel(graphName).write(System.out,"N3");
		}
		System.out.println("***************************************");

		Dataset wfDataset = DatasetFactory.create(mainGraphUrl, SmartFileManager.get());
		final Graph wfGraph = wfDataset.asDatasetGraph().getDefaultGraph();

		System.out.println("*** Workflow graph  ***");
		wfDataset.getDefaultModel().write(System.out,"N3");
		System.out.println("***************************************");

		System.out.println("*** Workflow graph in N-TRIPLE ***");
		wfDataset.getDefaultModel().write(System.out,"N-TRIPLE");
		System.out.println("***************************************");


		DataflowProducer applyOps =
			new DataflowProducer(
					new GraphProducer() {

						@Override
						public boolean dependsFrom(Producer producer) {
							return (producer == this);
						}

						@Override
						public Graph createGraph(DatasetGraph inputDs) {
							return wfGraph;
						}
					},
					new DatasetProducer() {

						@Override
						public boolean dependsFrom(Producer producer) {
							return (producer == this);
						}

						@Override
						public DatasetGraph createDataset(DatasetGraph inputDataset) {
							return inputDataset;
						}
					});

		DatasetGraph outputDatasetGraph = applyOps.createDataset(inputDatasetGraph);

		Dataset outputDataset = DatasetFactory.create(outputDatasetGraph);

		graphNames = outputDataset.listNames();
		System.out.println("*** Output Graphs  ***");
		System.out.println("* Default Graph *");
		outputDataset.getDefaultModel().write(System.out,"N3");
		while(graphNames.hasNext()) {
			String graphName = graphNames.next();
			System.out.println("* Named Graph: " + graphName + " *");
			outputDataset.getNamedModel(graphName).write(System.out,"N3");
		}
		System.out.println("***************************************");
	}

	/**
	 * Test6.
	 */
	static void test6() {

		String baseUri = "/home/miguel/TUIO/";

		String mainGraphUrl = baseUri + "test2.n3";
//		String mainGraphUrl = baseUri + "test2-1.n3";
		String defaultInputUrl = baseUri + "tuio_input.n3";
		List<String> graphUris = new Vector<String>();
		//String schemaUrl = "/home/miguel/TBCFreeWorkspace/Prova/spinx.n3";
		//graphUris.add(schemaUrl);

		final Dataset inputDataset = DatasetFactory.create(defaultInputUrl, graphUris);
		final DatasetGraph inputDatasetGraph = inputDataset.asDatasetGraph();

		Iterator<String> graphNames = inputDataset.listNames();
		System.out.println("*** Input graphs  ***");
		System.out.println("* Default Graph *");
		inputDataset.getDefaultModel().write(System.out,"N3");
		while(graphNames.hasNext()) {
			String graphName = graphNames.next();
			System.out.println("* Named Graph: " + graphName + " *");
			inputDataset.getNamedModel(graphName).write(System.out,"N3");
		}
		System.out.println("***************************************");

		Dataset wfDataset = DatasetFactory.create(mainGraphUrl, SmartFileManager.get());
		final Graph wfGraph = wfDataset.asDatasetGraph().getDefaultGraph();

		System.out.println("*** Workflow graph  ***");
		wfDataset.getDefaultModel().write(System.out,"N3");
		System.out.println("***************************************");

		System.out.println("*** Workflow graph in N-TRIPLE ***");
		wfDataset.getDefaultModel().write(System.out,"N-TRIPLE");
		System.out.println("***************************************");


		DataflowProducer applyOps =
			new DataflowProducer(
					new GraphProducer() {

						@Override
						public boolean dependsFrom(Producer producer) {
							return (producer == this);
						}

						@Override
						public Graph createGraph(DatasetGraph inputDs) {
							return wfGraph;
						}
					},
					new DatasetProducer() {

						@Override
						public boolean dependsFrom(Producer producer) {
							return (producer == this);
						}

						@Override
						public DatasetGraph createDataset(DatasetGraph inputDataset) {
							return inputDataset;
						}
					});

		DatasetGraph outputDatasetGraph = applyOps.createDataset(inputDatasetGraph);

		Dataset outputDataset = DatasetFactory.create(outputDatasetGraph);

		graphNames = outputDataset.listNames();
		System.out.println("*** Output Graphs  ***");
		System.out.println("* Default Graph *");
		outputDataset.getDefaultModel().write(System.out,"N3");
		while(graphNames.hasNext()) {
			String graphName = graphNames.next();
			System.out.println("* Named Graph: " + graphName + " *");
			outputDataset.getNamedModel(graphName).write(System.out,"N3");
		}
		System.out.println("***************************************");
		
//		DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
		DOMImplementation domImpl = null;
		try {
			domImpl = DOMImplementationRegistry.newInstance().getDOMImplementation("XML 2.0");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		Document xmlDoc = DomDecoder.decodeOne(outputDatasetGraph.getDefaultGraph(), domImpl);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(xmlDoc);
			StreamResult result =  new StreamResult(System.out);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		test6();
	}

}

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
package org.swows.test;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.xml.transform.TransformerException;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.RunnableQueue;
import org.apache.log4j.PropertyConfigurator;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.query.Dataset;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.BufferedGroupingRDFHandler;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriterFactory;
import org.openrdf.sail.config.SailRegistry;
import org.openrdf.sail.memory.MemoryStore;
import org.swows.datatypes.SmartFileManager;
import org.swows.function.Factory;
import org.swows.mouse.MouseApp;
import org.swows.node.Skolemizer;
import org.swows.node.Skolemizer2;
import org.swows.rio.BufferedGroupStripingRDFHandler;
import org.swows.util.GraphUtils;

public class Step2Test {

    static JFrame frame;
    static GraphicsDevice device = null;
	//static GraphicsConfiguration gc = 
	//static JFrame frame = new JFrame("SWOWS TUIO test", gc)
	static RunnableQueue batikRunnableQueue;
	static JSVGCanvas svgCanvas = new JSVGCanvas();
	static final String SCREEN = ":0.1";
	//static final String SCREEN = ":0.0";

	
	private static String readFile(String filename) throws IOException {
		FileReader fileReader = new FileReader(filename);
		StringWriter stWriter = new StringWriter();
		int chr;
		while ((chr = fileReader.read()) != -1) {
			stWriter.write(chr);
		}
		stWriter.flush();
		return stWriter.toString();
	}
	
	private static Graph execQuery(Repository repository, Dataset dataset, String queryString) {
		try {
			final GraphImpl resultGraph = new GraphImpl();
			RepositoryConnection con = repository.getConnection();
//			final List<Statement> stats = new Vector<Statement>();
			try {
				GraphQuery dfQuery = con.prepareGraphQuery(QueryLanguage.SPARQL, queryString);
				dfQuery.setDataset(dataset);
				dfQuery.evaluate(new RDFHandler() {
					@Override
					public void startRDF() throws RDFHandlerException {}
					@Override
					public void handleStatement(Statement stat) throws RDFHandlerException {
//						stats.add(stat);
						resultGraph.add(stat);
//						System.out.println(
//								"S: " + stat.getSubject() + " - " +
//								"P: " + stat.getPredicate() + " - " +
//								"O: " + stat.getObject() );
					}
					@Override
					public void handleNamespace(String arg0, String arg1)
							throws RDFHandlerException {}
					@Override
					public void handleComment(String arg0) throws RDFHandlerException {}
					@Override
					public void endRDF() throws RDFHandlerException {}
				});
			} finally {
				con.close();
			}
			return resultGraph;
		} catch (OpenRDFException e) {
			throw new RuntimeException(e);
		}
		
		
	}
	
	private static void execQueryAndPut(Repository repository, Dataset dataset, String queryString, String newContext, boolean addToExisting) {
		Graph resultGraph = execQuery(repository, dataset, queryString);
		try {
			RepositoryConnection con = repository.getConnection();
			try {
				Resource contextResource = null;
				if (!addToExisting) {
					RepositoryResult<Resource> contexts = con.getContextIDs();
					while (contexts.hasNext()) {
						Resource currContext = contexts.next();
						if (currContext.stringValue().equals(newContext))
							contextResource = currContext;
					}
					if (contextResource != null) {
						RepositoryResult<Statement> oldStats = con.getStatements(null, null, null, true, contextResource);
						GraphImpl oldGraph = new GraphImpl();
						while (oldStats.hasNext()) {
							oldGraph.add(oldStats.next());
						}
						con.remove(oldGraph, contextResource);
					}
				}
				if (contextResource == null)
					contextResource = repository.getValueFactory().createURI(newContext);
				con.add(resultGraph,contextResource);
			} finally {
				con.close();
			}
		} catch (OpenRDFException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private static void execQueryAndPut(Repository repository, Dataset dataset, String queryString, String newContext) {
		execQueryAndPut(repository, dataset, queryString, newContext, false);
	}
	
	private static void include(Repository repository, String mainUri) throws IOException {
		
		DatasetImpl dataset = new DatasetImpl();
		dataset.addDefaultGraph(repository.getValueFactory().createURI(mainUri));
		
		try {
			RepositoryConnection con = repository.getConnection();
			try {
				RepositoryResult<Resource> contexts = con.getContextIDs();
				while (contexts.hasNext())
					dataset.addNamedGraph(repository.getValueFactory().createURI(contexts.next().stringValue()));
			} finally {
				con.close();
			}
		} catch (OpenRDFException e) {
			throw new RuntimeException(e);
		}
			
		execQueryAndPut( repository, dataset, readFile("resources/sparql/includeDataflows.sparql"), mainUri );
//		execQueryAndPut( repository, dataset, readFile("resources/sparql/includeQueries.sparql"), mainUri );
//		execQueryAndPut( repository, dataset, readFile("resources/sparql/includeUpdates.sparql"), mainUri );
		execQueryAndPut( repository, dataset, readFile("resources/sparql/includeConstantGraphs.sparql"), mainUri );
		
	}

	private static void concat(Repository repository, String mainUri) throws IOException {
		
		DatasetImpl dataset = new DatasetImpl();
		dataset.addDefaultGraph(repository.getValueFactory().createURI(mainUri));
		
		try {
			RepositoryConnection con = repository.getConnection();
			try {
				RepositoryResult<Resource> contexts = con.getContextIDs();
				while (contexts.hasNext())
					dataset.addNamedGraph((URI) contexts.next());
			} finally {
				con.close();
			}
		} catch (OpenRDFException e) {
			throw new RuntimeException(e);
		}
			
		execQueryAndPut( repository, dataset, readFile("resources/sparql/concatQueries.sparql"), mainUri );
	}

//	private static void print(Repository repository, Writer writer, RDFFormat format) throws IOException {
//		try {
//			RepositoryConnection con = repository.getConnection();
//			try {
//				RepositoryResult<Resource> contexts = con.getContextIDs();
//				while (contexts.hasNext()) {
//					Resource context = contexts.next();
//					RepositoryResult<Statement> stats = con.getStatements(null, null, null, true, context);
//					RDFWriter rdfWriter = Rio.createWriter(format, writer);
//					rdfWriter.startRDF();
//					while (stats.hasNext())
//						rdfWriter.handleStatement(stats.next());
//					rdfWriter.endRDF();
//				}
//			} finally {
//				con.close();
//			}
//		} catch (OpenRDFException e) {
//			throw new RuntimeException(e);
//		}
//	}

	private static void print(
			Repository repository, Resource context,
			Writer writer, RDFFormat format) throws IOException {
		try {
			RepositoryConnection con = repository.getConnection();
			try {
				RepositoryResult<Statement> stats = con.getStatements(null, null, null, true, context);
				RDFHandler rdfWriter =
						Skolemizer2.deskolemizerHandler(
								new BufferedGroupStripingRDFHandler(
//										Rio.createWriter(format, writer)),
										new RDFXMLPrettyWriter(writer)),
								con.getValueFactory() );
				rdfWriter.startRDF();
				while (stats.hasNext())
					rdfWriter.handleStatement(stats.next());
				rdfWriter.endRDF();
			} finally {
				con.close();
			}
		} catch (OpenRDFException e) {
			throw new RuntimeException(e);
		}
	}

	private static void print(
			Repository repository, String context,
			Writer writer, RDFFormat format) throws IOException {
		print(repository, repository.getValueFactory().createURI(context), writer, format);
	}

	private static void print(
			Repository repository, Resource context,
			Writer writer) throws IOException {
		print(repository, context, writer, RDFFormat.N3);
	}

	private static void print(
			Repository repository, String context,
			Writer writer) throws IOException {
		print(repository, repository.getValueFactory().createURI(context), writer, RDFFormat.N3);
	}

	private static void print(
			Repository repository, Resource context,
			RDFFormat format) throws IOException {
		print(repository, context, new OutputStreamWriter(System.out), format);
	}
	
	private static void print(
			Repository repository, String context,
			RDFFormat format) throws IOException {
		print(repository, repository.getValueFactory().createURI(context), new OutputStreamWriter(System.out), format);
	}
	
	private static void print(Repository repository, Resource context) throws IOException {
		print(repository, context, new OutputStreamWriter(System.out));
	}

	private static void print(Repository repository, String context) throws IOException {
		print(repository, repository.getValueFactory().createURI(context));
	}

	public static void main(final String[] args) throws TransformerException, IOException, RepositoryException {
    	
    	//BasicConfigurator.configure();
        PropertyConfigurator.configure("/home/miguel/git/WorldInfo/log4j.properties");
    	

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for (int gdIndex = 0; gdIndex < gs.length; gdIndex++ ) {
        	GraphicsDevice currDevice = gs[gdIndex];
        	if (currDevice.getIDstring().equals(SCREEN))
        		device = currDevice;
        }
        device = ge.getDefaultScreenDevice(); // TODO: remove this workaround for test without screen
        GraphicsConfiguration conf = device.getDefaultConfiguration();
        
//		String baseUri = "file:///home/miguel/git/WorldInfo/dataflow/";
		String baseDir = "/home/miguel/git/WorldInfo/dataflow/";
		String baseUri = "file://" + baseDir;
//		String baseUri = "/home/dario/NetBeansProjects/provaTavolo/test/pampersoriginal/dataflow/";

		String mainGraphUrl = baseUri + "main.n3";
		String mainGraphPath = baseDir + "main.n3";
		File mainFile = new File(mainGraphPath);
		
//		Dataset wfDataset = DatasetFactory.create(mainGraphUrl, SmartFileManager.get());

//		Dataset wfDatasetTemp = DatasetFactory.create(mainGraphUrl, SmartFileManager.get());
//		Dataset wfDataset = TDBFactory.createDataset("/home/miguel/git/WorldInfo/tdb/");
//		wfDataset.getDefaultModel().add(wfDatasetTemp.getDefaultModel());
//		Iterator<String> names = wfDatasetTemp.listNames();
//		while(names.hasNext()) {
//			String name = names.next();
//			wfDataset.addNamedModel(name,wfDatasetTemp.getNamedModel(name));
//		}
		
		Repository myRepository = new SailRepository(new MemoryStore());
		myRepository.initialize();
		
		String mainUri = mainGraphUrl;
		Resource mainContext = myRepository.getValueFactory().createURI(mainUri); 
		try {
			RepositoryConnection con = myRepository.getConnection();
			try {
				SmartFileManager.loadGraph(mainGraphUrl, RDFFormat.N3, con, mainContext);
//				con.add(mainFile, baseUri, RDFFormat.N3, mainContext);
				SmartFileManager.includeAllInAGraph(con, mainContext);
			} finally {
				con.close();
			}
		} catch (OpenRDFException e) {
			throw new RuntimeException(e);
		} catch (java.io.IOException e) {
			throw new RuntimeException(e);
		}
		

		
//		System.out.println("*** Workflow graph  ***");
//		print(myRepository, mainUri);
//		System.out.println("***************************************");
		
//		System.out.println("*** SubDataflow graph  ***");
//		print(myRepository, baseUri + "svg.n3");
//		System.out.println("***************************************");
		
//		DatasetImpl dataset = new DatasetImpl();
//		try {
//			RepositoryConnection con = myRepository.getConnection();
//			try {
//				RepositoryResult<Resource> contexts = con.getContextIDs();
//				while (contexts.hasNext())
//					dataset.addNamedGraph((URI) contexts.next());
//			} finally {
//				con.close();
//			}
//		} catch (OpenRDFException e) {
//			throw new RuntimeException(e);
//		}
//
//		for (URI uri : dataset.getNamedGraphs()) {
//			System.out.println();
//			System.out.println("*** Graph: " + uri.stringValue() + " ***");
//			print(myRepository, uri);
//			System.out.println("***************************************");
//			System.out.println();
//		}
		
		include(myRepository, mainUri);
		
//		Model newWfModel = wfDataset.getDefaultModel();
//		newWfModel.write(new FileOutputStream("/home/miguel/git/WorldInfo/tmp/mainAfterInclude.n3"),"N3");

//		mainGraphUrl = "/home/miguel/git/WorldInfo/tmp/mainAfterInclude.n3";
//		Dataset wfDataset2 = DatasetFactory.create(mainGraphUrl, SmartFileManager.get());
//		Model newWfModel = wfDataset2.getDefaultModel();
//		wfDataset.setDefaultModel(newWfModel);

//    	Graph newWfGraph = newWfModel.getGraph();

//		org.swows.spinx.QueryFactory.addTextualQueries(newWfGraph);

//		query = QueryFactory.read("resources/sparql/excludeSpinx.sparql");
//    	wfDataset.setDefaultModel(newWfModel);
//		queryExecution =
//				QueryExecutionFactory.create(query, wfDataset);
//		newWfModel = queryExecution.execConstruct();
		
//		System.out.println("*** Workflow graph in N3 ***");
////		newWfModel.write(System.out,"N3");
//		print(myRepository, mainUri);
//		System.out.println("***************************************");
		
		System.out.println("*** Workflow graph in N3 ***");
		print(myRepository, mainUri, RDFFormat.RDFXML);
		System.out.println("***************************************");

//		newWfGraph = newWfModel.getGraph();
		
//		GraphUtils.deletePropertyBasedOn(newWfGraph, "http://www.swows.org/spinx");
//		GraphUtils.deletePropertyBasedOn(newWfGraph, "http://spinrdf.org/sp");

//		System.out.println("*** Workflow graph in N3 ***");
//		ModelFactory.createModelForGraph(Skolemizer.deSkolemize(newWfGraph)).write(System.out,"N3");
//		System.out.println("***************************************");

		
		//MouseApp tuioApp = 
//		new MouseApp("World Info", conf, wfGraph, false);
//		new TuioApp("SWOWS TUIO test", conf, wfGraph, false, 1024, 768, true);
		
    }	
    
}

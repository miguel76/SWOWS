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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.xml.transform.TransformerException;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.RunnableQueue;
import org.apache.log4j.PropertyConfigurator;
import org.swows.datatypes.SmartFileManager;
import org.swows.function.Factory;
import org.swows.mouse.MouseApp;
import org.swows.node.Skolemizer;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;
import org.swows.vocabulary.SP;
import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.function.FunctionRegistry;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

public class Step2Test {

    static JFrame frame;
    static GraphicsDevice device = null;
	//static GraphicsConfiguration gc = 
	//static JFrame frame = new JFrame("SWOWS TUIO test", gc)
	static RunnableQueue batikRunnableQueue;
	static JSVGCanvas svgCanvas = new JSVGCanvas();
	static final String SCREEN = ":0.1";
	//static final String SCREEN = ":0.0";
	
//	private static void include(Dataset wfDataset) {
	private static void include(GraphStore wfGraphStore) {
		
		Dataset wfDataset = DatasetFactory.create(wfGraphStore);
			
    	Query query;
		QueryExecution queryExecution;
		Model newWfModel;

//    	Query query = QueryFactory.read("resources/sparql/includeDataflows.sparql");
//		QueryExecution queryExecution =
//				QueryExecutionFactory.create(query, wfDataset);
//		Model newWfModel = queryExecution.execConstruct();
////    	wfDataset.setDefaultModel(newWfModel);
//    	wfDataset.getDefaultModel().removeAll();
//    	wfDataset.getDefaultModel().add(newWfModel);

    	UpdateRequest updateRequest = UpdateFactory.read("resources/sparql/update/includeDataflows.sparql");
    	UpdateProcessor processor = UpdateExecutionFactory.create(updateRequest, wfGraphStore);
    	processor.execute();

    	//		query = QueryFactory.read("resources/sparql/includeQueries.sparql");
//		queryExecution =
//				QueryExecutionFactory.create(query, wfDataset);
//		newWfModel = queryExecution.execConstruct();
////	   	wfDataset.setDefaultModel(newWfModel);
//    	wfDataset.getDefaultModel().removeAll();
//    	wfDataset.getDefaultModel().add(newWfModel);
    	
    	updateRequest = UpdateFactory.read("resources/sparql/update/includeQueries.sparql");
    	processor = UpdateExecutionFactory.create(updateRequest, wfGraphStore);
    	processor.execute();
		
//		query = QueryFactory.read("resources/sparql/includeUpdates.sparql");
// 		queryExecution =
//				QueryExecutionFactory.create(query, wfDataset);
//		newWfModel = queryExecution.execConstruct();
////		wfDataset.setDefaultModel(newWfModel);
//    	wfDataset.getDefaultModel().removeAll();
//    	wfDataset.getDefaultModel().add(newWfModel);
		
    	updateRequest = UpdateFactory.read("resources/sparql/update/includeUpdates.sparql");
    	processor = UpdateExecutionFactory.create(updateRequest, wfGraphStore);
    	processor.execute();
		
//		query = QueryFactory.read("resources/sparql/normalizeNamedGraphs.sparql");
//		queryExecution =
//				QueryExecutionFactory.create(query, wfDataset);
//		newWfModel = queryExecution.execConstruct();
////	   	wfDataset.setDefaultModel(newWfModel);
//    	wfDataset.getDefaultModel().removeAll();
//    	wfDataset.getDefaultModel().add(newWfModel);
		
    	updateRequest = UpdateFactory.read("resources/sparql/update/normalizeNamedGraphs.sparql");
    	processor = UpdateExecutionFactory.create(updateRequest, wfGraphStore);
    	processor.execute();
		
//		query = QueryFactory.read("resources/sparql/normalizeGroups.sparql");
//		queryExecution =
//				QueryExecutionFactory.create(query, wfDataset);
//		newWfModel = queryExecution.execConstruct();
////	   	wfDataset.setDefaultModel(newWfModel);
//    	wfDataset.getDefaultModel().removeAll();
//    	wfDataset.getDefaultModel().add(newWfModel);
		
    	updateRequest = UpdateFactory.read("resources/sparql/update/normalizeGroups.sparql");
    	processor = UpdateExecutionFactory.create(updateRequest, wfGraphStore);
    	processor.execute();
		
//		query = QueryFactory.read("resources/sparql/avoidUnconnectedElements.sparql");
//		queryExecution =
//				QueryExecutionFactory.create(query, wfDataset);
//		newWfModel = queryExecution.execConstruct();
////	   	wfDataset.setDefaultModel(newWfModel);
//    	wfDataset.getDefaultModel().removeAll();
//    	wfDataset.getDefaultModel().add(newWfModel);
		
    	updateRequest = UpdateFactory.read("resources/sparql/update/avoidUnconnectedElements.sparql");
    	processor = UpdateExecutionFactory.create(updateRequest, wfGraphStore);
    	processor.execute();
		
    	updateRequest = UpdateFactory.read("resources/sparql/update/constGraphsInBGPs.sparql");
    	processor = UpdateExecutionFactory.create(updateRequest, wfGraphStore);
    	processor.execute();
		
//		query = QueryFactory.read("resources/sparql/toQuads.sparql");
//		queryExecution =
//				QueryExecutionFactory.create(query, wfDataset);
//		newWfModel = queryExecution.execConstruct();
////		wfDataset.setDefaultModel(newWfModel);
//    	wfDataset.getDefaultModel().removeAll();
//    	wfDataset.getDefaultModel().add(newWfModel);
		
//		query = QueryFactory.read("resources/sparql/includeConstantGraphs.sparql");
//		queryExecution =
//				QueryExecutionFactory.create(query, wfDataset);
//		newWfModel = queryExecution.execConstruct();
////		wfDataset.setDefaultModel(newWfModel);
//    	wfDataset.getDefaultModel().removeAll();
//    	wfDataset.getDefaultModel().add(newWfModel);
		
	}

	private static void concat(Dataset wfDataset) {
		
    	Query query = QueryFactory.read("resources/sparql/concatQueries.sparql");
		QueryExecution queryExecution =
				QueryExecutionFactory.create(query, wfDataset);
		Model newWfModel = queryExecution.execConstruct();
		wfDataset.setDefaultModel(newWfModel);
		
	}
	
	private static void print(Model model, OutputStream outputStream) {
		model.setNsPrefix(SP.PREFIX, SP.BASE_URI);
		model.setNsPrefix("spx", SPINX.getURI());
		model.setNsPrefix("df", DF.getURI());
		model.write(outputStream,"N3");
	}

    public static void main(final String[] args) throws TransformerException, FileNotFoundException {
    	
    	//BasicConfigurator.configure();
        PropertyConfigurator.configure("/home/miguel/git/WorldInfo/log4j.properties");
    	
		FunctionRegistry registry = FunctionRegistry.get();
		registry.put(Factory.getBaseURI() + "to", Factory.getInstance());

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for (int gdIndex = 0; gdIndex < gs.length; gdIndex++ ) {
        	GraphicsDevice currDevice = gs[gdIndex];
        	if (currDevice.getIDstring().equals(SCREEN))
        		device = currDevice;
        }
        device = ge.getDefaultScreenDevice(); // TODO: remove this workaround for test without screen
        GraphicsConfiguration conf = device.getDefaultConfiguration();
        
		String baseUri = "/home/miguel/git/WorldInfo/dataflow/";
//		String baseUri = "/home/dario/NetBeansProjects/provaTavolo/test/pampersoriginal/dataflow/";

		String mainGraphUrl = baseUri + "main.n3";
		
//		Dataset wfDataset = DatasetFactory.create(mainGraphUrl, SmartFileManager.get());
//		GraphStore wfGraphStore = GraphStoreFactory.create(DatasetFactory.create(mainGraphUrl, SmartFileManager.get()));
		GraphStore wfDatasetGraph = GraphStoreFactory.create(DatasetFactory.create(mainGraphUrl, SmartFileManager.get()));

//		Dataset wfDatasetTemp = DatasetFactory.create(mainGraphUrl, SmartFileManager.get());
//		Dataset wfDataset = TDBFactory.createDataset("/home/miguel/git/WorldInfo/tdb/");
//		wfDataset.getDefaultModel().add(wfDatasetTemp.getDefaultModel());
//		Iterator<String> names = wfDatasetTemp.listNames();
//		while(names.hasNext()) {
//			String name = names.next();
//			wfDataset.addNamedModel(name,wfDatasetTemp.getNamedModel(name));
//		}
		
//		DatasetGraph wfDatasetGraph = wfDataset.asDatasetGraph();
		final Graph wfGraph = wfDatasetGraph.getDefaultGraph();
		SmartFileManager.includeAllInAGraph(wfGraph, wfDatasetGraph);
//		SmartFileManager.includeAllInAGraphSingle(wfGraph);

//		System.out.println("*** Workflow graph  ***");
//		wfDataset.getDefaultModel().write(System.out,"N3");
//		System.out.println("***************************************");

//		Iterator<String> names = wfDataset.listNames();
//		while (names.hasNext()) {
//			String name = names.next();
//			System.out.println();
//			System.out.println("*** Graph: " + name + " ***");
//			wfDataset.getNamedModel(name).write(System.out,"N3");
//			System.out.println("***************************************");
//			System.out.println();
//		}
		
		include(wfDatasetGraph);
		Model newWfModel = ModelFactory.createModelForGraph(wfDatasetGraph.getDefaultGraph());
		print( 
				newWfModel,
				new FileOutputStream("/home/miguel/git/WorldInfo/tmp/mainAfterInclude4.sk.n3") );
		print( 
				ModelFactory.createModelForGraph( Skolemizer.deSkolemize(newWfModel.getGraph()) ),
				new FileOutputStream("/home/miguel/git/WorldInfo/tmp/mainAfterInclude4.n3") );
//		ModelFactory
//		.createModelForGraph(
//				Skolemizer.deSkolemize(newWfModel.getGraph()) )
//				.write(new FileOutputStream("/home/miguel/git/WorldInfo/tmp/mainAfterInclude3.rdf"));
		
//		mainGraphUrl = "/home/miguel/git/WorldInfo/tmp/mainAfterInclude.n3";
//		Dataset wfDataset2 = DatasetFactory.create(mainGraphUrl, SmartFileManager.get());
//		Model newWfModel = wfDataset2.getDefaultModel();
//		wfDataset.setDefaultModel(newWfModel);

    	Graph newWfGraph = newWfModel.getGraph();

//		org.swows.spinx.QueryFactory.addTextualQueries(newWfGraph);

//		query = QueryFactory.read("resources/sparql/excludeSpinx.sparql");
//    	wfDataset.setDefaultModel(newWfModel);
//		queryExecution =
//				QueryExecutionFactory.create(query, wfDataset);
//		newWfModel = queryExecution.execConstruct();
		
//		System.out.println("*** Workflow graph in N3 ***");
//		newWfModel.write(System.out,"N3");
//		System.out.println("***************************************");

//		newWfGraph = newWfModel.getGraph();
		
//		GraphUtils.deletePropertyBasedOn(newWfGraph, "http://www.swows.org/spinx");
//		GraphUtils.deletePropertyBasedOn(newWfGraph, "http://spinrdf.org/sp");

		System.out.println("*** Workflow graph in N3 ***");
		ModelFactory.createModelForGraph(Skolemizer.deSkolemize(newWfGraph)).write(System.out,"N3");
		System.out.println("***************************************");

		
		//MouseApp tuioApp = 
//		new MouseApp("World Info", conf, wfGraph, false);
//		new TuioApp("SWOWS TUIO test", conf, wfGraph, false, 1024, 768, true);
		
    }	
    
}

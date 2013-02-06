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

public class Step2Test {

    static JFrame frame;
    static GraphicsDevice device = null;
	//static GraphicsConfiguration gc = 
	//static JFrame frame = new JFrame("SWOWS TUIO test", gc)
	static RunnableQueue batikRunnableQueue;
	static JSVGCanvas svgCanvas = new JSVGCanvas();
	static final String SCREEN = ":0.1";
	//static final String SCREEN = ":0.0";

    public static void main(final String[] args) throws TransformerException {
    	
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

//		String mainGraphUrl = baseUri + "test-circles.n3";
		String mainGraphUrl = baseUri + "main.n3";

		Dataset wfDataset = DatasetFactory.create(mainGraphUrl, SmartFileManager.get());
		DatasetGraph wfDatasetGraph = wfDataset.asDatasetGraph();
		final Graph wfGraph = wfDatasetGraph.getDefaultGraph();
		SmartFileManager.includeAllInAGraph(wfGraph, wfDatasetGraph);

		System.out.println("*** Workflow graph  ***");
		wfDataset.getDefaultModel().write(System.out,"N3");
		System.out.println("***************************************");

		Iterator<String> names = wfDataset.listNames();
		while (names.hasNext()) {
			String name = names.next();
			System.out.println();
			System.out.println("*** Graph: " + name + " ***");
			wfDataset.getNamedModel(name).write(System.out,"N3");
			System.out.println("***************************************");
			System.out.println();
		}
		
//    	Query query = QueryFactory.read("resources/sparql/includeQueries.sparql");
    	Query query = QueryFactory.read("resources/sparql/includeDataflows.sparql");
		QueryExecution queryExecution =
				QueryExecutionFactory.create(query, wfDataset);
		Model newWfModel = queryExecution.execConstruct();

		System.out.println("*** Workflow graph in N3 ***");
		newWfModel.write(System.out,"N3");
		System.out.println("***************************************");

		Graph newWfGraph = newWfModel.getGraph();
		
		System.out.println("*** Workflow graph in N3 ***");
		ModelFactory.createModelForGraph(Skolemizer.deSkolemize(newWfGraph)).write(System.out,"N3");
		System.out.println("***************************************");

		
		//MouseApp tuioApp = 
//		new MouseApp("World Info", conf, wfGraph, false);
//		new TuioApp("SWOWS TUIO test", conf, wfGraph, false, 1024, 768, true);
		
    }	
    
}

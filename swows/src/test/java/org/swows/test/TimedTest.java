package org.swows.test;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.xml.transform.TransformerException;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.RunnableQueue;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.swows.datatypes.SmartFileManager;
import org.swows.function.Factory;
import org.swows.time.TimedApp;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.sparql.function.FunctionRegistry;

public class TimedTest {

    static JFrame frame;
    static GraphicsDevice device = null;
	//static GraphicsConfiguration gc = 
	//static JFrame frame = new JFrame("SWOWS TUIO test", gc)
	static RunnableQueue batikRunnableQueue;
	static JSVGCanvas svgCanvas = new JSVGCanvas();
	//static final String SCREEN = ":0.1";
	//static final String SCREEN = ":0.0";

    public static void main(final String[] args) throws TransformerException {
    	
		String baseUri = "/home/miguel/pampers/dataflow/";

//		BasicConfigurator.configure();
        PropertyConfigurator.configure("/home/miguel/pampers/log4j.properties");
    	
		FunctionRegistry registry = FunctionRegistry.get();
		registry.put(Factory.getBaseURI() + "to", Factory.getInstance());

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice[] gs = ge.getScreenDevices();
//        for (int gdIndex = 0; gdIndex < gs.length; gdIndex++ ) {
//        	GraphicsDevice currDevice = gs[gdIndex];
//        	if (currDevice.getIDstring().equals(SCREEN))
//        		device = currDevice;
//        }
        device = ge.getDefaultScreenDevice();
        GraphicsConfiguration conf = device.getDefaultConfiguration();
        
//		String baseUri = "/pampers/dataflow/";

//		String mainGraphUrl = baseUri + "test-circles.n3";
		String mainGraphUrl = baseUri + "main.n3";

		Dataset wfDataset = DatasetFactory.create(mainGraphUrl, SmartFileManager.get());
		final Graph wfGraph = wfDataset.asDatasetGraph().getDefaultGraph();

//		System.out.println("*** Workflow graph  ***");
//		wfDataset.getDefaultModel().write(System.out,"N3");
//		System.out.println("***************************************");

//		System.out.println("*** Workflow graph in N-TRIPLE ***");
//		wfDataset.getDefaultModel().write(System.out,"N-TRIPLE");
//		System.out.println("***************************************");

		//TuioApp tuioApp = 
//		new TuioApp("SWOWS TUIO test", conf, wfGraph);
//		new TimedApp("ESAPACK DEMO", conf, wfGraph, false, 1024, 768);
		new TimedApp("ESAPACK DEMO", conf, wfGraph, false );
		
    }	
    
}

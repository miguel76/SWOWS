package org.swows.test;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.util.RunnableQueue;
import org.apache.log4j.BasicConfigurator;
import org.swows.datatypes.SmartFileManager;
import org.swows.function.Factory;
import org.swows.producer.DataflowProducer;
import org.swows.runnable.RunnableContext;
import org.swows.tuio.TuioGateway;
import org.swows.xmlinrdf.DomDecoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.function.FunctionRegistry;

public class TuioTestOld {

    static JFrame frame;
    static GraphicsDevice device = null;
	//static GraphicsConfiguration gc = 
	//static JFrame frame = new JFrame("SWOWS TUIO test", gc)
	static RunnableQueue batikRunnableQueue;
	static JSVGCanvas svgCanvas = new JSVGCanvas();
	static final String SCREEN = ":0.1";
	//static final String SCREEN = ":0.0";


    public static void main(final String[] args) throws TransformerException {
    	
    	BasicConfigurator.configure();
        //PropertyConfigurator.configure("log4j.properties");
    	
		FunctionRegistry registry = FunctionRegistry.get();
		registry.put(Factory.getBaseURI() + "to", Factory.getInstance());

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for (int gdIndex = 0; gdIndex < gs.length; gdIndex++ ) {
        	GraphicsDevice currDevice = gs[gdIndex];
        	if (currDevice.getIDstring().equals(SCREEN))
        		device = currDevice;
        }
        GraphicsConfiguration conf = device.getDefaultConfiguration();
//        System.out.println(conf.getBounds().width + "X" + conf.getBounds().height);
        
        frame = new JFrame("SWOWS TUIO test", conf);
    	
    	final TuioGateway tuioGateway = new TuioGateway();
    	
		String baseUri = "/home/miguel/TUIO/";

		String mainGraphUrl = baseUri + "test-circles.n3";
//		String mainGraphUrl = baseUri + "test7.n3";
		
		String defaultInputUrl = baseUri + "tuio_input.n3";
		List<String> graphUris = new Vector<String>();

		//String schemaUrl = "/home/miguel/TBCFreeWorkspace/Prova/spinx.n3";
		//graphUris.add(schemaUrl);

		final Dataset inputDataset = DatasetFactory.create(defaultInputUrl, graphUris);
		final DatasetGraph inputDatasetGraph = inputDataset.asDatasetGraph();
//		final DatasetGraph inputDatasetGraph = DatasetGraphFactory.create(tuioGateway.getGraph());

/*
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
*/

		Dataset wfDataset = DatasetFactory.create(mainGraphUrl, SmartFileManager.get());
		final Graph wfGraph = wfDataset.asDatasetGraph().getDefaultGraph();

//		System.out.println("*** Workflow graph  ***");
//		wfDataset.getDefaultModel().write(System.out,"N3");
//		System.out.println("***************************************");

//		System.out.println("*** Workflow graph in N-TRIPLE ***");
//		wfDataset.getDefaultModel().write(System.out,"N-TRIPLE");
//		System.out.println("***************************************");

		DataflowProducer applyOps =	new DataflowProducer(wfGraph, inputDatasetGraph);

		//DatasetGraph outputDatasetGraph = applyOps.createDataset(inputDatasetGraph);
		Graph outputGraph = applyOps.createGraph(inputDatasetGraph);

		/*
		Dataset outputDataset = DatasetFactory.create(outputDatasetGraph);
		Iterator<String> graphNames = outputDataset.listNames();
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
//		System.out.println("*** Output Graph  ***");
//		Model outputModel = ModelFactory.createModelForGraph(outputGraph);
//		outputModel.write(System.out,"N3");
//		System.out.println("***************************************");

/*
		DOMImplementation domImpl = null;
		try {
			domImpl = DOMImplementationRegistry.newInstance().getDOMImplementation("XML 2.0");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
*/
		
		svgCanvas = new JSVGCanvas();
		
		//svgCanvas.setSize(640,480);
        svgCanvas.setSize(conf.getBounds().width, conf.getBounds().height);
        
        // Set the JSVGCanvas listeners.
        svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
            public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
                //label.setText("Document Loading...");
            }
            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
                //label.setText("Document Loaded.");
            }
        });

        svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            public void gvtBuildStarted(GVTTreeBuilderEvent e) {
                //label.setText("Build Started...");
            }
            public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
                //label.setText("Build Done.");
//                frame.pack();
            }
        });

        svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
            public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
                //label.setText("Rendering Started...");
            }
            public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
                // Display the frame.
            	batikRunnableQueue = svgCanvas.getUpdateManager().getUpdateRunnableQueue();
            	device.setFullScreenWindow(frame);
                frame.pack();
                frame.setVisible(true);
            	tuioGateway.connect();
            }
        });
		
        // Add components to the frame.

		//frame.getContentPane().setSize(640, 480);
		frame.getContentPane().setSize(conf.getBounds().width, conf.getBounds().height);
        frame.getContentPane().add(svgCanvas);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        //frame.setSize(640, 480);
        frame.setSize(conf.getBounds().width, conf.getBounds().height);

		DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
		Document xmlDoc =
				DomDecoder.decodeOne(
						outputGraph,
						domImpl,
						new RunnableContext() {
							@Override
							public void run(Runnable runnable) {
								try {
									batikRunnableQueue.invokeAndWait(runnable);
								} catch(InterruptedException e) {
									throw new RuntimeException(e);
								}
							}
						});

        svgCanvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
		svgCanvas.setDocument(xmlDoc);


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
		
}

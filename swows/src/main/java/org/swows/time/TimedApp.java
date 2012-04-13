package org.swows.time;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.xml.transform.TransformerException;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.util.RunnableQueue;
import org.swows.datatypes.SmartFileManager;
import org.swows.graph.SingleGraphDataset;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.producer.DataflowProducer;
import org.swows.runnable.RunnableContext;
import org.swows.xmlinrdf.DomDecoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;

public class TimedApp extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RunnableQueue batikRunnableQueue;

	public TimedApp(String title, final GraphicsConfiguration gc, Graph dataflowGraph) {
		this(title, gc, dataflowGraph, true);
	}
		
	public TimedApp(String title, final GraphicsConfiguration gc, Graph dataflowGraph, final boolean fullscreen) {
		this(title, gc, dataflowGraph, fullscreen, gc.getBounds().width, gc.getBounds().height);
	}
	
	public TimedApp(
			String title, final GraphicsConfiguration gc, Graph dataflowGraph,
			final boolean fullscreen, int width, int height ) {
		super(title, gc);
    	final SystemTime systemTime = new SystemTime();
		final DynamicDataset inputDatasetGraph = new SingleGraphDataset(systemTime.getGraph());
		DataflowProducer applyOps =
				new DataflowProducer(new DynamicGraphFromGraph( dataflowGraph ), inputDatasetGraph);
		DynamicGraph outputGraph = applyOps.createGraph(inputDatasetGraph);

		final JSVGCanvas svgCanvas = new JSVGCanvas();
        svgCanvas.setSize(width,height);
        
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
            	if (fullscreen)
            		gc.getDevice().setFullScreenWindow(TimedApp.this);
                pack();
                setVisible(true);
            }
        });
		
		getContentPane().setSize(width, height);
        getContentPane().add(svgCanvas);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        setSize(width, height);

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

//        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//		Transformer transformer;
//		try {
//			transformer = transformerFactory.newTransformer();
//			DOMSource source = new DOMSource(xmlDoc);
//			StreamResult result =  new StreamResult(System.out);
//			transformer.transform(source, result);
//		} catch (TransformerException e) {
//			e.printStackTrace();
//		}

	}
	
    public static void main(final String[] args) throws TransformerException {
    	
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice device = ge.getDefaultScreenDevice();
        GraphicsConfiguration conf = device.getDefaultConfiguration();
        
        if (args.length != 3) {
        	System.out.println("Wrong Number of Arguments!");
        	System.out.println("usage: java -jar swows.jar <dataflow_uri> <window_title> F(ull screen)/W(indow)");
        	System.exit(0);
        }
		String mainGraphUrl = args[0];
		String windowTitle = args[1];
		char windowMode = args[2].charAt(0);
		
		boolean fullScreen = windowMode == 'f' || windowMode == 'F';

//		String mainGraphUrl = baseUri + "test-circles.n3";
		//String mainGraphUrl = baseUri + "main.n3";

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
		new TimedApp(windowTitle, conf, wfGraph, fullScreen);
		
    }	
}

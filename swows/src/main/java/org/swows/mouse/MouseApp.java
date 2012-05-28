package org.swows.mouse;

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
import org.swows.graph.DynamicDatasetMap;
import org.swows.graph.EventCachingGraph;
import org.swows.graph.SingleGraphDataset;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.producer.DataflowProducer;
import org.swows.runnable.RunnableContext;
import org.swows.runnable.RunnableContextFactory;
import org.swows.time.SystemTime;
import org.swows.vocabulary.Instance;
import org.swows.xmlinrdf.DocumentReceiver;
import org.swows.xmlinrdf.DomDecoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.swows.xmlinrdf.DomEventListener;

public class MouseApp extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RunnableQueue batikRunnableQueue = null;
	private EventCachingGraph cachingGraph = null;
	private boolean graphicsInitialized = false;
	private Document newDocument = null;
	private JSVGCanvas svgCanvas = null;

	public MouseApp(String title, final GraphicsConfiguration gc, Graph dataflowGraph) {
		this(title, gc, dataflowGraph, true);
	}
		
	public MouseApp(String title, final GraphicsConfiguration gc, Graph dataflowGraph, final boolean fullscreen) {
		this(title, gc, dataflowGraph, fullscreen, gc.getBounds().width, gc.getBounds().height);
	}
	
	public MouseApp(
			String title, final GraphicsConfiguration gc, Graph dataflowGraph,
			final boolean fullscreen, int width, int height) {
		super(title, gc);
		RunnableContextFactory.setDefaultRunnableContext(new RunnableContext() {
			@Override
			public synchronized void run(final Runnable runnable) {
				try {
					while (batikRunnableQueue == null || cachingGraph == null) Thread.yield();
//					while (batikRunnableQueue == null) Thread.yield();
					batikRunnableQueue.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							runnable.run();
							cachingGraph.sendEvents();
						}
					});
					if (newDocument != null && svgCanvas != null) {
						svgCanvas.setDocument(newDocument);
						newDocument = null;
						batikRunnableQueue = null;
					}
				} catch(InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});
//    	final MouseInput tuioGateway =
//    			new MouseInput(autoRefresh, new RunnableContext() {
//    				@Override
//    				public void run(Runnable runnable) {
//    					try {
//    						batikRunnableQueue.invokeAndWait(runnable);
//    					} catch(InterruptedException e) {
//    						throw new RuntimeException(e);
//    					}
//    				}
//    			});
    	final MouseInput mouseInput = new MouseInput();
    	final SystemTime systemTime = new SystemTime();
    	final DynamicDatasetMap inputDatasetGraph = new DynamicDatasetMap(systemTime.getGraph());
    	inputDatasetGraph.addGraph(Node.createURI(Instance.getURI() + "mouseEvents"), mouseInput.getGraph());
//		final DynamicDataset inputDatasetGraph = new SingleGraphDataset(mouseInput.getGraph());
		DataflowProducer applyOps =	new DataflowProducer(new DynamicGraphFromGraph(dataflowGraph), inputDatasetGraph);
		DynamicGraph outputGraph = applyOps.createGraph(inputDatasetGraph);
		cachingGraph = new EventCachingGraph(outputGraph);
//		cachingGraph = new EventCachingGraph( new LoggingGraph(outputGraph, Logger.getRootLogger(), true, true) );
		svgCanvas = new JSVGCanvas();
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
        		batikRunnableQueue = svgCanvas.getUpdateManager().getUpdateRunnableQueue();
            	if (!graphicsInitialized) {
            		// Display the frame.
            		if (fullscreen)
            			gc.getDevice().setFullScreenWindow(MouseApp.this);
            		pack();
            		setVisible(true);
//            		tuioGateway.connect();
            		graphicsInitialized = true;
            	}
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
                
		Set<DomEventListener> domEventListenerSet = new HashSet <DomEventListener>();
		domEventListenerSet.add(mouseInput);
		Map<String,Set<DomEventListener>> domEventListeners = new HashMap <String,Set<DomEventListener>>();
		domEventListeners.put("click", domEventListenerSet);
		domEventListeners.put("mousedown", domEventListenerSet);
		domEventListeners.put("mouseup", domEventListenerSet);
                
		Document xmlDoc =
				DomDecoder.decodeOne(
						cachingGraph,
//						outputGraph,
//						new LoggingGraph(cachingGraph, Logger.getRootLogger(), true, true),
						domImpl /*,
						new RunnableContext() {
							@Override
							public void run(Runnable runnable) {
								try {
									batikRunnableQueue.invokeAndWait(runnable);
								} catch(InterruptedException e) {
									throw new RuntimeException(e);
								}
							}
						} */,
						new DocumentReceiver() {
//							{
//								(new Thread() {
//									public void run() {
//										while (true) {
//											while (newDocument == null) yield();
//											RunnableQueue runnableQueue = batikRunnableQueue;
//											runnableQueue.suspendExecution(true);
//											batikRunnableQueue = null;
////											batikRunnableQueue.getThread().halt();
////											batikRunnableQueue = null;
//											svgCanvas.setDocument(newDocument);
//											newDocument = null;
//											batikRunnableQueue.resumeExecution();
//										}
//									}
//								}).start();
//							}
//							private Document newDocument = null;
							@Override
							public void sendDocument(Document doc) {
								newDocument = doc;
							}
                                                                
						},domEventListeners);

        svgCanvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);

     /*   EventTarget t = (EventTarget) xmlDoc;

        if (EventsProducer.getEventsProducer() == null) {
            try {
                             
              EventsProducer.setEventsProducer();      
            } catch (java.lang.ExceptionInInitializerError ex) {
                ex.printStackTrace();
                ex.getCause();
            }
        }

       t.addEventListener("click", new EventListener() {

            public void handleEvent(Event evt) {
                EventsProducer.getEventsProducer().update(evt);
                
            }
        }, false);

*/
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
        	System.out.println("usage: java -jar swows-tuio.jar <dataflow_uri> <window_title> F(ull screen)/W(indow)");
        	System.exit(0);
        }
		String mainGraphUrl = args[0];
		String windowTitle = args[1];
		char windowMode = args[2].charAt(0);
		
		boolean fullScreen = windowMode == 'f' || windowMode == 'F';

		Dataset wfDataset = DatasetFactory.create(mainGraphUrl, SmartFileManager.get());
		final Graph wfGraph = wfDataset.asDatasetGraph().getDefaultGraph();

		new MouseApp(windowTitle, conf, wfGraph, fullScreen);
		
    }	

}
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
package org.swows.file;

import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.xml.transform.TransformerException;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.util.RunnableQueue;
import org.swows.graph.DynamicDatasetMap;
import org.swows.graph.EventCachingGraph;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.producer.DataflowProducer;
import org.swows.runnable.RunnableContext;
import org.swows.runnable.RunnableContextFactory;
import org.swows.time.SystemTime;
import org.swows.xmlinrdf.DocumentReceiver;
import org.swows.xmlinrdf.DomDecoder2;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;

public class FileApp extends JFrame {
	
	// TODO: still work to be done (if this class is usefull)

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RunnableQueue batikRunnableQueue = null;
	private EventCachingGraph cachingGraph = null;
	private Document newDocument = null;

	public FileApp(String title, final GraphicsConfiguration gc, Graph dataflowGraph) {
		this(title, gc, dataflowGraph, true);
	}
		
	public FileApp(String title, final GraphicsConfiguration gc, Graph dataflowGraph, Color bgColor) {
		this(title, gc, dataflowGraph, true, bgColor);
	}
		
	public FileApp(String title, final GraphicsConfiguration gc, Graph dataflowGraph, final boolean fullscreen) {
		this(title, gc, dataflowGraph, fullscreen, gc.getBounds().width, gc.getBounds().height, null);
	}
	
	public FileApp(String title, final GraphicsConfiguration gc, Graph dataflowGraph, final boolean fullscreen, Color bgColor) {
		this(title, gc, dataflowGraph, fullscreen, gc.getBounds().width, gc.getBounds().height, bgColor);
	}
	
	public FileApp(
			String title, final GraphicsConfiguration gc, Graph dataflowGraph,
			final boolean fullscreen, int width, int height) {
		this(title, gc, dataflowGraph, fullscreen, width, height, null);
	}
	
	public FileApp(
			String title, final GraphicsConfiguration gc, Graph dataflowGraph,
			final boolean fullscreen, int width, int height, Color bgColor) {
		super(title, gc);
		RunnableContextFactory.setDefaultRunnableContext(new RunnableContext() {
			@Override
			public synchronized void run(final Runnable runnable) {
				try {
					while (batikRunnableQueue == null || cachingGraph == null) Thread.yield();
//					while (batikRunnableQueue == null) Thread.yield();
					final long start = System.currentTimeMillis();
					batikRunnableQueue.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							long runEntered = System.currentTimeMillis();
							System.out.println(
									"Update thread launched in "
											+ (runEntered - start) + "ms" );
							runnable.run();
							long afterCascade = System.currentTimeMillis();
							System.out.println(
									"RDF envent cascade executed in "
											+ (afterCascade - runEntered) + "ms" );
							cachingGraph.sendEvents();
							long afterSvgDom = System.currentTimeMillis();
							System.out.println(
									"SVG DOM updated in "
											+ (afterSvgDom - afterCascade) + "ms" );
						}
					});
					long runFinished = System.currentTimeMillis();
					System.out.println(
							"SVG updated and repainted in "
									+ (runFinished - start + "ms" ) );
					if (newDocument != null) {
						batikRunnableQueue = null;
//						Document doc = newDocument;
						newDocument = null;
//						svgCanvas.setDocument(doc);
					}
				} catch(InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});
    	final SystemTime systemTime = new SystemTime();
    	final DynamicDatasetMap inputDatasetGraph = new DynamicDatasetMap(systemTime.getGraph());
//    	inputDatasetGraph.addGraph(NodeFactory.createURI(SWI.getURI() + "mouseEvents"), mouseInput.getGraph());
//		final DynamicDataset inputDatasetGraph = new SingleGraphDataset(mouseInput.getGraph());
		DataflowProducer applyOps =	new DataflowProducer(new DynamicGraphFromGraph(dataflowGraph), inputDatasetGraph);
		DynamicGraph outputGraph = applyOps.createGraph(inputDatasetGraph);
		cachingGraph = new EventCachingGraph(outputGraph);
//		cachingGraph = new EventCachingGraph( new LoggingGraph(outputGraph, Logger.getRootLogger(), true, true) );

		DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
                
//		Set<DomEventListener> domEventListenerSet = new HashSet <DomEventListener>();
//		domEventListenerSet.add(mouseInput);
//		Map<String,Set<DomEventListener>> domEventListeners = new HashMap <String,Set<DomEventListener>>();
//		domEventListeners.put("click", domEventListenerSet);
//		domEventListeners.put("mousedown", domEventListenerSet);
//		domEventListeners.put("mouseup", domEventListenerSet);
                
		Document xmlDoc =
				DomDecoder2.decodeOne(
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
                                                                
						} ); //,domEventListeners);

        
        DOMImplementation implementation = null;
		try {
			implementation = DOMImplementationRegistry.newInstance()
					.getDOMImplementation("XML 3.0");
		} catch (ClassCastException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
      	DOMImplementationLS feature = (DOMImplementationLS) implementation.getFeature("LS",
        		"3.0");
        LSSerializer serializer = feature.createLSSerializer();
        LSOutput output = feature.createLSOutput();
        output.setByteStream(System.out);
        serializer.write(xmlDoc, output);

	}
        
    public static void main(final String[] args) throws TransformerException {
    	
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice device = ge.getDefaultScreenDevice();
        GraphicsConfiguration conf = device.getDefaultConfiguration();
        
        if (args.length != 4) {
        	System.out.println("Wrong Number of Arguments!");
        	System.out.println("usage: java -jar swows-mouse.jar <dataflow_uri> <window_title> F(ull screen)/W(indow) <bg_color>");
        	System.exit(0);
        }
		String mainGraphUrl = args[0];
		String windowTitle = args[1];
		char windowMode = args[2].charAt(0);
		
		Color color = Color.decode(args[3]);
		
		boolean fullScreen = windowMode == 'f' || windowMode == 'F';

		Dataset wfDataset = DatasetFactory.create(mainGraphUrl);
		final Graph wfGraph = wfDataset.asDatasetGraph().getDefaultGraph();

		new FileApp(windowTitle, conf, wfGraph, fullScreen, color);
		
    }	

}

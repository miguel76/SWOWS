package org.swows.graph;

import java.util.Timer;
import java.util.TimerTask;

import org.swows.reader.ReaderFactory;
import org.swows.runnable.LocalTimer;
import org.swows.runnable.RunnableContext;
import org.swows.runnable.RunnableContextFactory;

import com.hp.hpl.jena.util.FileManager;

public class LoadGraph extends DynamicChangingGraph {
	
	static {
		ReaderFactory.initialize();
	}
	
	private String filenameOrURI, baseURI, rdfSyntax;
//	private long pollingPeriod;

	public LoadGraph(String filenameOrURI, String baseURI, String rdfSyntax, final long pollingPeriod) {
		this.filenameOrURI = filenameOrURI;
		this.baseURI = baseURI;
		this.rdfSyntax = rdfSyntax;
//		this.pollingPeriod = pollingPeriod;
		this.baseGraph = FileManager.get().loadModel(filenameOrURI,baseURI,rdfSyntax).getGraph();
		if (pollingPeriod > 0) {
			final RunnableContext runnableCtxt = RunnableContextFactory.getDefaultRunnableContext();
//			Timer updateTimer = new Timer();
			Timer updateTimer = LocalTimer.get();
//			(new Thread() {
//				@Override
//				public void run() {
//					try {
//						while(true) {
//							sleep(pollingPeriod);
//							runnableCtxt.run(new Runnable() {
//								@Override
//								public void run() {
//									update();
//								}
//							});
//							Thread.yield();
//						}
//					} catch(InterruptedException e) {
//						throw new RuntimeException(e);
//					}
//				}
//			}).run();
			updateTimer.schedule( new TimerTask() {
				@Override
				public void run() {
					runnableCtxt.run(new Runnable() {
						@Override
						public void run() {
							update();
						}
					});
				}
			}, pollingPeriod, pollingPeriod);
		}
	}
	
	private void update() {
		setBaseGraph( FileManager.get().loadModel(filenameOrURI,baseURI,rdfSyntax).getGraph() );
	}

}

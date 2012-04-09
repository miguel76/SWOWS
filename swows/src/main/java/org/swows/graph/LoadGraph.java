package org.swows.graph;

import java.util.Timer;
import java.util.TimerTask;

import org.swows.runnable.LocalTimer;

import com.hp.hpl.jena.util.FileManager;

public class LoadGraph extends DynamicChangingGraph {
	
	private String filenameOrURI, baseURI, rdfSyntax;
//	private long pollingPeriod;

	public LoadGraph(String filenameOrURI, String baseURI, String rdfSyntax, long pollingPeriod) {
		this.filenameOrURI = filenameOrURI;
		this.baseURI = baseURI;
		this.rdfSyntax = rdfSyntax;
//		this.pollingPeriod = pollingPeriod;
		this.baseGraph = FileManager.get().loadModel(filenameOrURI,baseURI,rdfSyntax).getGraph();
		if (pollingPeriod > 0) {
//			Timer updateTimer = new Timer();
			Timer updateTimer = LocalTimer.get();
			updateTimer.schedule( new TimerTask() {
				@Override
				public void run() {
					update();
				}
			}, 0, pollingPeriod);
		}
	}
	
	private void update() {
		setBaseGraph( FileManager.get().loadModel(filenameOrURI,baseURI,rdfSyntax).getGraph() );
	}

}

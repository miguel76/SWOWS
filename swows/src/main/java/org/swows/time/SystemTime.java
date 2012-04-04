package org.swows.time;

import java.util.TimerTask;

import org.swows.runnable.LocalTimer;
import org.swows.vocabulary.Instance;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEvents;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

public class SystemTime extends TimerTask {
	
	private static final int DEFAULT_PERIOD = 5;
	
	private long updatePeriod;
	private Graph timeGraph;
	private Triple currTriple;
		
	public SystemTime() {
		this(DEFAULT_PERIOD);
	}
	
	public SystemTime(long updatePeriod) {
		this.updatePeriod = updatePeriod;
	}
	
	public void run() {
		Triple newTriple = tripleFromTime();
		timeGraph.getEventManager().notifyEvent(timeGraph, GraphEvents.startRead);
		timeGraph.delete(currTriple);
		timeGraph.add(newTriple);
		timeGraph.getEventManager().notifyEvent(timeGraph, GraphEvents.finishRead);
	}
	
//	private static Triple tripleFromTime(long time) {
//		return new Triple(
//				Instance.GraphRoot.asNode(),
//				org.swows.vocabulary.time.systemTime.asNode(),
//				Node.createLiteral( "" + time, (String) null, XSDDatatype.XSDinteger ) );
//	}
	
	private static Triple tripleFromTime() {
		return new Triple(
				Instance.GraphRoot.asNode(),
				org.swows.vocabulary.time.systemTime.asNode(),
				Node.createLiteral( "" + System.currentTimeMillis(), (String) null, XSDDatatype.XSDinteger ) );
	}
	
	public Graph getGraph() {
		timeGraph = GraphFactory.createGraphMem();
		currTriple = tripleFromTime();
		timeGraph.add(currTriple);
		if (timeGraph == null) {
			LocalTimer.get().schedule(this, 0, updatePeriod);
		}
		return timeGraph;
	}

}

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
package org.swows.time;

import java.util.Timer;
import java.util.TimerTask;

import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.runnable.RunnableContextFactory;
import org.swows.vocabulary.SWI;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

public class SystemTime implements Runnable {
	
	private static final int DEFAULT_PERIOD = 5;
	
	private long updatePeriod;
	private DynamicGraphFromGraph timeGraph;
	private Triple currTriple;
		
	public SystemTime() {
		this(DEFAULT_PERIOD);
	}
	
	public SystemTime(long updatePeriod) {
		this.updatePeriod = updatePeriod;
	}
	
	public void run() {
		Triple newTriple = tripleFromTime();
		timeGraph.delete(currTriple);
		timeGraph.add(newTriple);
		timeGraph.sendUpdateEvents();
		currTriple = newTriple;
	}
	
//	private static Triple tripleFromTime(long time) {
//		return new Triple(
//				Instance.GraphRoot.asNode(),
//				org.swows.vocabulary.time.systemTime.asNode(),
//				Node.createLiteral( "" + time, (String) null, XSDDatatype.XSDinteger ) );
//	}
	
	private static Triple tripleFromTime() {
		long time = System.currentTimeMillis();
		return new Triple(
				SWI.GraphRoot.asNode(),
				org.swows.vocabulary.TIME.systemTime.asNode(),
				Node.createLiteral( String.format("%d.%03d", time / 1000, time % 1000 ), (String) null, XSDDatatype.XSDdecimal ) );
//				Node.createLiteral( "" + System.currentTimeMillis(), (String) null, XSDDatatype.XSDinteger ) );
	}
	
	public DynamicGraph getGraph() {
		timeGraph = new DynamicGraphFromGraph( GraphFactory.createGraphMem() );
		currTriple = tripleFromTime();
		timeGraph.add(currTriple);
//		if (timeGraph == null) {
//		LocalTimer.get().schedule(this, 0, updatePeriod);
//		final RunnableContext runnableCtxt = RunnableContextFactory.getDefaultRunnableContext();
		Timer updateTimer = new Timer();
//		Timer updateTimer = LocalTimer.get();
		updateTimer.schedule( new TimerTask() {
			@Override
			public void run() {
//				runnableCtxt.run(SystemTime.this);
				RunnableContextFactory.getDefaultRunnableContext().run(SystemTime.this);
			}
		}, 0, updatePeriod);
//		}
		return timeGraph;
	}

}

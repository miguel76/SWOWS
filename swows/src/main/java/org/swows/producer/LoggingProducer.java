/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open Web Server (SWOWS).

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
package org.swows.producer;

import org.apache.log4j.Logger;
import org.swows.graph.LoggingGraph;
import org.swows.graph.events.DynamicGraph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

/**
 * The Class LoggingProducer connects to another producer
 * logging created graphs or datasets and their updates.
 */
//public class LoggingProducer implements Producer {
public class LoggingProducer extends GraphFunction {

	//private Producer connectedProducer;
	private Logger logger;
	private boolean initialGraphDebug, graphUpdateDebug;

	/**
	 * Instantiates a new logging producer.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public LoggingProducer(Graph conf, Node confRoot, ProducerMap map) {
		super(conf, confRoot, map);
		logger = (confRoot.isURI())
					? Logger.getLogger(confRoot.getURI())
					: Logger.getRootLogger();
		initialGraphDebug = true;
		graphUpdateDebug = true;
	}

	/**
	 * Instantiates a new logging producer.
	 *
	 * @param connectedProducer the connected producer
	 */
	public LoggingProducer(Producer connectedProducer) {
		this(connectedProducer, Logger.getRootLogger());
	}

	/**
	 * Instantiates a new logging producer.
	 *
	 * @param connectedProducer the connected producer
	 * @param logger the log4j logger
	 */
	public LoggingProducer(Producer connectedProducer, Logger logger) {
		this(connectedProducer, logger, true, true);
	}

	/**
	 * Instantiates a new logging producer.
	 *
	 * @param connectedProducer the connected producer
	 * @param logger the log4j logger
	 * @param initialGraphDebug if true the initial graphs will be traced  
	 * @param graphUpdateDebug if true the graph updates will be debugged/traced  
	 */
	public LoggingProducer(Producer connectedProducer, Logger logger, boolean initialGraphDebug, boolean graphUpdateDebug) {
		super(connectedProducer);
		this.logger = logger;
		this.initialGraphDebug = initialGraphDebug;
		this.graphUpdateDebug = graphUpdateDebug;
	}

//	/* (non-Javadoc)
//	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
//	 */
//	@Override
//	public boolean dependsFrom(Producer producer) {
//		return (producer == connectedProducer || connectedProducer.dependsFrom(producer));
//	}
//
//	/* (non-Javadoc)
//	 * @see org.swows.producer.Producer#createGraph(com.hp.hpl.jena.sparql.core.DatasetGraph)
//	 */
//	@Override
//	public Graph createGraph(DatasetGraph inputDataset) {
//		Graph newGraph = connectedProducer.createGraph(inputDataset);
//		return
//				(logger.isDebugEnabled())
//					? new LoggingGraph(newGraph, logger, initialGraphDebug, graphUpdateDebug )
//					: newGraph;
//	}

	@Override
	public DynamicGraph exec(DynamicGraph input) {
		return
				(logger.isDebugEnabled())
					? new LoggingGraph(input, logger, initialGraphDebug, graphUpdateDebug )
					: input;
	}

//	/* (non-Javadoc)
//	 * @see org.swows.producer.Producer#createDataset(com.hp.hpl.jena.sparql.core.DatasetGraph)
//	 */
//	@Override
//	public DatasetGraph createDataset(DatasetGraph inputDataset) {
//		DatasetGraph newDataset = connectedProducer.createDataset(inputDataset);
//		return
//				(logger.isDebugEnabled())
//					? new LoggingGraph(newGraph, logger, initialGraphDebug, graphUpdateDebug )
//					: newDataset;
//	}

}

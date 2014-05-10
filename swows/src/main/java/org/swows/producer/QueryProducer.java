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
package org.swows.producer;

import org.swows.graph.events.DynamicDataset;

import com.hp.hpl.jena.query.Query;

/**
 * The Interface Query Producer is implemented by all the classes
 * that generate queries in a dataflow.
 * Implementing classes, a part from implementing Producer
 * methods, must implement a constructor with three parameters:<ul>
 * <li>the {@link com.hp.hpl.jena.graph.Graph} with dataflow definition</li>
 * <li>the specific {@link com.hp.hpl.jena.graph.Node} in
 * the graph representing the producer configuration</li>
 * <li>the {@link ProducerMap} to access the other defined
 * producers</li>
 * </ul>
 */
public interface QueryProducer {

//	public void build(Graph conf, Node confRoot, ProducerMap map);

	/**
	 * Generates a query.
	 *
	 * @param inputDataset the input dataset of the containing dataflow
	 * @return the created graph
	 */
	public Query createQuery(DynamicDataset inputDataset);
	

}

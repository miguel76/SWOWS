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
package org.swows.producer.old;

import org.apache.jena.query.Query;
import org.swows.graph.events.DynamicDataset;
import org.swows.producer.ProducerMap;
import org.swows.transformation.QueryTransformation;
import org.swows.transformation.Transformation;

/**
 * The Abstract Class Query Producer is implemented by all the classes
 * that generate queries.
 * Implementing classes, a part from implementing Producer
 * methods, must implement a constructor with three parameters:<ul>
 * <li>the {@link org.apache.jena.graph.Graph} with dataflow definition</li>
 * <li>the specific {@link org.apache.jena.graph.Node} in
 * the graph representing the producer configuration</li>
 * <li>the {@link ProducerMap} to access the other defined
 * producers</li>
 * </ul>
 */
public abstract class QueryProducer implements TransformationProducer {

//	public void build(Graph conf, Node confRoot, ProducerMap map);

	/**
	 * Generates a query.
	 *
	 * @param inputDataset the input dataset of the containing dataflow
	 * @return the created graph
	 */
	public abstract Query createQuery(DynamicDataset inputDataset);
	
	@Override
	public Transformation createGraphTransform(DynamicDataset inputDataset) {
		return new QueryTransformation(createQuery(inputDataset));
	}

}

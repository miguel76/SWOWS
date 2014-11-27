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
import org.swows.transformation.Transformation;

/**
 * The Interface TransformationProducer may be implemented by classes
 * that generate different kinds of graph transformations.
 */
public interface TransformationProducer {

	/**
	 * Generates a transformation.
	 *
	 * @param inputDataset the input dataset of the containing dataflow
	 * @return the created graph
	 */
	public Transformation createGraphTransform(DynamicDataset inputDataset);
	
}

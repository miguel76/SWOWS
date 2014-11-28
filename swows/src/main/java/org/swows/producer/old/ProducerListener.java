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

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;

/**
 * The listener interface for receiving producer events.
 * The class that is interested in processing a producer
 * event implements this interface, and the object created
 * with that class is registered with a {@code NotifyingProducer} using the
 * {@code addProducerListener} method. When a producer event occurs,
 * that object's appropriate method is invoked.
 *
 * @see NotifyingProducer
 */
public interface ProducerListener {

	/**
	 * Notify graph creation.
	 *
	 * @param inputDataset the input dataset
	 * @param graph the created graph
	 */
	public void notifyGraphCreation(DynamicDataset inputDataset, DynamicGraph graph);

	/**
	 * Notify dataset creation.
	 *
	 * @param inputDataset the input dataset
	 * @param dataset the created dataset
	 */
	public void notifyDatasetCreation(DynamicDataset inputDataset, DynamicDataset dataset);

}

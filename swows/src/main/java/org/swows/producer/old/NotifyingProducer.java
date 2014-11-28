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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;

/**
 * The Class NotifyingProducer encapsulates another
 * producer adding notification to registered listeners.
 * Whenever {@code createGraph} or {@code createDataset}
 * are called the corresponding notification method is
 * called for every registered listener.
 * @see ProducerListener
 */
public class NotifyingProducer implements RDFProducer {

	private RDFProducer innerProducer;

	private Set<ProducerListener> listenersSet = null;

	/**
	 * Instantiates a new notifying producer.
	 *
	 * @param innerProducer the inner producer
	 */
	public NotifyingProducer(RDFProducer innerProducer) {
		this.innerProducer = innerProducer;
	}

	/**
	 * Register listener.
	 *
	 * @param newListener the new listener
	 */
	public void registerListener(ProducerListener newListener) {
		if (listenersSet == null) {
			listenersSet = new HashSet<ProducerListener>();
		}
		listenersSet.add(newListener);
	}

	/**
	 * Unregister listener.
	 *
	 * @param listener the listener
	 */
	public void unregisterListener(ProducerListener listener) {
		if (listenersSet != null) {
			listenersSet.remove(listener);
			if (listenersSet.isEmpty())
				listenersSet = null;
		}
	}

	private void notifyGraphCreation(DynamicDataset datasetInput, DynamicGraph newGraph) {
		if (listenersSet != null) {
			Iterator<ProducerListener> listeners = listenersSet.iterator();
			while (listeners.hasNext()) {
				listeners.next().notifyGraphCreation(datasetInput, newGraph);
			}
		}
	}

	private void notifyDatasetCreation(DynamicDataset datasetInput, DynamicDataset newDataset) {
		if (listenersSet != null) {
			Iterator<ProducerListener> listeners = listenersSet.iterator();
			while (listeners.hasNext()) {
				listeners.next().notifyDatasetCreation(datasetInput, newDataset);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#createDataset(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	public DynamicDataset createDataset(DynamicDataset inputDataset) {
		DynamicDataset innerDataset = innerProducer.createDataset(inputDataset);
		notifyDatasetCreation(inputDataset, innerDataset);
		return innerDataset;
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#createGraph(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	public DynamicGraph createGraph(DynamicDataset inputDataset) {
		DynamicGraph innerGraph = innerProducer.createGraph(inputDataset);
		notifyGraphCreation(inputDataset, innerGraph);
		return innerGraph;
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
	 */
	public boolean dependsFrom(RDFProducer producer) {
		return (producer == innerProducer || innerProducer.dependsFrom(producer));
	}

}

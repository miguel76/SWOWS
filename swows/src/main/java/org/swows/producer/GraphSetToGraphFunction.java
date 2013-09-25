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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.vocabulary.DF;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * The Abstract Class GraphSetToGraphFunction is the
 * common ancestor of producer classes that operate on an
 * input set of graphs and generate a graph in output.
 * Concrete classes must implement the {@code exec} method.
 */
public abstract class GraphSetToGraphFunction extends GraphProducer {

/*
	public static ProducerFactory factory(final Class<GraphSetToGraphFunction> clazz) {
		return new ProducerFactory() {
			@Override
			public Producer createProducer(Graph conf, Node confRoot,
					ExecutionContext context) {
				return clazz
							.getConstructor(Graph.class, Node.class, ProducerMap.class)
							.newInstance(conf, confRoot, context);
			}
		};

	}

	public static ProducerFactory getFactory() {
		final Class<ProducerFactory> clazz = ;
		return new ProducerFactory() {
			@Override
			public Producer createProducer(Graph conf, Node confRoot,
					ExecutionContext context) {
				return clazz
							.getConstructor(Graph.class, Node.class, ProducerMap.class)
							.newInstance(conf, confRoot, context);
			}
		};

	}
*/

	private List<Producer> producerList;

	/**
	 * Instantiates a new graph set to graph function.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public GraphSetToGraphFunction(Graph conf, Node confRoot, ProducerMap map) {
		Model confModel = ModelFactory.createModelForGraph(conf);
		producerList = new Vector<Producer>();
		/*
		Iterator<RDFNode> listNodes =
				confModel
					.getRDFNode(confRoot)
					.asResource()
					.getPropertyResourceValue(SPINX.input)
					.as(RDFList.class)
					.iterator();
					*/
		StmtIterator stmtIter =
			confModel
				.getRDFNode(confRoot)
				.asResource()
				.listProperties(DF.input);
		while(stmtIter.hasNext()) {
			producerList.add(map.getProducer(stmtIter.next().getObject().asNode()));
		}

	}

	/**
	 * Instantiates a new graph list to graph function.
	 *
	 * @param prodIter the prod iter
	 */
	public GraphSetToGraphFunction(Iterator<Producer> prodIter) {
		producerList = new Vector<Producer>();
		while(prodIter.hasNext()) {
			producerList.add(prodIter.next());
		}

	}

/*
		return exec(
				((RDFList)
						((Resource)
								confModel
									.getRDFNode(confRoot)
						)
							.getPropertyResourceValue(SPINX.input)
				)
					.iterator()
					.mapWith(
						new Map1<RDFNode, Graph>() {
							@Override
							public Graph map1(final RDFNode node) {
								return context.getInnerGraph(node.asNode());
							}
						}));
*/

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
 	 */
	public boolean dependsFrom(Producer producer) {
		final Iterator<Producer> prodIter = producerList.iterator();
		while (prodIter.hasNext()) {
			Producer currProd = prodIter.next();
			if (currProd.equals(producer) || currProd.dependsFrom(producer))
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.GraphProducer#createGraph(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public DynamicGraph createGraph(final DynamicDataset inputDataset) {
		final Iterator<Producer> prodIter = producerList.iterator();
		return exec(new Iterator<DynamicGraph>() {
			public boolean hasNext() {
				return prodIter.hasNext();
			}
			public DynamicGraph next() {
				return prodIter.next().createGraph(inputDataset);
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		});
	}

	/**
	 * Exec.
	 *
	 * @param input the input
	 * @return the graph
	 */
	public abstract DynamicGraph exec(Iterator<DynamicGraph> input);

}

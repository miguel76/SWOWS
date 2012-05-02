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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.Instance;
import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * The Class DataflowProducer executes a dataflow:
 * generates an output.
 *
 * {@link com.hp.hpl.jena.sparql.core.DatasetGraph}
 * from an input {@code DatasetGraph} applying some (graph)
 * operations.
 * The operations that can be used are defined in classes
 * implementing the interface {@link Producer}. This class
 * itself implements the Producer interface, so it can be
 * used as a graph operator by an outer data flow.
 * @author miguel.ceriani@gmail.com
 */
public class DataflowProducer extends DatasetProducer {

	private Map<Node, Producer> innerProds = new ConcurrentHashMap<Node, Producer>();
	private Producer confProducer, inputProd;

	/**
	 * Instantiates a new data flow.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public DataflowProducer(Graph conf, Node confRoot, ProducerMap map) {
		this(
				map.getProducer( GraphUtils.getSingleValueProperty(conf, confRoot, SPINX.config.asNode()) ),
				map.getProducer( GraphUtils.getSingleValueProperty(conf, confRoot, SPINX.input.asNode()) ) );
	}

	/**
	 * Instantiates a new data flow.
	 *
	 * @param confProducer the producer of the configuration graph
	 * @param inputProd the producer of the input dataset
	 */
	public DataflowProducer(Producer confProducer, Producer inputProd) {
		this.confProducer = confProducer;
		this.inputProd = inputProd;
	}

	/**
	 * Instantiates a new data flow.
	 *
	 * @param confGraph the configuration graph
	 * @param inputProd the producer of the input dataset
	 */
	public DataflowProducer(final DynamicGraph confGraph, DatasetProducer inputProd) {
		this(
				new GraphProducer() {
					@Override
					public boolean dependsFrom(Producer producer) {
						return false;
					}
					@Override
					public DynamicGraph createGraph(DynamicDataset inputDs) {
						return confGraph;
					}
				},
				inputProd );
	}

	/**
	 * Instantiates a new data flow.
	 *
	 * @param confGraph the configuration graph
	 * @param inputDataset the input dataset
	 */
	public DataflowProducer(final DynamicGraph confGraph, final DynamicDataset inputDataset) {
		this(
				confGraph,
				new DatasetProducer() {
					@Override
					public boolean dependsFrom(Producer producer) {
						return false;
					}

					@Override
					public DynamicDataset createDataset(DynamicDataset inputDataset2) {
						return inputDataset;
					}
				} );
	}
	
	/*
	private static GraphProducer inputProvider =
		new GraphProducer() {
			@Override
			public Graph exec(Graph conf, Node confRoot, ExecutionContext context) {
				Iterator<Node> ids = conf.queryHandler().objectsFor(confRoot, SPINX.id.asNode());
				if (ids.hasNext())
					return context.getInputGraph(ids.next().getURI());
				else
					return context.getDefaultInputGraph();
			}
		};
	*/

	private Class<?> getProducerClass(Graph conf, Node graphId) {
		if (conf.contains(graphId, RDF.type.asNode(), SPINX.UnionGraph.asNode()))
			return UnionFunction.class;
//		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.InputGraph.asNode()))
//			return InputProvider.class;
		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.IdentityGraph.asNode()))
			return IdentityGraphFunction.class;
		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.ConstructGraph.asNode()))
			return SparqlConstructFunction.class;
		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.DataflowGraph.asNode()))
			return DataflowProducer.class;
		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.InferenceGraph.asNode()))
			return InferenceGraphProducer.class;
		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.EmptyGraph.asNode()))
			return EmptyGraphProducer.class;
		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.UpdatableGraph.asNode()))
			return UpdatableProducer.class;
		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.InlineGraph.asNode()))
			return InlineGraphProducer.class;
		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.InlineDataset.asNode()))
			return InlineDatasetProducer.class;
		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.SelectGraph.asNode()))
			return SelectGraphProducer.class;
		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.LoadGraph.asNode()))
			return LoadGraphProducer.class;
		//TODO: manage the included graphs pre-dataflow execution!
		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.IncludedGraph.asNode()))
			return LoadGraphProducer.class;
		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.LoggedGraph.asNode()))
			return LoggingProducer.class;
		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.IntegerRange.asNode()))
			return RangeProducer.class;
		else if (conf.contains(graphId, RDF.type.asNode(), SPINX.IntegerRangeFromGraph.asNode()))
			return RangeFunction.class;
                else if (conf.contains(graphId, RDF.type.asNode(), SPINX.TwitterGraph.asNode()))
			return TwitterProducer.class;
		throw new RuntimeException("Unrecognized Graph Producer for node " + graphId + " in graph " + conf);
		//return null;
	}
	
	private class ProducerMapWithMemory implements ProducerMap {
		
		private ProducerMap innerProducerMap;
		private Node specialProducerNode;
		private Producer specialProducer;
		private boolean found = false;
		private Graph conf;
		
		public ProducerMapWithMemory(
				ProducerMap innerProducerMap,
				Node specialProducerNode,
				Producer specialProducer,
				Graph conf) {
			this.innerProducerMap = innerProducerMap;
			this.specialProducerNode = specialProducerNode;
			this.specialProducer = specialProducer;
			this.conf = conf;
		}
		
		@Override
		public Producer getRecProducer(Node graphId) {
			if (graphId.equals(specialProducerNode)) {
				found = true;
				return specialProducer;
			}
			else
				return (innerProducerMap == null)
							? null
							: innerProducerMap.getRecProducer(graphId);
		}
		
		@Override
		public Producer getProducer(Node graphId) {
			Producer recProducer = getRecProducer(graphId);
			if (recProducer != null)
				return recProducer;
			return getInnerProducer(conf, graphId, this);
		}
		
		public boolean getFound() {
			return found;
		}
		
	}

	private Producer getInnerProducer(
			final Graph conf, final Node node, final ProducerMap map) {
		if (innerProds.containsKey(node))
			return innerProds.get(node);
//		if (map != null) {
//			Producer existingProducer = map.getProducer(node);
//			if (existingProducer != null)
//				return existingProducer;
//		}
			if (node.equals(Instance.InputDataset.asNode()))
				return new DatasetProducer() {
					@Override
					public boolean dependsFrom(Producer producer) {
						return false;
					}
					@Override
					public DynamicDataset createDataset(DynamicDataset inputDataset) {
						return inputDataset;
					}
				};
			try {
				/*
				Producer newProducer =
					(Producer)
						getProducerClass(conf, node)
						.getConstructor(Graph.class, Node.class, ProducerMap.class)
						.newInstance(conf, node, map);
						*/
				Class<?> prodClass = getProducerClass(conf, node);
				/*
				boolean isGraphProducer = false;
				boolean isDatasetProducer = false;
				for (Type interf : prodClass.getGenericInterfaces()) {
					if (interf.equals(GraphProducer.class))
						isGraphProducer = true;
					else if (interf.equals(DatasetProducer.class))
						isDatasetProducer = true;
				}
				*/
				/*
				final RecursiveGraphProducer recGraphProd =
					new GraphProducer() {
						@Override
						public Graph createGraph(DatasetGraph inputDataset) {
							return new DelegatingGraph() {
								{
									baseGraphCopy = Graph.emptyGraph;
								}
								@Override
								protected Graph getBaseGraph() {
									return null;
								}
							};
						}
						@Override
						public boolean dependsFrom(Producer producer) {
							return false;
						}
					};
					*/
//				final BuildingGraphProducer buildingGraphProducer = new BuildingGraphProducer();
//				innerProds.put(node, buildingGraphProducer); // prova
				final BuildingDatasetProducer buildingDatasetProducer = new BuildingDatasetProducer();
				//innerProds.put(node, buildingDatasetProducer);
				ProducerMapWithMemory tempProdMap = new ProducerMapWithMemory(map, node, buildingDatasetProducer, conf);
				Producer newProducer =
							(Producer)
							prodClass
							.getConstructor(Graph.class, Node.class, ProducerMap.class)
							.newInstance(
								conf, node,
								tempProdMap
//								new ProducerMap() {
//									@Override
//									public Producer getProducer(Node graphId) {
//										/*
//										if (graphId.equals(node)) {
//											return buildingGraphProducer;
//										}
//										else
//											return (map == null)
//														? getInnerProducer(conf, graphId, this)
//														: map.getProducer(graphId);
//														*/
//										return getInnerProducer(conf, graphId, null);
//									}
//								}
								);
				if (tempProdMap.getFound()) {
					NotifyingProducer notifyingProducer = new NotifyingProducer( newProducer );
					buildingDatasetProducer.attacheTo(notifyingProducer);
					newProducer = notifyingProducer;
				}
				newProducer = new CachedProducer(newProducer);
				innerProds.put(node, newProducer);
				return newProducer;
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				Throwable t = e.getCause();
				if (t != null)
					if (t instanceof RuntimeException)
						throw (RuntimeException) t;
					else
						throw new RuntimeException(t);
				else
					throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
	}


	/* (non-Javadoc)
	 * @see org.swows.producer.DatasetProducer#createDataset(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public DynamicDataset createDataset(DynamicDataset parentInputDataset) {
		final Graph configGraph = confProducer.createGraph(parentInputDataset);
		DynamicDataset inputDataset = inputProd.createDataset(parentInputDataset);
		//final Dataset inputDs = DatasetFactory.create(inputDataset);

		Producer outputProducer = getInnerProducer(configGraph, Instance.OutputDataset.asNode(), null);
		return outputProducer.createDataset(inputDataset);
		
//		QueryHandler configQueryHandler = configGraph.queryHandler();
//		DatasetGraph resultDataset = DatasetGraphFactory.createMem();
//		ExtendedIterator<Node> outputGraphNodes =
//			configQueryHandler.subjectsFor(RDF.type.asNode(), SPINX.OutputGraph.asNode());
//		while (outputGraphNodes.hasNext()) {
//			final Node outGraphNode = outputGraphNodes.next();
//			Node outInputNode = configQueryHandler.objectsFor(outGraphNode, SPINX.input.asNode()).next();
//			Producer outputGraphProd =
//				getInnerProducer(configGraph, outInputNode,	null);
//			Graph outputGraph = outputGraphProd.createGraph(inputDataset);
//			Iterator<Node> outNameNodes = configQueryHandler.objectsFor(outGraphNode, SPINX.id.asNode());
//			if (outNameNodes.hasNext()) {
//				resultDataset.addGraph(outNameNodes.next(), outputGraph);
//			} else {
//				resultDataset.setDefaultGraph(outputGraph);
//			}
//		}
//		return resultDataset;
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
	 */
	@Override
	public boolean dependsFrom(Producer producer) {
		return (producer == inputProd || producer == confProducer || inputProd.dependsFrom(producer) || confProducer.dependsFrom(producer));
	}

}

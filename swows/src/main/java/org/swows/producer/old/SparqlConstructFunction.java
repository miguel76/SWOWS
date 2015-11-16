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

import java.net.URI;
import java.util.Iterator;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.swows.graph.SparqlConstructGraph;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.producer.ProducerMap;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;

/**
 * The Class SparqlConstructFunction executes a SPARQL
 * Construct query on some graphs and return the built
 * graph.
 * The query is defined using the
 * <a href="http://spinrdf.org/sp.html">SPIN&nbsp;-&nbsp;SPARQL syntax</a>.
 */

public class SparqlConstructFunction extends GraphProducer {

//	private Query query;
//	private Map<Node, Producer> prodMap = new HashMap<Node, Producer>();
	private RDFProducer inputProducer = null;
//	private Producer configProducer = null;
//	private String queryTxt = null;
//	private String baseURI;
	
	private QueryProducer queryProducer = null;
	// TODO: find a better way

	/**
	 * Instantiates a new sparql construct function.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see RDFProducer
	 */
	public SparqlConstructFunction(Graph conf, Node confRoot, final ProducerMap map) {
		URI baseURI = URI.create(confRoot.getURI());
		
		Node inputNode = GraphUtils.getSingleValueOptProperty(conf, confRoot, DF.input.asNode());
		inputProducer =
				(inputNode != null) ?
						map.getProducer( inputNode ) :
						EmptyGraphProducer.getInstance();
		
		Node queryAsInputNode = NodeFactory.createURI(baseURI.resolve("#query").toString());
						
		Node queryNode = GraphUtils.getSingleValueOptProperty(conf, confRoot, DF.configTxt.asNode());
		String queryTxt = (queryNode != null) ? queryNode.getLiteralLexicalForm() : null;
		
		Node configNode = GraphUtils.getSingleValueOptProperty(conf, confRoot, DF.config.asNode());
		RDFProducer configProducer = (configNode != null) ? map.getProducer(configNode) : null;
		
		Node configRootNode = GraphUtils.getSingleValueOptProperty(conf, confRoot, DF.configRoot.asNode());
		if (configRootNode == null) configRootNode = configNode;
		if (configRootNode == null && inputNode != null) {
			Iterator<Node> namedInputNodes = GraphUtils.getPropertyValues(conf, inputNode, DF.namedInput.asNode());
			while (namedInputNodes.hasNext()) {
				Node namedInputNode = namedInputNodes.next();
				if (conf.contains(namedInputNode, DF.id.asNode(), queryAsInputNode)) {
					configRootNode = GraphUtils.getSingleValueProperty(conf, namedInputNode, DF.input.asNode());
					break;
				}
			}
		}
		
//		String configRootURI = (configRootNode != null && configRootNode.isURI()) ? configRootNode.getURI() : null;
		
		queryProducer =
				new CoalesceQueryProducer(
						new GraphQueryProducer(
								new CoalesceGraphProducer(
										configProducer,
										new SelectGraphProducer(inputProducer, queryAsInputNode) ),
										configRootNode/*NodeFactory.createURI(baseURI.resolve("").toString())*/),
						new StringQueryProducer(queryTxt, baseURI.toString()));
//		final Model confModel = ModelFactory.createModelForGraph(conf);
//		Resource constructResource =
//			confModel
//					.getRDFNode(confRoot)
//					.asResource()
//					.getPropertyResourceValue(SPINX.config);
//		Iterator<Producer> fromGraphProds =
//			confModel
//					.getRDFNode(confRoot)
//					.asResource()
//					.listProperties(SPINX.from)
//					.mapWith(new Map1<Statement, Producer>() {
//						@Override
//						public Producer map1(Statement stmt) {
//							return map.getProducer(stmt.getObject().asNode());
//						}
//					});
		//defaultProd = null;
//		if (!fromGraphProds.hasNext()) {
//			defaultProd = EmptyGraphProducer.getInstance();
//		} else {
//			defaultProd = fromGraphProds.next();
//			if (fromGraphProds.hasNext()) {
//				defaultProd =
//					new UnionFunction(
//							new ConcatenatedIterator<Producer>(
//									new SingletonIterator<Producer>(defaultProd),
//									fromGraphProds));
//			}
//		}
//		Iterator<Node> fromNamedGraphNames =
//			confModel
//					.getRDFNode(confRoot)
//					.asResource()
//					.listProperties(SPINX.fromNamed)
//					.mapWith(new Map1<Statement, Node>() {
//						@Override
//						public Node map1(Statement stmt) {
//							return stmt.getObject().asNode();
//						}
//					});
//		while (fromNamedGraphNames.hasNext()) {
//			Node graphNames = fromNamedGraphNames.next();
//			prodMap.put(graphNames, map.getProducer(graphNames));
//		}
		
//		query = QueryFactory.toQuery(conf, constructResource.asNode());
//		if (query == null)
//			throw new RuntimeException("Parsing Error");
		
		/*
		// debug 
		Graph revEnginedGraph = SpinxFactory.fromQuery(query);
		System.out.println();
		System.out.println("**********************************");
		System.out.println("*** Reverse Engine query graph ***");
		System.out.println("**********************************");
		ModelFactory.createModelForGraph(revEnginedGraph).write(System.out,"N3");
		System.out.println("**********************************");
		System.out.println();
		*/
		
		/*
		SPINFactory.asExpression(constructResource);
		query = ARQFactory.get().createQuery(SPINFactory.asQuery(constructResource));
		*/
		/*
		Iterator<String> graphUris =
			new ConcatenatedIterator<String>(
					query.getGraphURIs().iterator(),
					query.getNamedGraphURIs().iterator());
		while (graphUris.hasNext()) {
			Node node = confModel.getResource(graphUris.next()).asNode();
			prodMap.put(node, (GraphProducer) map.getProducer(node));
		}
		*/
	}

	/*
	@Override
	public Graph exec(final Graph conf, Node confRoot, final ExecutionContext context) {
		final Model confModel = ModelFactory.createModelForGraph(conf);
		Resource constructResource =
			confModel
					.getRDFNode(confRoot)
					.asResource()
					.getPropertyResourceValue(SPINX.input);
		SPINFactory.asExpression(constructResource);
		Query query = ARQFactory.get().createQuery(SPINFactory.asQuery(constructResource));
		Dataset queryDataset =
			DatasetFactory.create(new DatasetGraphCollection() {

				@Override
				public Graph getGraph(Node graphNode) {
					return context.getInnerGraph(graphNode);
				}

				@Override
				public Graph getDefaultGraph() {
					return context.getDefaultInputGraph();
				}

				@Override
				public Iterator<Node> listGraphNodes() {
					return conf.queryHandler().subjectsFor(RDF.type.asNode(), SPINX.Graph.asNode());
				}

			});
		QueryExecution queryExecution = ARQFactory.get().createQueryExecution(query, queryDataset, null);
		return queryExecution.execConstruct().getGraph();
	}
	*/

	/* (non-Javadoc)
	 * @see org.swows.producer.Producer#dependsFrom(org.swows.producer.Producer)
	 */
	public boolean dependsFrom(RDFProducer producer) {
		return
				(inputProducer != null && inputProducer.dependsFrom(producer));
//				|| (queryProducer != null && configProducer.dependsFrom(producer));
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.GraphProducer#createGraph(org.apache.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public DynamicGraph createGraph(final DynamicDataset inputDataset) {
		
		Query query = queryProducer.createQuery(inputDataset);
		if (query == null)
			throw new RuntimeException("Query not found");
		
//		DatasetGraph queryDatasetGraph =
//			new DatasetGraphCollection() {
//				private Map<Node, Graph> loadedNamedGraphs = new HashMap<Node, Graph>();
//				private Graph loadedDefaultGraph = null;
//				@Override
//				public Graph getGraph(Node graphNode) {
//					if (loadedNamedGraphs.containsKey(graphNode))
//						return loadedNamedGraphs.get(graphNode);
//					if (prodMap.containsKey(graphNode)) {
//						Graph newGraph = prodMap.get(graphNode).createGraph(inputDataset);
//						loadedNamedGraphs.put(graphNode, newGraph);
//						return newGraph;
//					}
//					throw new RuntimeException("Named Graph " + graphNode.getURI() + " not found");
//				}
//				@Override
//				public Graph getDefaultGraph() {
//					if (loadedDefaultGraph != null)
//						return loadedDefaultGraph;
//					loadedDefaultGraph = Graph.emptyGraph;
//					if (defaultProd != null) {
//						loadedDefaultGraph = defaultProd.createGraph(inputDataset);
//					}
//					return loadedDefaultGraph;
//				}
//				@Override
//				public Iterator<Node> listGraphNodes() {
//					return prodMap.keySet().iterator();
//				}
//			};
		//Dataset queryDataset = DatasetFactory.create(queryDatasetGraph);
		/*
		QueryExecution queryExecution =
			ARQFactory.get().createQueryExecution(query, queryDataset, null);
		Graph resultGraph = queryExecution.execConstruct().getGraph();
		*/
		
		DynamicDataset queryDatasetGraph = inputProducer.createDataset(inputDataset);
		return new SparqlConstructGraph(query, queryDatasetGraph);
	}

}

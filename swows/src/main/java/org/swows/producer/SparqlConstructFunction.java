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

import org.swows.graph.SparqlConstructGraph;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.spinx.QueryFactory;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;
import org.swows.vocabulary.SWI;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.update.UpdateFactory;

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
	private Producer inputProducer = null;
	private Producer configProducer = null;
	private String queryTxt = null;

	/**
	 * Instantiates a new sparql construct function.
	 *
	 * @param conf the graph with dataflow definition
	 * @param confRoot the specific node in the graph representing the producer configuration
	 * @param map the map to access the other defined producers
	 * @see Producer
	 */
	public SparqlConstructFunction(Graph conf, Node confRoot, final ProducerMap map) {
		inputProducer = map.getProducer( GraphUtils.getSingleValueProperty(conf, confRoot, DF.input.asNode()) );
		Node queryNode = GraphUtils.getSingleValueOptProperty(conf, confRoot, DF.txtConfig.asNode());
		if (queryNode != null)
			queryTxt = queryNode.getLiteralLexicalForm();
		else
			configProducer = map.getProducer( GraphUtils.getSingleValueProperty(conf, confRoot, DF.config.asNode()) );
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
	@Override
	public boolean dependsFrom(Producer producer) {
		return
				(inputProducer != null && inputProducer.dependsFrom(producer))
				|| (configProducer != null && configProducer.dependsFrom(producer));
	}

	/* (non-Javadoc)
	 * @see org.swows.producer.GraphProducer#createGraph(com.hp.hpl.jena.sparql.core.DatasetGraph)
	 */
	@Override
	public DynamicGraph createGraph(final DynamicDataset inputDataset) {
		
		Graph conf = null;
		Node constructNode = null;
		if (configProducer != null) {
			conf = configProducer.createGraph(inputDataset);
			final Model confModel = ModelFactory.createModelForGraph(conf);
			constructNode =
					confModel.getRDFNode(SWI.GraphRoot.asNode()).asNode();
		}
		Query query =
				(queryTxt != null) ?
						com.hp.hpl.jena.query.QueryFactory.create(queryTxt) :
						QueryFactory.toQuery(conf, constructNode);
		if (query == null)
			throw new RuntimeException("Parsing Error");
		
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

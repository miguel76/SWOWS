package org.swows.provenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.ARQInternalErrorException;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.OpVars;
import org.apache.jena.sparql.algebra.TransformCopy;
import org.apache.jena.sparql.algebra.Transformer;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpDistinct;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpGroup;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpReduced;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.E_BNode;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.expr.ExprFunctionN;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.aggregate.Accumulator;
import org.apache.jena.sparql.expr.aggregate.Aggregator;
import org.apache.jena.sparql.expr.aggregate.AggregatorBase;
import org.apache.jena.sparql.function.FunctionEnv;
import org.apache.jena.sparql.modify.TemplateLib;
import org.apache.jena.sparql.syntax.Template;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.ModelUtils;
import org.apache.jena.sparql.util.Symbol;
import org.apache.jena.vocabulary.RDF;
import org.swows.origin.AccumulatorMultiExpr;
import org.swows.origin.OriginManager;

public class Provenance {
	
    private static final Logger logger =
            Logger.getLogger(Provenance.class.getName());

	private int varCount = 0;
//	private Query query;
//	private List<Var> addedVars = new ArrayList<Var>();
	
	private Var newVar() {
		Var newVar = Var.alloc("tempVar" + varCount++);
//		addedVars.add(newVar);
		return newVar;
	}
	
	private static class GraphAsTripleCollection implements Collection<Triple> {
		
		private Graph graph;
		
		public GraphAsTripleCollection(Graph graph) {
			this.graph = graph;
		}
		
		@Override
		public boolean add(Triple t) {
			graph.add(t);
			return true;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean contains(Object o) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Iterator<Triple> iterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object[] toArray() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T[] toArray(T[] a) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean remove(Object o) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends Triple> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}

	}
	
	private static class DatasetGraphAsTripleCollection implements Collection<Triple> {
		
		private DatasetGraph datasetGraph;
		
		public DatasetGraphAsTripleCollection(DatasetGraph datasetGraph) {
			this.datasetGraph = datasetGraph;
		}
		
		@Override
		public boolean add(Triple t) {
			datasetGraph.add(new Quad(Quad.defaultGraphIRI, t));
			return true;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean contains(Object o) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Iterator<Triple> iterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object[] toArray() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T[] toArray(T[] a) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean remove(Object o) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends Triple> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}

	}

	private static final Symbol keyMap = Symbol.create("swows:origin:map");
    @SuppressWarnings("unchecked")
	private static Map<Node, DatasetGraph> getNodesToOriginMap(Context context) {
    	Map<Node, DatasetGraph> nodesToOriginMap = null;
    	if (context != null) {
    		nodesToOriginMap = (Map<Node, DatasetGraph>) context.get(keyMap);
    	}
        if (nodesToOriginMap == null) {
        	nodesToOriginMap = new HashMap<Node, DatasetGraph>();
        	if (context != null) {
        		context.set(keyMap, nodesToOriginMap);
        	}
        }
        return nodesToOriginMap;
	}
	
	private static class E_BNodeForProvenance extends E_BNode {
		
		private Template template;
		
		public E_BNodeForProvenance(Template template) {
			super(null);
			this.template = template;
		}
		
	    // Not really a special form but we need access to 
	    // the binding.
	    @Override
	    public NodeValue evalSpecial(Binding binding, FunctionEnv env) {
	        Node newNode = NodeFactory.createBlankNode();
	        DatasetGraph provDatasetGraph = DatasetGraphFactory.createMem();
	        template.subst(
	        		new DatasetGraphAsTripleCollection(provDatasetGraph),
	        		/* bNodeMap */ null,
	        		binding);
	        getNodesToOriginMap(env.getContext()).put(newNode, provDatasetGraph);
	        return NodeValue.makeNode(newNode);
	    }
	    
	    @Override
	    public NodeValue eval(List<NodeValue> args) {
	    	throw new ARQInternalErrorException();
	    }

	    @Override
	    public Expr copy(ExprList newArgs) {
	        return new E_BNodeForProvenance(template) ;
	    } 
	}

	private static class ProvExpr extends ExprFunctionN {
		
		public ProvExpr(ExprList args) { super("PROV", args); }

		@Override
	    public NodeValue eval(List<NodeValue> args, FunctionEnv env) {
			Map<Node, DatasetGraph> nodesToOriginMap = getNodesToOriginMap(env.getContext());
	        Node newNode = NodeFactory.createBlankNode();
	        DatasetGraph provDatasetGraph = DatasetGraphFactory.createMem();
	        args.forEach(
	        	nodeValue -> {
					if (nodeValue != null) {
						DatasetGraph singleProvDatasetGraph = nodesToOriginMap.get(nodeValue.asNode());
						if (singleProvDatasetGraph != null) {
							singleProvDatasetGraph.find().forEachRemaining(quad -> provDatasetGraph.add(quad));
						}
					}
	        	}
			);
			nodesToOriginMap.put(newNode, provDatasetGraph);
			return NodeValue.makeNode(newNode);
		}

		@Override
	    public NodeValue eval(List<NodeValue> args) { return null; }

		@Override
	    public Expr copy(ExprList newArgs) {
			return new ProvExpr(newArgs);
		}
		
	}
	
	private static class ProvAccumulator extends AccumulatorMultiExpr {

        private Node newNode = NodeFactory.createBlankNode();
        DatasetGraph provDatasetGraph = DatasetGraphFactory.createMem();
        Map<Node, DatasetGraph> nodesToOriginMap = null;

	    protected ProvAccumulator(Collection<Expr> exprs) {
			super(exprs);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected NodeValue getAccValue() {
			if (nodesToOriginMap != null) {
				nodesToOriginMap.put(newNode, provDatasetGraph);
			}
			return NodeValue.makeNode(newNode);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void accumulate(Iterator<NodeValue> nodeValues, Binding binding, FunctionEnv env) {
	        if (nodesToOriginMap == null) {
	        	nodesToOriginMap = getNodesToOriginMap(env.getContext());
	        }
	        nodeValues.forEachRemaining(nodeValue -> {
					if (nodeValue != null) {
						DatasetGraph singleProvDatasetGraph = nodesToOriginMap.get(nodeValue.asNode());
						if (singleProvDatasetGraph != null) {
							singleProvDatasetGraph.find().forEachRemaining(quad -> provDatasetGraph.add(quad));
						}
					}
				});
		}

		@Override
		protected void accumulateError(Binding binding, FunctionEnv functionEnv) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private static class ProvAggregator extends AggregatorBase {
		protected ProvAggregator(ExprList exprList) {
		        super("AGG", false, exprList);
		}

		@Override
		public Aggregator copy(ExprList exprs) {
			return new ProvAggregator(exprList);
		}

		@Override
		public boolean equals(Aggregator other, boolean bySyntax) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Accumulator createAccumulator() {
			return new ProvAccumulator(exprList.getList());
		}

		@Override
		public Node getValueEmpty() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
//    public static void addProvenanceToQuery(Query query) {
//    	(new Provenance()).addProvenance(query);
//    }
    
    private static void addReifiedTriple(Node node, Triple triple, Consumer<Triple> sink) {
    	sink.accept(new Triple(node, RDF.type.asNode(), RDF.Statement.asNode()));
    	sink.accept(new Triple(node, RDF.subject.asNode(), triple.getSubject()));
    	sink.accept(new Triple(node, RDF.predicate.asNode(), triple.getPredicate()));
    	sink.accept(new Triple(node, RDF.object.asNode(), triple.getObject()));
    }
    
//    public Op addProvenance(Op op, Iterable<Triple> templateTriples, Consumer<Triple> provTemplSink) {
//        logger.info("Transforming Main Pattern: " + op);
//
//		List<Var> provVars = new ArrayList<Var>();
//		Op newOp = Transformer.transform(new TransformCopy() {
//			
//			@Override
//			public Op transform(OpBGP opBGP) {
//		        logger.info("Transforming BGP: " + opBGP);
//		        Var provVar = newVar();
//		        List<Var> newBGPVars = new ArrayList<Var>();
//		        provVars.add(provVar);
//		        newBGPVars.add(provVar);
//		        provTemplSink.accept(new Triple(provVar, RDF.type.asNode(), PROV.Collection.asNode()));
//		        opBGP.getPattern().forEach(new Consumer<Triple>() {
//					@Override
//					public void accept(Triple t) {
//						Var statementVar = newVar();
//				        newBGPVars.add(statementVar);
////						template.add(new Triple(provVar, PROV.wasDerivedFrom.asNode(), statementVar));
//				        provTemplSink.accept(new Triple(provVar, PROV.hadMember.asNode(), statementVar));
//				        addReifiedTriple(statementVar, t, provTemplSink);
//					}
//				});
//		        VarExprList varExprList = new VarExprList();
//		        newBGPVars.forEach(new Consumer<Var>() {
//					@Override
//					public void accept(Var var) {
//						varExprList.add(var, new E_BNode());
//					}
//				});
//				Op newOp = OpExtend.extend(opBGP, varExprList);
//		        
////				Op newOp = OpExtend.extend(opBGP, provVar, new E_BNode());
//		        logger.info("BGP Transformed to: " + newOp);
////				return OpExtend.extend(opBGP, newVar(), new E_BNode());
//				return newOp;
//			}
//			
////			@Override
////			public Op transform(OpProject opProject, Op subOp) {
////				OpVars.visibleVars(subOp)
////				subOp
////				// TODO Auto-generated method stub
////				return super.transform(opProject, subOp);
////			}
//			
//		}, op);
//		
//		List<Var> newTemplateVars = new ArrayList<Var>();
//		templateTriples.forEach(new Consumer<Triple>() {
//			@Override
//			public void accept(Triple t) {
//				Var statementVar = newVar();
//				newTemplateVars.add(statementVar);
//		        addReifiedTriple(statementVar, t, provTemplSink);
//				for (Var provVar: provVars) {
//					provTemplSink.accept(new Triple(statementVar, PROV.wasDerivedFrom.asNode(), provVar));
//				}
//			}
//		});
//		
//        VarExprList varExprList = new VarExprList();
//        newTemplateVars.forEach(new Consumer<Var>() {
//			@Override
//			public void accept(Var var) {
//				varExprList.add(var, new E_BNode());
//			}
//		});
//		newOp = OpExtend.extend(newOp, varExprList);
//					
//        logger.info("Main Pattern Transformed to: " + newOp);
//
//		return newOp;
//	}
    
    public Pair<Op, Var> addProvenance(Op op) {
        logger.info("Transforming Main Pattern: " + op);

		List<Var> provVars = new ArrayList<Var>();
		Op newOp = Transformer.transform(new TransformCopy() {
			
			@Override
			public Op transform(OpBGP opBGP) {
		        logger.info("Transforming BGP: " + opBGP);
		        Var provVar = newVar();
//		        Vector<Var> newBGPVars = new Vector<Var>();
		        provVars.add(provVar);
//		        newBGPVars.addElement(provVar);
				Op newOp =
						OpExtend.extend(
								opBGP, provVar,
								new E_BNodeForProvenance(new Template(opBGP.getPattern())));
		        
//				Op newOp = OpExtend.extend(opBGP, provVar, new E_BNode());
		        logger.info("BGP Transformed to: " + newOp);
				return newOp;
			}
			
			@Override
			public Op transform(OpProject opProject, Op subOp) {
				List<Var> vars = opProject.getVars();
				OpVars.visibleVars(subOp).forEach(new Consumer<Var>() {
					@Override
					public void accept(Var var) {
						if (provVars.contains(var)) {
							vars.add(var);
						}
					}
				});
				return new OpProject(subOp, vars);
			}
			
			private Op aggregate(VarExprList groupVars, List<ExprAggregator> aggregators, Op subOp) {
				ExprList exprList = new ExprList();
				OpVars.visibleVars(subOp).forEach(new Consumer<Var>() {
					@Override
					public void accept(Var var) {
						if (provVars.contains(var)) {
							exprList.add(new ExprVar(var));
						}
					}
				});
								
				Var aggrVar = newVar();
				provVars.add(aggrVar);
				aggregators.add(
						new ExprAggregator(
								aggrVar,
								new ProvAggregator(exprList)));
				return new OpGroup(subOp, groupVars, aggregators);
			}
			
			@Override
			public Op transform(OpGroup opGroup, Op subOp) {
				return aggregate(opGroup.getGroupVars(), opGroup.getAggregators(), subOp);
//				ExprList exprList = new ExprList();
//				OpVars.visibleVars(subOp).forEach(new Consumer<Var>() {
//					@Override
//					public void accept(Var var) {
//						if (provVars.contains(var)) {
//							exprList.add(new ExprVar(var));
//						}
//					}
//				});
//								
//				Var aggrVar = newVar();
//				provVars.addElement(aggrVar);
//				List<ExprAggregator> aggregators = opGroup.getAggregators();
//				aggregators.add(
//						new ExprAggregator(
//								aggrVar,
//								new ProvAggregator(exprList)));
//				return new OpGroup(subOp, opGroup.getGroupVars(), aggregators);
			}
			
			@Override
			public Op transform(OpDistinct opDistinct, Op subOp) {
				return aggregate(
						new VarExprList(
								Collections.list(
										Collections.enumeration(
												OpVars.visibleVars(subOp)))),
						new ArrayList<ExprAggregator>(),
						subOp);
			}
			
			@Override
			public Op transform(OpReduced opReduced, Op subOp) {
				return subOp;
			}
			
		}, op);
		
        Var provVar = newVar();
		ExprList exprList = new ExprList();
		OpVars.visibleVars(newOp).forEach(new Consumer<Var>() {
			@Override
			public void accept(Var var) {
				if (provVars.contains(var)) {
					exprList.add(new ExprVar(var));
				}
			}
		});
		newOp = OpExtend.extend(newOp, provVar, new ProvExpr(exprList));
		
        logger.info("Main Pattern Transformed to: " + newOp);

		return new ImmutablePair<Op, Var>(newOp, provVar);
	}
    
//    public void addProvenance(Query query) {
//		if (query.isConstructType()) {
//			Op op = Algebra.compile(query) ;
//	        BasicPattern template = query.getConstructTemplate().getBGP();
//			BasicPattern originalTemplate = new BasicPattern(template);
//			Op newOp = addProvenance(op, originalTemplate, new TripleConsumerFromBP(template));
//			query.setQueryPattern(OpAsQuery.asQuery(newOp).getQueryPattern());
//		}
//	}
    
//    public Consumer<Triple> separateProvenance(Consumer<Triple> output, Consumer<Triple> provenance) {
//    	return new Consumer<Triple>() {
//			@Override
//			public void accept(Triple t) {
//				if (addedVars.contains(t.getSubject())) {
//					provenance.accept(t);
//				} else {
//					output.accept(t);
//				}
//			}
//		};
//    }

//    public void separateProvenance(Iterator<Triple> triples, Consumer<Triple> output, Consumer<Triple> provenance) {
//    	triples.forEachRemaining(separateProvenance(output, provenance));
//    }

    private static class TripleConsumerFromModel implements Consumer<Triple> {
    	
    	Model model;
    	
    	TripleConsumerFromModel(Model model) {
    		this.model = model;
    	}
    	
		@Override
		public void accept(Triple t) {
			model.add(model.asStatement(t));
		}
		
    }
    
    private static class TripleConsumerFromBP implements Consumer<Triple> {
    	
    	BasicPattern basicPattern;
    	
    	TripleConsumerFromBP(BasicPattern basicPattern) {
    		this.basicPattern = basicPattern;
    	}
    	
		@Override
		public void accept(Triple t) {
			basicPattern.add(t);
		}
		
    }
    
//    public void separateProvenance(Iterator<Triple> triples, Model output, Consumer<Triple> provenance) {
//    	separateProvenance(triples, new TripleConsumerFromModel(output), provenance);
//    }
//
//    public void separateProvenance(Iterator<Triple> triples, Consumer<Triple> output, Model provenance) {
//    	separateProvenance(triples, output, new TripleConsumerFromModel(provenance));
//    }
//
//    public void separateProvenance(Iterator<Triple> triples, Model output, Model provenance) {
//    	separateProvenance(triples, new TripleConsumerFromModel(output), new TripleConsumerFromModel(provenance));
//    }
//
//    public Pair<Model, Model> separateProvenance(Iterator<Triple> triples) {
//    	Model output = ModelFactory.createDefaultModel(), provenance = ModelFactory.createDefaultModel();
//    	separateProvenance(triples, output, provenance);
//    	return new ImmutablePair<Model, Model>(output, provenance);
//    }

//    public static Pair<Model, Model> execWithProvenance(Query query, Dataset inputDataset) {
//    	Provenance prov = new Provenance();
//    	prov.addProvenance(query);
//    	QueryExecution queryExecution = QueryExecutionFactory.create(query, inputDataset);
//    	return prov.separateProvenance(queryExecution.execConstructTriples());
//    }

    static private String labelForQuery(Query q) {
        if ( q.isSelectType() )     return "SELECT" ; 
        if ( q.isConstructType() )  return "CONSTRUCT" ; 
        if ( q.isDescribeType() )   return "DESCRIBE" ; 
        if ( q.isAskType() )        return "ASK" ;
        return "<<unknown>>" ;
    }

    public static void execConstructWithProvenance(Query query, Dataset inputDataset, Model outputModel, OriginManager originManager) {
        if ( ! query.isConstructType() )
            throw new QueryExecException("Attempt to get a CONSTRUCT model from a "+labelForQuery(query)+" query") ;
		Op op = Algebra.compile(query);
    	Provenance prov = new Provenance();
    	Pair<Op, Var> provResult = prov.addProvenance(op);
//		query.setQueryPattern(OpAsQuery.asQuery(provResult.getLeft()).getQueryPattern());
//    	QueryExecution queryExecution = QueryExecutionFactory.create(query, inputDataset);
    	Template constructTemplate = query.getConstructTemplate();
    	List<Triple> templateTriples = constructTemplate.getTriples();
    	QueryExecution queryExecution = QueryExecutionFactory.create(OpAsQuery.asQuery(provResult.getLeft()), inputDataset);
    	ResultSet resultSet = queryExecution.execSelect();
//    	Iterator<Binding> outputBindings = new Iterator<Binding>() {
//			@Override
//			public Binding next() {
//				Binding binding = resultSet.nextBinding();
//				Node provNode = binding.get(provResult.getRight());
//				if (provNode != null) {
//					Graph originGraph = getNodesToOriginMap(queryExecution.getContext()).get(provNode);
//					originManager.addOrigin(newQuads, originQuads);
//				}
//				return binding;
//			}
//			@Override
//			public boolean hasNext() {
//				return resultSet.hasNext();
//			}
//		};
    	(new Iterator<Binding>() {
    				@Override
    				public Binding next() {
    					return resultSet.nextBinding();
    				}
    				@Override
    				public boolean hasNext() {
    					return resultSet.hasNext();
    				}
    	}).forEachRemaining(new Consumer<Binding>() {
            Map<Node, Node> bNodeMap = new HashMap<>() ;
			@Override
			public void accept(Binding binding) {
                // Iteration is a new mapping of bnodes. 
                bNodeMap.clear() ;

                DatasetGraph originDatsetGraph = null;
				Node provNode = binding.get(provResult.getRight());
				if (provNode != null) {
					originDatsetGraph = getNodesToOriginMap(queryExecution.getContext()).get(provNode);
				}
				
//                List<Quad> quadList = new ArrayList<>(templateTriples.size());
				
				Stream<Triple> newTripleStream =
						templateTriples
						.stream()
						.map(triple -> TemplateLib.subst(triple, binding, bNodeMap))
						.filter(q -> ! q.isConcrete() || ! ModelUtils.isValidAsStatement(q.getSubject(), q.getPredicate(), q.getObject()) )
						.map(new Function<Triple, Triple>() {
							@Override
							public Triple apply(Triple triple) {
								outputModel.add(outputModel.asStatement(triple));
								return triple;
							}
						});
				
                if (originDatsetGraph != null) {
                	originManager.addOrigin(
                			newTripleStream.map(triple -> new Quad(Quad.defaultGraphIRI, triple)).iterator(),
                			originDatsetGraph.find());
                } else {
                	newTripleStream.count();
				}

//                for (Triple triple : templateTriples) {
//                    Triple q = TemplateLib.subst(triple, binding, bNodeMap) ;
//                    if ( ! q.isConcrete() || ! ModelUtils.isValidAsStatement(q.getSubject(), q.getPredicate(), q.getObject()) ) {
//                        //Log.warn(TemplateLib.class, "Unbound quad: "+FmtUtils.stringForQuad(quad)) ;
//                        continue ;
//                    }
//                    outputModel.add(outputModel.asStatement(q));
//                    if (originDatsetGraph != null) {
//                    	originManager.addOrigin(
//                    			new Quad(Quad.defaultGraphIRI, q),
//                    			originDatsetGraph.find());
//                    }
//                }
                                
			}
		});
		
    }

}

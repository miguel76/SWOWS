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
package org.swows.spinx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.jena.atlas.lib.Sink;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.ResultSet;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitor;
import org.apache.jena.sparql.algebra.Table;
import org.apache.jena.sparql.algebra.op.OpAssign;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpConditional;
import org.apache.jena.sparql.algebra.op.OpDatasetNames;
import org.apache.jena.sparql.algebra.op.OpDiff;
import org.apache.jena.sparql.algebra.op.OpDisjunction;
import org.apache.jena.sparql.algebra.op.OpDistinct;
import org.apache.jena.sparql.algebra.op.OpExt;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpGroup;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpLabel;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.algebra.op.OpList;
import org.apache.jena.sparql.algebra.op.OpMinus;
import org.apache.jena.sparql.algebra.op.OpNull;
import org.apache.jena.sparql.algebra.op.OpOrder;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpProcedure;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpPropFunc;
import org.apache.jena.sparql.algebra.op.OpQuad;
import org.apache.jena.sparql.algebra.op.OpQuadBlock;
import org.apache.jena.sparql.algebra.op.OpQuadPattern;
import org.apache.jena.sparql.algebra.op.OpReduced;
import org.apache.jena.sparql.algebra.op.OpSequence;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.algebra.op.OpSlice;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.op.OpTopN;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.expr.ExprFunction;
import org.apache.jena.sparql.expr.ExprFunctionOp;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.FunctionLabel;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.aggregate.Accumulator;
import org.apache.jena.sparql.expr.aggregate.AggGroupConcat;
import org.apache.jena.sparql.expr.aggregate.AggGroupConcatDistinct;
import org.apache.jena.sparql.expr.aggregate.Aggregator;
import org.apache.jena.sparql.expr.nodevalue.NodeValueString;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.sparql.modify.request.UpdateAdd;
import org.apache.jena.sparql.modify.request.UpdateClear;
import org.apache.jena.sparql.modify.request.UpdateCopy;
import org.apache.jena.sparql.modify.request.UpdateCreate;
import org.apache.jena.sparql.modify.request.UpdateDataDelete;
import org.apache.jena.sparql.modify.request.UpdateDataInsert;
import org.apache.jena.sparql.modify.request.UpdateDeleteWhere;
import org.apache.jena.sparql.modify.request.UpdateDrop;
import org.apache.jena.sparql.modify.request.UpdateLoad;
import org.apache.jena.sparql.modify.request.UpdateModify;
import org.apache.jena.sparql.modify.request.UpdateMove;
import org.apache.jena.sparql.modify.request.UpdateVisitor;
import org.apache.jena.sparql.path.P_Alt;
import org.apache.jena.sparql.path.P_FixedLength;
import org.apache.jena.sparql.path.P_Inverse;
import org.apache.jena.sparql.path.P_Link;
import org.apache.jena.sparql.path.P_Mod;
import org.apache.jena.sparql.path.P_OneOrMore1;
import org.apache.jena.sparql.path.P_OneOrMoreN;
import org.apache.jena.sparql.path.P_Path0;
import org.apache.jena.sparql.path.P_Path1;
import org.apache.jena.sparql.path.P_Path2;
import org.apache.jena.sparql.path.P_ReverseLink;
import org.apache.jena.sparql.path.P_Seq;
import org.apache.jena.sparql.path.P_ZeroOrMore1;
import org.apache.jena.sparql.path.P_ZeroOrMoreN;
import org.apache.jena.sparql.path.P_ZeroOrOne;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.sse.Tags;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementAssign;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementExists;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementMinus;
import org.apache.jena.sparql.syntax.ElementNamedGraph;
import org.apache.jena.sparql.syntax.ElementNotExists;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.Template;
import org.apache.jena.update.Update;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.swows.vocabulary.SAS;
import org.swows.vocabulary.SP;
import org.swows.vocabulary.SPINX;
import org.swows.vocabulary.SWI;

public class SpinxFactory {
	
	private StreamRDF tripleStream;
	
	private Query query;
//	private Update update;
	
	private Map<Var,Node> varMap = new HashMap<Var,Node>();

	private Node defaultGraph = null;
	private Map<Node,Node> namedGraphMap = null;
	
	private Map<Var,Node> parentVarMap = null;
	
	private static Node createNode() {
//		return Skolemizer.getInstance().getNode();
		return NodeFactory.createAnon();
	}

	public SpinxFactory(
			Query query, StreamRDF tripleStream,
			Node defaultGraph, Map<Node,Node> namedGraphMap,
			Map<Var,Node> parentVarMap) {
		this.query = query;
		this.tripleStream = tripleStream;
		this.parentVarMap = parentVarMap;
		this.defaultGraph = defaultGraph;
		this.namedGraphMap = namedGraphMap;
	}

	public SpinxFactory(Query query, StreamRDF tripleStream, Map<Var,Node> parentVarMap) {
		this.query = query;
		this.tripleStream = tripleStream;
		this.parentVarMap = parentVarMap;
	}

//	public SpinxFactory(Update update, Graph graph, Map<Var,Node> parentVarMap) {
//		this.update = update;
//		this.graph = graph;
//		this.parentVarMap = parentVarMap;
//	}

	public SpinxFactory(
			StreamRDF tripleStream,
			Node defaultGraph, Map<Node,Node> namedGraphMap,
			Map<Var,Node> parentVarMap) {
		this.tripleStream = tripleStream;
		this.parentVarMap = parentVarMap;
		this.defaultGraph = defaultGraph;
		this.namedGraphMap = namedGraphMap;
	}

	public SpinxFactory(StreamRDF tripleStream, Map<Var,Node> parentVarMap) {
		this.tripleStream = tripleStream;
		this.parentVarMap = parentVarMap;
	}

	private Node fromNode(Node node) {
		if (node.isVariable())
			return fromVar(((Var) node));
		return node;
	}

	private Node fromVar(Var var) {
		Node varNode = varMap.get(var);
		if (varNode == null) {
			varNode = createNode();
			Node varNameNode = NodeFactory.createLiteral(var.getVarName());
			tripleStream.triple(new Triple(varNode, SP.varName.asNode(), varNameNode));
			tripleStream.triple(new Triple(varNode, RDF.type.asNode(), SP.Variable.asNode()));
			varMap.put(var, varNode);
		}
		return varNode;
	}

	private Node fromParentVar(Var var) {
		Node varNode = parentVarMap.get(var);
		if (varNode == null) {
			varNode = createNode();
			Node varNameNode = NodeFactory.createLiteral(var.getVarName());
			tripleStream.triple(new Triple(varNode, SP.varName.asNode(), varNameNode));
			tripleStream.triple(new Triple(varNode, RDF.type.asNode(), SP.Variable.asNode()));
			parentVarMap.put(var, varNode);
		}
		return varNode;
	}

	private Node fromAggregator(Aggregator aggregator, Var var) {
		Node aggrNode = createNode();
		tripleStream.triple(new Triple( aggrNode, RDF.type.asNode(), SP.Expression.asNode() ));
		tripleStream.triple(new Triple(aggrNode, RDF.type.asNode(), SP.Aggregation.asNode()));
		tripleStream.triple(new Triple(aggrNode, RDF.type.asNode(), AggregatorSymbols.getUriNode(aggregator)));
		ExprList exprList = aggregator.getExprList();
		for (Expr expression: exprList) {
			if (expression != null)
				tripleStream.triple(new Triple(aggrNode, SP.expression.asNode(), fromExpr(expression)));
			if (aggregator instanceof AggGroupConcat || aggregator instanceof AggGroupConcatDistinct) {
				Accumulator accumulator = aggregator.createAccumulator();
//				accumulator.accumulate(null, null);
//				accumulator.accumulate(null, null);
				String separator = accumulator.getValue().asString();
				Node scalarvalNode = createNode();
				tripleStream.triple(new Triple( aggrNode, SPINX.scalarval.asNode(), scalarvalNode ));
				tripleStream.triple(new Triple( scalarvalNode, SPINX.key.asNode(), NodeFactory.createLiteral("separator") ));
				tripleStream.triple(new Triple( scalarvalNode, SPINX.value.asNode(), NodeFactory.createLiteral(separator) ));
			}
		}
		return aggrNode;
	}
	
	private Node fromExpr(Expr expr) {
//		Node exprRootNode = createNode();
//		graph.add(new Triple( exprRootNode, RDF.type.asNode(), SP.Expression.asNode() ));
		if (expr instanceof ExprFunction) {
			ExprFunction functionExpr = (ExprFunction) expr;
//			String functionIRI = ((ExprFunction) expr).getFunctionIRI();
			Node exprRootNode = createNode();
			tripleStream.triple(new Triple( exprRootNode, RDF.type.asNode(), SP.Expression.asNode() ));
			tripleStream.triple(new Triple( exprRootNode, RDF.type.asNode(), SPINX.FunctionCall.asNode() ));
			String functionIRI = functionExpr.getFunctionIRI();
			if (functionIRI != null) {
				Node functionIRINode = NodeFactory.createURI( functionIRI );
				tripleStream.triple(new Triple( exprRootNode, SPINX.functionIRI.asNode(), functionIRINode ));
			}
			String opName = functionExpr.getOpName();
			if (opName != null) {
				Node opNameNode = NodeFactory.createLiteral( opName );
				tripleStream.triple(new Triple( exprRootNode, SPINX.opName.asNode(), opNameNode ));
			}
			FunctionLabel functionLabel = functionExpr.getFunctionSymbol();
			if (functionLabel != null) {
				String functionSymbol = functionLabel.getSymbol();
				if (functionSymbol != null) {
					Node functionSymbolNode = NodeFactory.createLiteral( functionSymbol );
					tripleStream.triple(new Triple( exprRootNode, SPINX.functionLabel.asNode(), functionSymbolNode ));
					KnownFunctionsMapping.set(functionSymbol, functionExpr.getClass());
				}
			}
			Iterator<Expr> args = functionExpr.getArgs().iterator();
			int argCount = 0;
			while (args.hasNext()) {
				Node argPredNode = NodeFactory.createURI( SP.getURI() + "arg" + ++argCount );
				Node argNode = fromExpr(args.next());
				tripleStream.triple(new Triple( exprRootNode, argPredNode, argNode ));
			}
			if (expr instanceof ExprFunctionOp) {
				tripleStream.triple(new Triple( exprRootNode, RDF.type.asNode(), SPINX.OpCall.asNode() ));
				Element subElem = ((ExprFunctionOp) expr).getElement();
				tripleStream.triple(new Triple( exprRootNode, SPINX.element.asNode(), fromElement(subElem) ));
			}
			return exprRootNode;
		} else if (expr instanceof ExprVar) {
			return fromVar( ((ExprVar) expr).asVar() );
		} else if (expr instanceof NodeValue) {
			return fromNode( ((NodeValue) expr).asNode() );
		} else if (expr instanceof ExprAggregator) {
			return fromAggregator( ((ExprAggregator) expr).getAggregator(), ((ExprAggregator) expr).getVar() );
		} else
			return SPINX.UnknownExpr.asNode();
//		return exprRootNode;
	}

	private Node fromPath(Path path) {
		if (path instanceof P_Path0) {
			Node linkNode = fromNode( ((P_Path0) path).getNode() );
			if (path instanceof P_Link)
				return linkNode;
			else if (path instanceof P_ReverseLink) {
				Node pathRootNode = createNode();
				tripleStream.triple(new Triple(pathRootNode, RDF.type.asNode(), SP.Path.asNode()));
				tripleStream.triple(new Triple(pathRootNode, RDF.type.asNode(), SP.ReversePath.asNode()));
				tripleStream.triple(new Triple(pathRootNode, SP.subPath.asNode(), linkNode));
				return pathRootNode;
			} else {
				// TODO: exception for unrecognized Path
				return null;
			}
		} else {
			Node pathRootNode = createNode();
			tripleStream.triple(new Triple(pathRootNode, RDF.type.asNode(), SP.Path.asNode()));
			if (path instanceof P_Path1) {
				Node subPathNode = fromPath( ((P_Path1) path).getSubPath() );
				tripleStream.triple(new Triple(pathRootNode, SP.subPath.asNode(), subPathNode));
				if (path instanceof P_Inverse) {
					tripleStream.triple(new Triple(pathRootNode, RDF.type.asNode(), SP.ReversePath.asNode()));
				} else {
					long min = 0;
					long max = -1; // -1 as infinity (unlimited)
					if (path instanceof P_FixedLength) {
						min = max = ((P_FixedLength) path).getCount();
					} else if (path instanceof P_Mod) {
						min = ((P_Mod) path).getMin();
						max = ((P_Mod) path).getMax();
					} else if (path instanceof P_OneOrMore1 || path instanceof P_OneOrMoreN) {
						min = 1;
					} else if (path instanceof P_ZeroOrMore1 || path instanceof P_ZeroOrMoreN) {
					} else if (path instanceof P_ZeroOrOne) {
						max = 1;
					} else {
						// TODO: exception for unrecognized Path
					}
					tripleStream.triple(new Triple(pathRootNode, RDF.type.asNode(), SP.ModPath.asNode()));
					if (min != 0) {
						Node minNode = NodeFactory.createLiteral(Long.toString(min), XSDDatatype.XSDinteger);
						tripleStream.triple(new Triple(pathRootNode, SP.modMin.asNode(), minNode));
					}
					if (max != 0) {
						Node maxNode = NodeFactory.createLiteral(Long.toString(max), XSDDatatype.XSDinteger);
						tripleStream.triple(new Triple(pathRootNode, SP.modMax.asNode(), maxNode));
					}
				}
			} else if (path instanceof P_Path2) {
				Node path1Node = fromPath( ((P_Path2) path).getLeft() );
				Node path2Node = fromPath( ((P_Path2) path).getRight() );
				tripleStream.triple(new Triple(pathRootNode, SP.path1.asNode(), path1Node));
				tripleStream.triple(new Triple(pathRootNode, SP.path2.asNode(), path2Node));
				if (path instanceof P_Alt)
					tripleStream.triple(new Triple(pathRootNode, RDF.type.asNode(), SP.AltPath.asNode()));
				else if (path instanceof P_Seq)
					tripleStream.triple(new Triple(pathRootNode, RDF.type.asNode(), SP.SeqPath.asNode()));
				else {
				// TODO: exception for unrecognized Path
				}
			}
			return pathRootNode;
		}
	}
	
	private Node fromTriple(Triple triple) {
		Node elementRootNode = createNode();
		tripleStream.triple(new Triple(
				elementRootNode,
				RDF.type.asNode(),
				NodeFactory.createURI(SP.getURI() + "Element")));
		tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SP.TriplePattern.asNode()));
		Node subjectNode = fromNode(triple.getSubject());
		Node predicateNode = fromNode(triple.getPredicate());
		Node objectNode = fromNode(triple.getObject());
		tripleStream.triple(new Triple( elementRootNode, SP.subject.asNode(), subjectNode));
		tripleStream.triple(new Triple( elementRootNode, SP.predicate.asNode(), predicateNode));
		tripleStream.triple(new Triple( elementRootNode, SP.object.asNode(), objectNode));
		return elementRootNode;
	}

	private Node fromTriplePath(TriplePath triplePath) {
		if (triplePath.isTriple())
			return fromTriple(triplePath.asTriple());
		Node elementRootNode = createNode();
		tripleStream.triple(new Triple(
				elementRootNode,
				RDF.type.asNode(),
				NodeFactory.createURI(SP.getURI() + "Element")));
		Node subjectNode = fromNode(triplePath.getSubject());
		Node pathNode = fromPath(triplePath.getPath());
		Node objectNode = fromNode(triplePath.getObject());
		tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SP.TriplePath.asNode()));
		tripleStream.triple(new Triple( elementRootNode, SP.subject.asNode(), subjectNode));
		tripleStream.triple(new Triple( elementRootNode, SP.path.asNode(), pathNode));
		tripleStream.triple(new Triple( elementRootNode, SP.object.asNode(), objectNode));
		
		return elementRootNode;
	}
	
	private Node fromGraphNameNode(Node graphNameNode) {
		Node newGraphNameNode = null;
		if (namedGraphMap != null)
			newGraphNameNode = namedGraphMap.get(graphNameNode);
		return (newGraphNameNode != null) ? newGraphNameNode : fromNode(graphNameNode);
	}

	private Node fromElement(Element element) {
		Node elementRootNode = createNode();
		tripleStream.triple(new Triple(
				elementRootNode,
				RDF.type.asNode(),
				NodeFactory.createURI(SP.getURI() + "Element")));
		List<Element> childList = null;
		if (element instanceof ElementExists) {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SPINX.Exists.asNode()));
			Node childElement = fromElement( ((ElementExists) element).getElement() );
			tripleStream.triple(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementNotExists) {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SPINX.NotExists.asNode()));
			Node childElement = fromElement( ((ElementNotExists) element).getElement() );
			tripleStream.triple(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementAssign) {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SPINX.Assign.asNode()));
			Node var = fromVar( ((ElementAssign) element).getVar() );
			Node expr = fromExpr( ((ElementAssign) element).getExpr() );
			tripleStream.triple(new Triple( elementRootNode, SPINX.var.asNode(), var ));
			tripleStream.triple(new Triple( elementRootNode, SPINX.expr.asNode(), expr ));
		} else if (element instanceof ElementBind) {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SP.Bind.asNode()));
			Node var = fromVar( ((ElementBind) element).getVar() );
			Node expr = fromExpr( ((ElementBind) element).getExpr() );
			tripleStream.triple(new Triple( elementRootNode, SPINX.var.asNode(), var ));
			tripleStream.triple(new Triple( elementRootNode, SPINX.expr.asNode(), expr ));
		} else if (element instanceof ElementFilter) {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SP.Filter.asNode()));
			Node expr = fromExpr( ((ElementFilter) element).getExpr() );
			tripleStream.triple(new Triple( elementRootNode, SPINX.expr.asNode(), expr ));
		} else if (element instanceof ElementGroup) {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SPINX.ElementGroup.asNode()));
			childList = ((ElementGroup) element).getElements();
		} else if (element instanceof ElementMinus) {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SP.Minus.asNode()));
			Node childElement = fromElement( ((ElementMinus) element).getMinusElement() );
			tripleStream.triple(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementNamedGraph) {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SP.NamedGraph.asNode()));
			Node graphNameNode = fromGraphNameNode( ((ElementNamedGraph) element).getGraphNameNode() );
			tripleStream.triple(new Triple( elementRootNode, SP.graphNameNode.asNode(), graphNameNode ));
			Node childElement = fromElement( ((ElementNamedGraph) element).getElement() );
			tripleStream.triple(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementOptional) {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SP.Optional.asNode()));
			Node childElement = fromElement( ((ElementOptional) element).getOptionalElement() );
			tripleStream.triple(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementPathBlock) {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SPINX.ElementGroup.asNode()));
			Iterator<TriplePath> triplePaths = ((ElementPathBlock) element).patternElts();
			while (triplePaths.hasNext()) {
				Node childNode = fromTriplePath( triplePaths.next() );
				tripleStream.triple(new Triple(elementRootNode, SPINX.element.asNode(), childNode));
			}
		} else if (element instanceof ElementTriplesBlock) {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SPINX.ElementGroup.asNode()));
			Iterator<Triple> triples = ((ElementTriplesBlock) element).patternElts();
			while (triples.hasNext()) {
				Node childNode = fromTriple( triples.next() );
				tripleStream.triple(new Triple(elementRootNode, SPINX.element.asNode(), childNode));
			}
		} else if (element instanceof ElementService) {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SP.Service.asNode()));
			Node serviceNode = fromNode( ((ElementService) element).getServiceNode() );
			tripleStream.triple(new Triple( elementRootNode, SP.serviceURI.asNode(), serviceNode ));
			Node childElement = fromElement( ((ElementService) element).getElement() );
			tripleStream.triple(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementSubQuery) {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SP.SubQuery.asNode()));
			Query subQuery = ((ElementSubQuery) element).getQuery();
			Node subQueryNode = createNode();
			tripleStream.triple(new Triple( elementRootNode, SP.query.asNode(), subQueryNode ));
			fromQuery( subQuery, tripleStream, subQueryNode, varMap );
		} else if (element instanceof ElementUnion) {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SP.Union.asNode()));
			childList = ((ElementUnion) element).getElements();
		} else {
			tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SPINX.EmptyElement.asNode()));
		}
		if ( childList != null ) {
			Iterator<Element> elems = childList.iterator();
			while (elems.hasNext()) {
				Node childNode = fromElement( elems.next());
				tripleStream.triple(new Triple(elementRootNode, SPINX.element.asNode(), childNode));
			}
		}
		return elementRootNode;
	}
	
	private Node fromRootElement(Element element) {
		Node innerElement = fromElement(element);
		if (defaultGraph == null || element instanceof ElementNamedGraph)
			return innerElement;
		Node elementRootNode = createNode();
		tripleStream.triple(new Triple(
				elementRootNode,
				RDF.type.asNode(),
				NodeFactory.createURI(SP.getURI() + "Element")));
		tripleStream.triple(new Triple( elementRootNode, RDF.type.asNode(), SP.NamedGraph.asNode()));
		tripleStream.triple(new Triple( elementRootNode, SP.graphNameNode.asNode(), defaultGraph ));
		tripleStream.triple(new Triple( elementRootNode, SPINX.element.asNode(), innerElement ));
		return elementRootNode;
	}
	
	private Node fromTemplateTriple(Triple triple) {
		Node tripleRootNode = createNode();
		tripleStream.triple(new Triple( tripleRootNode, RDF.type.asNode(), SP.TripleTemplate.asNode()));
		Node subjNode = fromNode(triple.getSubject());
		tripleStream.triple(new Triple(tripleRootNode, SP.subject.asNode(), subjNode));
		Node predNode = fromNode(triple.getPredicate());
		tripleStream.triple(new Triple(tripleRootNode, SP.predicate.asNode(), predNode));
		Node objNode = fromNode(triple.getObject());
		tripleStream.triple(new Triple(tripleRootNode, SP.object.asNode(), objNode));
		return tripleRootNode;
	}	

	private Node fromTemplate(Template template) {
		Iterator<Triple> templateTriples = template.getTriples().iterator();
		Node templateRootNode = createNode();
		tripleStream.triple(new Triple( templateRootNode, RDF.type.asNode(), SPINX.TripleTemplateSet.asNode()));
		while (templateTriples.hasNext()) {
			Node tripleNode = fromTemplateTriple(templateTriples.next());
			tripleStream.triple(new Triple(templateRootNode, SPINX.triple.asNode(), tripleNode));
		}
		return templateRootNode;
	}	

	private Node fromTriples(List<Triple> triples) {
		Node triplesRootNode = createNode();
		for (Triple triple : triples)
			tripleStream.triple(new Triple(triplesRootNode, SPINX.triple.asNode(), fromTemplateTriple(triple)));
		return triplesRootNode;
	}	

	private Node fromQuads(List<Quad> quads) {
		List<Triple> defaultGraphTriples = new Vector<Triple>();
		Map<Node,List<Triple>> namedGraphTriples = new HashMap<Node, List<Triple>>();
		for (Quad quad: quads) {
			if (quad.isDefaultGraph())
				defaultGraphTriples.add(quad.asTriple());
			else {
				Node currGraphName = quad.getGraph();
				List<Triple> currGraphTriples = namedGraphTriples.get(currGraphName);
				if (currGraphTriples == null) {
					currGraphTriples = new Vector<Triple>();
					namedGraphTriples.put(currGraphName, currGraphTriples);
				}
				currGraphTriples.add(quad.asTriple());
			}
		}
		Node quadsRootNode = fromTriples(defaultGraphTriples);
		for (Node graphName : namedGraphTriples.keySet()) {
			Node namedGraphNode = fromTriples(namedGraphTriples.get(graphName));
			tripleStream.triple(new Triple(namedGraphNode, RDF.type.asNode(), SP.NamedGraph.asNode()));
			tripleStream.triple(new Triple(quadsRootNode, SP.named.asNode(), namedGraphNode));
		}
		return quadsRootNode;
	}	

	private void fromAsk(Node queryRootNode) {
		tripleStream.triple(new Triple(queryRootNode, RDF.type.asNode(), SP.Ask.asNode()));
	}

	private void fromConstruct(Node queryRootNode) {
		tripleStream.triple(new Triple(queryRootNode, RDF.type.asNode(), SP.Construct.asNode()));
		tripleStream.triple( new Triple(
				queryRootNode,
				SP.templates.asNode(),
				fromTemplate(query.getConstructTemplate()) ) );
	}

	private void fromDescribe(Node queryRootNode) {
		tripleStream.triple(new Triple(queryRootNode, RDF.type.asNode(), SP.Describe.asNode()));
	}

	private void fromSelect(Node queryRootNode) {
		tripleStream.triple(new Triple(queryRootNode, RDF.type.asNode(), SP.Select.asNode()));
//		List<String> varList = query.getResultVars();
//		if (varList != null) {
//			for (String var : varList) {
//				Node resultNode = createNode();
//				graph.add(new Triple(queryRootNode, SPINX.resultVariable.asNode(), resultNode));
//				Node varNode = fromVar(var, graph);
//				graph.add(new Triple(queryRootNode, SP.as.asNode(), varNode));
//				graph.add(new Triple(queryRootNode, SP.expression.asNode(), varNode));
//			}
//		}
		VarExprList projectedList = query.getProject();
		for ( Var projectedVar : projectedList.getVars() ) {
			Node projectedNode = createNode();
			tripleStream.triple(new Triple(queryRootNode, SPINX.resultVariable.asNode(), projectedNode));
			tripleStream.triple(new Triple(projectedNode, SP.as.asNode(), fromParentVar(projectedVar)));
			Expr projectedExpr = projectedList.getExpr(projectedVar);
			if (projectedExpr != null)
				tripleStream.triple(new Triple(projectedNode, SP.expression.asNode(), fromExpr(projectedExpr)));
		}
		if (query.isDistinct())
			tripleStream.triple(new Triple(queryRootNode, SP.distinct.asNode(), NodeFactory.createLiteral("true", XSDDatatype.XSDboolean)));

		VarExprList groupByExprList = query.getGroupBy();
		if (groupByExprList != null) {
			for (Var groupByVar : groupByExprList.getVars() ) {
				Node groupByNode = createNode();
				tripleStream.triple(new Triple(queryRootNode, SP.groupBy.asNode(), groupByNode));
				tripleStream.triple(new Triple(groupByNode, SP.as.asNode(), fromParentVar(groupByVar)));
				Expr groupByExpr = groupByExprList.getExpr(groupByVar);
				if (groupByExpr != null)
					tripleStream.triple(new Triple(groupByNode, SP.expression.asNode(), fromExpr(groupByExpr)));
			}
		}
	}
	
	private void fromDeleteInsert(UpdateModify update, Node queryRootNode) {
		tripleStream.triple(new Triple(queryRootNode, RDF.type.asNode(), SP.Modify.asNode()));
		tripleStream.triple( new Triple(
				queryRootNode,
				SP.where.asNode(),
				fromRootElement(update.getWherePattern() ) ) );
		tripleStream.triple( new Triple(
				queryRootNode,
				SAS.op.asNode(),
				fromRootOp(Algebra.compile(update.getWherePattern())) ) );
//		Node deleteNode = GraphUtils.getSingleValueOptProperty(graph, queryRootNode, SP.deletePattern.asNode());
		List<Quad> deleteQuads = update.getDeleteQuads();
		if (deleteQuads != null && !deleteQuads.isEmpty()) {
			tripleStream.triple( new Triple(
					queryRootNode,
					SP.deletePattern.asNode(),
					fromQuads(deleteQuads) ) );
		}
		List<Quad> insertQuads = update.getInsertQuads();
		if (insertQuads != null && !insertQuads.isEmpty()) {
			tripleStream.triple( new Triple(
					queryRootNode,
					SP.insertPattern.asNode(),
					fromQuads(insertQuads) ) );
		}
		List<Node> usingNodes = update.getUsing();
		for (Node usingNode : usingNodes)
			tripleStream.triple( new Triple(
					queryRootNode,
					SP.using.asNode(),
					usingNode ) );
		List<Node> usingNamedNodes = update.getUsingNamed();
		for (Node usingNamedNode : usingNamedNodes)
			tripleStream.triple( new Triple(
					queryRootNode,
					SP.usingNamed.asNode(),
					usingNamedNode ) );
		Node withNode = update.getWithIRI();
		if (withNode != null)
			tripleStream.triple( new Triple(
					queryRootNode,
					SP.with.asNode(),
					withNode ) );
	}

//	private void fromDeleteWhere(UpdateDeleteWhere update, Node queryRootNode) {
//		graph.add(new Triple(queryRootNode, RDF.type.asNode(), SP.DeleteWhere.asNode()));
//		graph.add( new Triple(
//				queryRootNode,
//				SP.where.asNode(),
//				fromElement(update. ) ) );
////		Node deleteNode = GraphUtils.getSingleValueOptProperty(graph, queryRootNode, SP.deletePattern.asNode());
//		List<Quad> deleteQuads = update.getDeleteQuads();
//		if (deleteQuads != null && !deleteQuads.isEmpty()) {
//			graph.add( new Triple(
//					queryRootNode,
//					SP.deletePattern.asNode(),
//					fromQuads(deleteQuads) ) );
//		}
//		List<Quad> insertQuads = update.getInsertQuads();
//		if (insertQuads != null && !insertQuads.isEmpty()) {
//			graph.add( new Triple(
//					queryRootNode,
//					SP.insertPattern.asNode(),
//					fromQuads(insertQuads) ) );
//		}
//	}
	
	private Node fromTriplePattern(Triple triplePattern) {
		Node triplePatternNode = createNode();
		tripleStream.triple(new Triple(triplePatternNode, RDF.type.asNode(), SAS.TriplePattern.asNode()));
		tripleStream.triple( new Triple(
				triplePatternNode,
				SP.subject.asNode(),
				fromNode(triplePattern.getSubject()) ) );
		tripleStream.triple( new Triple(
				triplePatternNode,
				SP.predicate.asNode(),
				fromNode(triplePattern.getPredicate()) ) );
		tripleStream.triple( new Triple(
				triplePatternNode,
				SP.object.asNode(),
				fromNode(triplePattern.getObject()) ) );
		return triplePatternNode;
	}
	
	private Node fromTriplePattern(TriplePath triplePattern) {
		if (triplePattern.isTriple())
			return fromTriplePattern(triplePattern.asTriple());
		Node triplePatternNode = createNode();
		tripleStream.triple(new Triple(triplePatternNode, RDF.type.asNode(), SAS.TriplePattern.asNode()));

		tripleStream.triple( new Triple(
				triplePatternNode,
				SP.subject.asNode(),
				fromNode(triplePattern.getSubject()) ) );
		tripleStream.triple( new Triple(
				triplePatternNode,
				SP.path.asNode(),
				fromPath(triplePattern.getPath()) ) );
		tripleStream.triple( new Triple(
				triplePatternNode,
				SP.object.asNode(),
				fromNode(triplePattern.getObject()) ) );
		return triplePatternNode;
	}
	
	private Node fromQuadPattern(Quad quadPattern) {
		Node quadPatternNode = createNode();
		tripleStream.triple(new Triple(quadPatternNode, RDF.type.asNode(), SAS.QuadPattern.asNode()));
		tripleStream.triple( new Triple(
				quadPatternNode,
				SP.graphNameNode.asNode(),
				fromNode(quadPattern.getGraph()) ) );
		tripleStream.triple( new Triple(
				quadPatternNode,
				SP.subject.asNode(),
				fromNode(quadPattern.getSubject()) ) );
		tripleStream.triple( new Triple(
				quadPatternNode,
				SP.predicate.asNode(),
				fromNode(quadPattern.getPredicate()) ) );
		tripleStream.triple( new Triple(
				quadPatternNode,
				SP.object.asNode(),
				fromNode(quadPattern.getObject()) ) );
		return quadPatternNode;
	}
	
	private Node fromTable(Table table) {
		Node tableNode = createNode();
		tripleStream.triple(new Triple(tableNode, RDF.type.asNode(), SAS.Table.asNode()));
		ResultSet resultSet = table.toResultSet();

//		Node prevColNode = null;
		for (String varName : resultSet.getResultVars()) {
			tripleStream.triple(new Triple(tableNode, SAS.varName.asNode(), NodeFactory.createLiteral(varName)));
		}
		
//		QueryIterator rows = table.
		Node prevRowNode = null;
		while (resultSet.hasNext()) {
			Binding row = resultSet.nextBinding();
			Node rowNode = createNode();
			tripleStream.triple(new Triple(rowNode, RDF.type.asNode(), SAS.Row.asNode()));
			
			Iterator<Var> vars = row.vars();
			while (vars.hasNext()) {
				Var var = vars.next();
				Node bindingNode = createNode();
				tripleStream.triple(new Triple(bindingNode, RDF.type.asNode(), SAS.Binding.asNode()));
				tripleStream.triple(new Triple(rowNode, SAS.binding.asNode(), bindingNode));
				tripleStream.triple(new Triple(
						bindingNode,
						SAS.variable.asNode(),
						fromVar(var)));
				tripleStream.triple(new Triple(
						bindingNode,
						SAS.value.asNode(),
						fromNode(row.get(var))));
			}
			
			if (prevRowNode != null) {
				tripleStream.triple(new Triple(prevRowNode, SAS.nextRow.asNode(), rowNode));
				tripleStream.triple(new Triple(rowNode, SAS.prevRow.asNode(), prevRowNode));
			} else
				tripleStream.triple(new Triple(tableNode, SAS.firstRow.asNode(), rowNode));
			prevRowNode = rowNode;
		}
		if (prevRowNode != null)
			tripleStream.triple(new Triple(tableNode, SAS.lastRow.asNode(), prevRowNode));
		
		return tableNode;
	}
	
	private class OpVisitorToSas implements OpVisitor {
		private Node opNode;
		
		private OpVisitorToSas(Node opNode) {
			this.opNode = opNode;
		}
		
		public void visit(OpBGP op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.BGP.asNode()));
			for (Triple triplePattern : op.getPattern()) {
				tripleStream.triple( new Triple(
						opNode,
						SAS.triplePattern.asNode(),
						fromTriplePattern(triplePattern) ) );
			}
		}

		public void visit(OpQuadPattern op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.BGP.asNode()));
			for (Quad quadPattern : op.getPattern()) {
				tripleStream.triple( new Triple(
						opNode,
						SAS.quadPattern.asNode(),
						fromQuadPattern(quadPattern) ) );
			}
		}

		public void visit(OpTriple op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.BGP.asNode()));
			tripleStream.triple( new Triple(
					opNode,
					SAS.triplePattern.asNode(),
					fromTriplePattern(op.getTriple()) ) );
		}

		public void visit(OpQuad op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.BGP.asNode()));
			tripleStream.triple( new Triple(
					opNode,
					SAS.quadPattern.asNode(),
					fromQuadPattern(op.getQuad()) ) );
		}

		public void visit(OpPath op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.BGP.asNode()));
			tripleStream.triple( new Triple(
					opNode,
					SAS.triplePattern.asNode(),
					fromTriplePattern(op.getTriplePath()) ) );
		}

		public void visit(OpTable op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.FromTable.asNode()));
			tripleStream.triple( new Triple(
					opNode,
					SAS.table.asNode(),
					fromTable(op.getTable()) ) );
		}

		public void visit(OpNull op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Null.asNode()));
		}

		public void visit(OpProcedure op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Procedure.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.procedureName.asNode(), fromNode(op.getProcId())));
			int argCount = 0;
			for (Expr e : op.getArgs()) {
				Node exprNode = fromExpr(e);
				Node argPredNode = NodeFactory.createURI( SP.getURI() + "arg" + ++argCount );
				tripleStream.triple(new Triple( opNode, argPredNode, exprNode ));
			}
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
		}

		public void visit(OpPropFunc op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.PropertyFunction.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.propertyFunctionName.asNode(), fromNode(op.getProperty())));
			tripleStream.triple(new Triple(opNode, SAS.subjectArg.asNode(), fromNode(op.getSubjectArgs().getArg())));
			tripleStream.triple(new Triple(opNode, SAS.objectArg.asNode(), fromNode(op.getObjectArgs().getArg())));
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
		}

		public void visit(OpFilter op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Filter.asNode()));
			for (Expr e : op.getExprs()) {
				Node exprNode = fromExpr(e);
				tripleStream.triple(new Triple( opNode, SAS.expr.asNode(), exprNode ));
			}
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
		}

		public void visit(OpGraph op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Graph.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.graphNameNode.asNode(), fromGraphNameNode(op.getNode())));
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
		}

		public void visit(OpService op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Service.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.serviceNode.asNode(), fromNode(op.getService())));
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
		}

		public void visit(OpDatasetNames op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.DatasetNames.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.graphNameNode.asNode(), fromNode(op.getGraphNode())));
		}

		public void visit(OpLabel op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Labelled.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.label.asNode(), NodeFactory.createLiteral(op.getObject().toString())));
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
		}

		public void visit(OpAssign op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Assign.asNode()));
			VarExprList varExprList = op.getVarExprList();
			for (Var var : varExprList.getVars()) {
				Node assignmentNode = createNode();
				tripleStream.triple(new Triple(opNode, SAS.assignment.asNode(), assignmentNode));
				tripleStream.triple(new Triple(assignmentNode, RDF.type.asNode(), SAS.Assign.asNode()));
				tripleStream.triple(new Triple(assignmentNode, SAS.variable.asNode(), fromVar(var)));
				tripleStream.triple(new Triple(assignmentNode, SAS.expr.asNode(), fromExpr(varExprList.getExpr(var))));
			}
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
		}

		public void visit(OpExtend op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Extend.asNode()));
			VarExprList varExprList = op.getVarExprList();
			for (Var var : varExprList.getVars()) {
				Node assignmentNode = createNode();
				tripleStream.triple(new Triple(opNode, SAS.extension.asNode(), assignmentNode));
				tripleStream.triple(new Triple(assignmentNode, RDF.type.asNode(), SAS.Assign.asNode()));
				tripleStream.triple(new Triple(assignmentNode, SAS.variable.asNode(), fromVar(var)));
				tripleStream.triple(new Triple(assignmentNode, SAS.expr.asNode(), fromExpr(varExprList.getExpr(var))));
			}
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
		}

		public void visit(OpJoin op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Join.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.leftOp.asNode(), fromOp(op.getLeft())));
			tripleStream.triple(new Triple(opNode, SAS.rightOp.asNode(), fromOp(op.getRight())));
		}

		public void visit(OpLeftJoin op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.LeftJoin.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.leftOp.asNode(), fromOp(op.getLeft())));
			tripleStream.triple(new Triple(opNode, SAS.rightOp.asNode(), fromOp(op.getRight())));
		}

		public void visit(OpUnion op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Union.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.leftOp.asNode(), fromOp(op.getLeft())));
			tripleStream.triple(new Triple(opNode, SAS.rightOp.asNode(), fromOp(op.getRight())));
		}

		public void visit(OpDiff op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Diff.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.leftOp.asNode(), fromOp(op.getLeft())));
			tripleStream.triple(new Triple(opNode, SAS.rightOp.asNode(), fromOp(op.getRight())));
		}

		public void visit(OpMinus op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Minus.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.leftOp.asNode(), fromOp(op.getLeft())));
			tripleStream.triple(new Triple(opNode, SAS.rightOp.asNode(), fromOp(op.getRight())));
		}

		public void visit(OpConditional op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Conditional.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.leftOp.asNode(), fromOp(op.getLeft())));
			tripleStream.triple(new Triple(opNode, SAS.rightOp.asNode(), fromOp(op.getRight())));
		}

		public void visit(OpSequence op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Sequence.asNode()));
//			graph.add(new Triple(opNode, SAS.subOpList, SAS.Sequence.asNode()));
			Iterator<Op> it = op.iterator();
			while (it.hasNext()) {
				tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(it.next())));
			}
		}

		public void visit(OpDisjunction op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Disjunction.asNode()));
			Iterator<Op> it = op.iterator();
			while (it.hasNext()) {
				tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(it.next())));
			}
		}

		public void visit(OpExt op) {
			// TODO Auto-generated method stub
		}

		public void visit(OpList op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.List.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
		}

		public void visit(OpOrder op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Order.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
			// TODO Auto-generated method stub
		}

		public void visit(OpProject op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Project.asNode()));
			for (Var var : op.getVars()) {
				tripleStream.triple(new Triple(opNode, SAS.variable.asNode(), fromVar(var)));
			}
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
		}

		public void visit(OpReduced op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Reduced.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
		}

		public void visit(OpDistinct op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Distinct.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
		}

		public void visit(OpSlice op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Slice.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.start.asNode(), NodeFactory.createLiteral(Long.toString(op.getStart()), XSDDatatype.XSDinteger)));
			tripleStream.triple(new Triple(opNode, SAS.length.asNode(), NodeFactory.createLiteral(Long.toString(op.getLength()), XSDDatatype.XSDinteger)));
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
		}

		private void aggregator(Node groupNode, Var var, Expr expr) {
			Node aggrNode = createNode();
			tripleStream.triple(new Triple(groupNode, SP.groupBy.asNode(), aggrNode));
			tripleStream.triple(new Triple(aggrNode, SP.as.asNode(), fromParentVar(var)));
			tripleStream.triple(new Triple(aggrNode, SP.expression.asNode(), fromExpr(expr)));
		}
		
		public void visit(OpGroup op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Group.asNode()));

			VarExprList groupVars = op.getGroupVars();
			if (groupVars != null) {
				for (Var aggrVar : groupVars.getVars() ) {
					Expr aggrExpr = groupVars.getExpr(aggrVar);
					aggregator(opNode, aggrVar, aggrExpr);
				}
			}

			List<ExprAggregator> exprAggregators = op.getAggregators();
			if (exprAggregators != null) {
				for (ExprAggregator exprAggregator : exprAggregators ) {
					Var aggrVar = exprAggregator.getVar();
//					Aggregator aggr = exprAggregator.getAggregator();
//					Expr aggrExpr = aggr.getExpr();
					aggregator(opNode, aggrVar, exprAggregator);
				}
			}
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
		}

		public void visit(OpTopN op) {
			tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.TopN.asNode()));
			tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), fromOp(op.getSubOp())));
			// TODO Auto-generated method stub
		}

		public void visit(OpQuadBlock arg0) {
			// TODO Auto-generated method stub
			
		}
			
	}
	
	private Node fromOp(Op op) {
		Node opNode = createNode();
		tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Op.asNode()));
		tripleStream.triple( new Triple(
				opNode,
				SAS.name.asNode(),
				NodeFactory.createLiteral(op.getName()) ) );
		op.visit(new OpVisitorToSas(opNode));
		return opNode;
	}

	private Node fromRootOp(Op op) {
		Node opInnerNode = fromOp(op);
		if (defaultGraph == null || op instanceof OpGraph)
			return opInnerNode;
		Node opNode = createNode();
		tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Op.asNode()));
		tripleStream.triple( new Triple(
				opNode,
				SAS.name.asNode(),
				NodeFactory.createLiteral(Tags.tagGraph) ) );
		tripleStream.triple(new Triple(opNode, RDF.type.asNode(), SAS.Graph.asNode()));
		tripleStream.triple(new Triple(opNode, SAS.graphNameNode.asNode(), defaultGraph));
		tripleStream.triple(new Triple(opNode, SAS.subOp.asNode(), opInnerNode));
		return opNode;
	}

	public void fromQuery(Node queryRootNode) {
		tripleStream.triple(new Triple(queryRootNode, RDF.type.asNode(), SP.Query.asNode()));
		tripleStream.triple( new Triple(
				queryRootNode,
				SP.where.asNode(),
				fromRootElement( query.getQueryPattern() ) ) );
		tripleStream.triple( new Triple(
				queryRootNode,
				SAS.op.asNode(),
				fromRootOp(Algebra.compile(query)) ) );
		if (query.isAskType())
			fromAsk(queryRootNode);
		else if (query.isConstructType())
			fromConstruct(queryRootNode);
		else if (query.isDescribeType())
			fromDescribe(queryRootNode);
		else if (query.isSelectType())
			fromSelect(queryRootNode);
		// if unknown?
	}

	public void fromUpdate(Update update, final Node queryRootNode) {
//		graph.add(new Triple(queryRootNode, RDF.type.asNode(), SP.Query.asNode()));
//		graph.add( new Triple(
//				queryRootNode,
//				SP.where.asNode(),
//				fromElement(query.getQueryPattern()) ) );
		update.visit(new UpdateVisitor() {
			
				public void visit(UpdateModify update) {
				fromDeleteInsert(update, queryRootNode);
			}
			
				public void visit(UpdateDeleteWhere update) {
//				fromDeleteWhere(update, queryRootNode);
			}
			
				public void visit(UpdateDataDelete update) {
				// TODO Auto-generated method stub
				
			}
			
				public void visit(UpdateDataInsert update) {
				// TODO Auto-generated method stub
				
			}
			
				public void visit(UpdateMove update) {
				// TODO Auto-generated method stub
				
			}
			
				public void visit(UpdateCopy update) {
				// TODO Auto-generated method stub
				
			}
			
				public void visit(UpdateAdd update) {
				// TODO Auto-generated method stub
				
			}
			
				public void visit(UpdateLoad update) {
				// TODO Auto-generated method stub
				
			}
			
				public void visit(UpdateCreate update) {
				// TODO Auto-generated method stub
				
			}
			
				public void visit(UpdateClear update) {
				// TODO Auto-generated method stub
				
			}
			
				public void visit(UpdateDrop update) {
				// TODO Auto-generated method stub
				
			}

				public Sink<Quad> createDeleteDataSink() {
				// TODO Auto-generated method stub
				return null;
			}

				public Sink<Quad> createInsertDataSink() {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	public static void fromUpdate(Update update, Graph graph, Node updateRootNode, Node defaultGraph, Map<Node,Node> namedGraphMap) {
		SpinxFactory factory = new SpinxFactory(new StreamRDFToGraph(graph), defaultGraph, namedGraphMap, new HashMap<Var, Node>() );
		factory.fromUpdate(update, updateRootNode);
	}
	
	public static void fromUpdate(Update update, Graph graph, Node defaultGraph, Map<Node,Node> namedGraphMap) {
		fromUpdate(update, graph, SWI.GraphRoot.asNode(), defaultGraph, namedGraphMap);
	}
	
	public static void fromUpdate(Update update, Graph graph, Node updateRootNode) {
		fromUpdate(update, graph, updateRootNode, null, null);
	}
	
	public static void fromUpdate(Update update, Graph graph) {
		fromUpdate(update, graph, SWI.GraphRoot.asNode());
	}
	
	public static void fromUpdate(Update update, StreamRDF tripleStream, Node updateRootNode, Node defaultGraph, Map<Node,Node> namedGraphMap) {
		SpinxFactory factory = new SpinxFactory(tripleStream, defaultGraph, namedGraphMap, new HashMap<Var, Node>() );
		factory.fromUpdate(update, updateRootNode);
	}
	
	public static void fromUpdate(Update update, StreamRDF tripleStream, Node defaultGraph, Map<Node,Node> namedGraphMap) {
		fromUpdate(update, tripleStream, SWI.GraphRoot.asNode(), defaultGraph, namedGraphMap);
	}
	
	public static void fromUpdate(Update update, StreamRDF tripleStream, Node updateRootNode) {
		fromUpdate(update, tripleStream, updateRootNode, null, null);
	}
	
	public static void fromUpdate(Update update, StreamRDF tripleStream) {
		fromUpdate(update, tripleStream, SWI.GraphRoot.asNode());
	}
	
	public static void fromUpdateRequest(
			UpdateRequest updateRequest, Graph graph,
			Node requestRootNode, Node defaultGraph, Map<Node,Node> namedGraphMap) {
		List<Update> updates = updateRequest.getOperations();
        for (Update update : updates) {
        	Node updateRootNode = createNode();
        	graph.add(new Triple(requestRootNode, RDFS.member.asNode(), updateRootNode));
        	fromUpdate(update, graph, updateRootNode, defaultGraph, namedGraphMap);
        }
	}

	public static void fromUpdateRequest(
			UpdateRequest updateRequest, Graph graph,
			Node defaultGraph, Map<Node,Node> namedGraphMap) {
		fromUpdateRequest(updateRequest, graph, SWI.GraphRoot.asNode(), defaultGraph, namedGraphMap);
	}

	public static void fromUpdateRequest(UpdateRequest updateRequest, Graph graph, Node requestRootNode) {
		fromUpdateRequest(updateRequest, graph, requestRootNode, null, null);
	}

	public static void fromUpdateRequest(UpdateRequest updateRequest, Graph graph) {
		fromUpdateRequest(updateRequest, graph, SWI.GraphRoot.asNode());
	}

	public static void fromUpdateRequest(
			UpdateRequest updateRequest, StreamRDF tripleStream,
			Node requestRootNode, Node defaultGraph, Map<Node,Node> namedGraphMap) {
		List<Update> updates = updateRequest.getOperations();
        for (Update update : updates) {
        	Node updateRootNode = createNode();
        	tripleStream.triple(new Triple(requestRootNode, RDFS.member.asNode(), updateRootNode));
        	fromUpdate(update, tripleStream, updateRootNode, defaultGraph, namedGraphMap);
        }
	}

	public static void fromUpdateRequest(
			UpdateRequest updateRequest, StreamRDF tripleStream,
			Node defaultGraph, Map<Node,Node> namedGraphMap) {
		fromUpdateRequest(updateRequest, tripleStream, SWI.GraphRoot.asNode(), defaultGraph, namedGraphMap);
	}

	public static void fromUpdateRequest(UpdateRequest updateRequest, StreamRDF tripleStream, Node requestRootNode) {
		fromUpdateRequest(updateRequest, tripleStream, requestRootNode, null, null);
	}

	public static void fromUpdateRequest(UpdateRequest updateRequest, StreamRDF tripleStream) {
		fromUpdateRequest(updateRequest, tripleStream, SWI.GraphRoot.asNode());
	}

	public static void fromQuery(Query query, Graph graph, Node queryRootNode, Node defaultGraph, Map<Node,Node> namedGraphMap, Map<Var,Node> parentVarMap) {
		SpinxFactory factory = new SpinxFactory(query, new StreamRDFToGraph(graph), defaultGraph, namedGraphMap, parentVarMap);
		factory.fromQuery(queryRootNode);
	}

	public static void fromQuery(Query query, Graph graph, Node queryRootNode, Map<Var,Node> parentVarMap) {
		SpinxFactory factory = new SpinxFactory(query, new StreamRDFToGraph(graph), parentVarMap);
		factory.fromQuery(queryRootNode);
	}

	public static void fromQuery(Query query, Graph graph, Node queryRootNode, Node defaultGraph, Map<Node,Node> namedGraphMap) {
		fromQuery(query, graph, queryRootNode, defaultGraph, namedGraphMap, new HashMap<Var, Node>() );
	}

	public static void fromQuery(Query query, Graph graph, Node queryRootNode) {
		fromQuery(query, graph, queryRootNode, new HashMap<Var, Node>() );
	}

	public static void fromQuery(Query query, Graph graph) {
		fromQuery(query, graph, SWI.GraphRoot.asNode());
	}

	public static void fromQuery(Query query, StreamRDF tripleStream, Node queryRootNode, Node defaultGraph, Map<Node,Node> namedGraphMap, Map<Var,Node> parentVarMap) {
		SpinxFactory factory = new SpinxFactory(query, tripleStream, defaultGraph, namedGraphMap, parentVarMap);
		factory.fromQuery(queryRootNode);
	}

	public static void fromQuery(Query query, StreamRDF tripleStream, Node queryRootNode, Map<Var,Node> parentVarMap) {
		SpinxFactory factory = new SpinxFactory(query, tripleStream, parentVarMap);
		factory.fromQuery(queryRootNode);
	}

	public static void fromQuery(Query query, StreamRDF tripleStream, Node queryRootNode, Node defaultGraph, Map<Node,Node> namedGraphMap) {
		fromQuery(query, tripleStream, queryRootNode, defaultGraph, namedGraphMap, new HashMap<Var, Node>() );
	}

	public static void fromQuery(Query query, StreamRDF tripleStream, Node queryRootNode) {
		fromQuery(query, tripleStream, queryRootNode, new HashMap<Var, Node>() );
	}

	public static void fromQuery(Query query, StreamRDF tripleStream) {
		fromQuery(query, tripleStream, SWI.GraphRoot.asNode());
	}

	public static Graph fromQuery(Query query, Node queryRootNode) {
		Graph graph = GraphFactory.createGraphMem();
		fromQuery(query, graph, queryRootNode);
		return graph;
	}

	public static Graph fromQuery(Query query) {
		Graph graph = GraphFactory.createGraphMem();
		fromQuery(query, graph);
		return graph;
	}

	public static Graph fromUpdate(Update update) {
		Graph graph = GraphFactory.createGraphMem();
		fromUpdate(update, graph);
		return graph;
	}

	public static Graph fromUpdateRequest(UpdateRequest updateRequest) {
		Graph graph = GraphFactory.createGraphMem();
		fromUpdateRequest(updateRequest, graph);
		return graph;
	}
	
	private static class StreamRDFToGraph extends StreamRDFBase {
		private Graph graph;
		private StreamRDFToGraph(Graph graph) {
			this.graph = graph;
		}
		@Override
		public void triple(Triple triple) {
			graph.add(triple);
		}
	}

}

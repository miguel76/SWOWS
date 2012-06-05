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
package org.swows.spinx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.swows.vocabulary.SP;
import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprAggregator;
import com.hp.hpl.jena.sparql.expr.ExprFunction;
import com.hp.hpl.jena.sparql.expr.ExprFunctionOp;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.FunctionLabel;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.expr.aggregate.Aggregator;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.sparql.path.P_Alt;
import com.hp.hpl.jena.sparql.path.P_FixedLength;
import com.hp.hpl.jena.sparql.path.P_Inverse;
import com.hp.hpl.jena.sparql.path.P_Link;
import com.hp.hpl.jena.sparql.path.P_Mod;
import com.hp.hpl.jena.sparql.path.P_OneOrMore;
import com.hp.hpl.jena.sparql.path.P_Path0;
import com.hp.hpl.jena.sparql.path.P_Path1;
import com.hp.hpl.jena.sparql.path.P_Path2;
import com.hp.hpl.jena.sparql.path.P_ReverseLink;
import com.hp.hpl.jena.sparql.path.P_Seq;
import com.hp.hpl.jena.sparql.path.P_ZeroOrMore;
import com.hp.hpl.jena.sparql.path.P_ZeroOrOne;
import com.hp.hpl.jena.sparql.path.Path;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementAssign;
import com.hp.hpl.jena.sparql.syntax.ElementBind;
import com.hp.hpl.jena.sparql.syntax.ElementExists;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementMinus;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementNotExists;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementService;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.Template;
import com.hp.hpl.jena.vocabulary.RDF;

public class OldSpinxFactory {
	
	private static Node fromNode(Node node, Graph graph) {
		if (node.isVariable())
			return fromVar(((Var) node), graph);
		return node;
	}

	private static Map< Graph, Map<Var,Node> > varMap = new HashMap<Graph, Map<Var,Node>>();

	private static Node fromVar(Var var, Graph graph) {
		Map<Var,Node> graphVarMap = varMap.get(graph);
		if (graphVarMap == null) {
			graphVarMap = new HashMap<Var, Node>();
			varMap.put(graph, graphVarMap);
		}
		Node varNode = graphVarMap.get(var);
		if (varNode == null) {
			varNode = Node.createAnon();
			Node varNameNode = Node.createLiteral(var.getVarName());
			graph.add(new Triple(varNode, SP.varName.asNode(), varNameNode));
			graph.add(new Triple(varNode, RDF.type.asNode(), SP.Variable.asNode()));
			graphVarMap.put(var, varNode);
		}
		return varNode;
	}

	private static Node fromAggregator(Aggregator aggregator, Graph graph) {
		Node aggrNode = Node.createAnon();
		graph.add(new Triple( aggrNode, RDF.type.asNode(), SP.Expression.asNode() ));
		graph.add(new Triple(aggrNode, RDF.type.asNode(), SP.Aggregation.asNode()));
		graph.add(new Triple(aggrNode, RDF.type.asNode(), AggregatorSymbols.getUriNode(aggregator)));
		Expr expression = aggregator.getExpr();
		if (expression != null)
			graph.add(new Triple(aggrNode, SP.expression.asNode(), fromExpr(expression, graph)));
		return aggrNode;
	}
	
	private static Node fromExpr(Expr expr, Graph graph) {
//		Node exprRootNode = Node.createAnon();
//		graph.add(new Triple( exprRootNode, RDF.type.asNode(), SP.Expression.asNode() ));
		if (expr instanceof ExprFunction) {
			ExprFunction functionExpr = (ExprFunction) expr;
//			String functionIRI = ((ExprFunction) expr).getFunctionIRI();
			Node exprRootNode = Node.createAnon();
			graph.add(new Triple( exprRootNode, RDF.type.asNode(), SP.Expression.asNode() ));
			graph.add(new Triple( exprRootNode, RDF.type.asNode(), SPINX.FunctionCall.asNode() ));
			String functionIRI = functionExpr.getFunctionIRI();
			if (functionIRI != null) {
				Node functionIRINode = Node.createURI( functionIRI );
				graph.add(new Triple( exprRootNode, SPINX.functionIRI.asNode(), functionIRINode ));
			}
			String opName = functionExpr.getOpName();
			if (opName != null) {
				Node opNameNode = Node.createLiteral( opName );
				graph.add(new Triple( exprRootNode, SPINX.opName.asNode(), opNameNode ));
			}
			FunctionLabel functionLabel = functionExpr.getFunctionSymbol();
			if (functionLabel != null) {
				String functionSymbol = functionLabel.getSymbol();
				if (functionSymbol != null) {
					Node functionSymbolNode = Node.createLiteral( functionSymbol );
					graph.add(new Triple( exprRootNode, SPINX.functionLabel.asNode(), functionSymbolNode ));
					KnownFunctionsMapping.set(functionSymbol, functionExpr.getClass());
				}
			}
			Iterator<Expr> args = functionExpr.getArgs().iterator();
			int argCount = 0;
			while (args.hasNext()) {
				Node argPredNode = Node.createURI( SP.getURI() + "arg" + ++argCount );
				Node argNode = fromExpr(args.next(), graph);
				graph.add(new Triple( exprRootNode, argPredNode, argNode ));
			}
			if (expr instanceof ExprFunctionOp) {
				graph.add(new Triple( exprRootNode, RDF.type.asNode(), SPINX.OpCall.asNode() ));
				Element subElem = ((ExprFunctionOp) expr).getElement();
				graph.add(new Triple( exprRootNode, SPINX.element.asNode(), fromElement(subElem, graph) ));
			}
			return exprRootNode;
		} else if (expr instanceof ExprVar) {
			return fromVar( ((ExprVar) expr).asVar(), graph );
		} else if (expr instanceof NodeValue) {
			return fromNode( ((NodeValue) expr).asNode(), graph );
		} else if (expr instanceof ExprAggregator) {
			return fromAggregator( ((ExprAggregator) expr).getAggregator(), graph );
		} else
			return SPINX.UnknownExpr.asNode();
//		return exprRootNode;
	}

	private static Node fromPath(Path path, Graph graph) {
		if (path instanceof P_Path0) {
			Node linkNode = fromNode( ((P_Path0) path).getNode(), graph );
			if (path instanceof P_Link)
				return linkNode;
			else if (path instanceof P_ReverseLink) {
				Node pathRootNode = Node.createAnon();
				graph.add(new Triple(pathRootNode, RDF.type.asNode(), SP.Path.asNode()));
				graph.add(new Triple(pathRootNode, RDF.type.asNode(), SP.ReversePath.asNode()));
				graph.add(new Triple(pathRootNode, SP.subPath.asNode(), linkNode));
				return pathRootNode;
			} else {
				// TODO: exception for unrecognized Path
				return null;
			}
		} else {
			Node pathRootNode = Node.createAnon();
			graph.add(new Triple(pathRootNode, RDF.type.asNode(), SP.Path.asNode()));
			if (path instanceof P_Path1) {
				Node subPathNode = fromPath( ((P_Path1) path).getSubPath(), graph );
				graph.add(new Triple(pathRootNode, SP.subPath.asNode(), subPathNode));
				if (path instanceof P_Inverse) {
					graph.add(new Triple(pathRootNode, RDF.type.asNode(), SP.ReversePath.asNode()));
				} else {
					long min = 0;
					long max = -1; // -1 as infinity (unlimited)
					if (path instanceof P_FixedLength) {
						min = max = ((P_FixedLength) path).getCount();
					} else if (path instanceof P_Mod) {
						min = ((P_Mod) path).getMin();
						max = ((P_Mod) path).getMax();
					} else if (path instanceof P_OneOrMore) {
						min = 1;
					} else if (path instanceof P_ZeroOrMore) {
					} else if (path instanceof P_ZeroOrOne) {
						max = 1;
					} else {
						// TODO: exception for unrecognized Path
					}
					graph.add(new Triple(pathRootNode, RDF.type.asNode(), SP.ModPath.asNode()));
					if (min != 0) {
						Node minNode = Node.createLiteral(Long.toString(min), XSDDatatype.XSDinteger);
						graph.add(new Triple(pathRootNode, SP.modMin.asNode(), minNode));
					}
					if (max != 0) {
						Node maxNode = Node.createLiteral(Long.toString(max), XSDDatatype.XSDinteger);
						graph.add(new Triple(pathRootNode, SP.modMax.asNode(), maxNode));
					}
				}
			} else if (path instanceof P_Path2) {
				Node path1Node = fromPath( ((P_Path2) path).getLeft(), graph );
				Node path2Node = fromPath( ((P_Path2) path).getRight(), graph );
				graph.add(new Triple(pathRootNode, SP.path1.asNode(), path1Node));
				graph.add(new Triple(pathRootNode, SP.path2.asNode(), path2Node));
				if (path instanceof P_Alt)
					graph.add(new Triple(pathRootNode, RDF.type.asNode(), SP.AltPath.asNode()));
				else if (path instanceof P_Seq)
					graph.add(new Triple(pathRootNode, RDF.type.asNode(), SP.SeqPath.asNode()));
				else {
				// TODO: exception for unrecognized Path
				}
			}
			return pathRootNode;
		}
	}
	
	private static Node fromTriple(Triple triple, Graph graph) {
		Node elementRootNode = Node.createAnon();
		graph.add(new Triple(
				elementRootNode,
				RDF.type.asNode(),
				Node.createURI(SP.getURI() + "Element")));
		graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.TriplePattern.asNode()));
		Node subjectNode = fromNode(triple.getSubject(), graph);
		Node predicateNode = fromNode(triple.getPredicate(), graph);
		Node objectNode = fromNode(triple.getObject(), graph);
		graph.add(new Triple( elementRootNode, SP.subject.asNode(), subjectNode));
		graph.add(new Triple( elementRootNode, SP.predicate.asNode(), predicateNode));
		graph.add(new Triple( elementRootNode, SP.object.asNode(), objectNode));
		return elementRootNode;
	}

	private static Node fromTriplePath(TriplePath triplePath, Graph graph) {
		if (triplePath.isTriple())
			return fromTriple(triplePath.asTriple(), graph);
		Node elementRootNode = Node.createAnon();
		graph.add(new Triple(
				elementRootNode,
				RDF.type.asNode(),
				Node.createURI(SP.getURI() + "Element")));
		Node subjectNode = fromNode(triplePath.getSubject(), graph);
		Node pathNode = fromPath(triplePath.getPath(), graph);
		Node objectNode = fromNode(triplePath.getObject(), graph);
		graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.TriplePath.asNode()));
		graph.add(new Triple( elementRootNode, SP.subject.asNode(), subjectNode));
		graph.add(new Triple( elementRootNode, SP.path.asNode(), pathNode));
		graph.add(new Triple( elementRootNode, SP.object.asNode(), objectNode));
		
		return elementRootNode;
	}

	private static Node fromElement(Element element, Graph graph) {
		Node elementRootNode = Node.createAnon();
		graph.add(new Triple(
				elementRootNode,
				RDF.type.asNode(),
				Node.createURI(SP.getURI() + "Element")));
		List<Element> childList = null;
		if (element instanceof ElementExists) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SPINX.Exists.asNode()));
			Node childElement = fromElement( ((ElementExists) element).getElement(), graph);
			graph.add(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementNotExists) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SPINX.NotExists.asNode()));
			Node childElement = fromElement( ((ElementNotExists) element).getElement(), graph );
			graph.add(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementAssign) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SPINX.Assign.asNode()));
			Node var = fromVar( ((ElementAssign) element).getVar(), graph );
			Node expr = fromExpr( ((ElementAssign) element).getExpr(), graph );
			graph.add(new Triple( elementRootNode, SPINX.var.asNode(), var ));
			graph.add(new Triple( elementRootNode, SPINX.expr.asNode(), expr ));
		} else if (element instanceof ElementBind) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.Bind.asNode()));
			Node var = fromVar( ((ElementBind) element).getVar(), graph );
			Node expr = fromExpr( ((ElementBind) element).getExpr(), graph );
			graph.add(new Triple( elementRootNode, SPINX.var.asNode(), var ));
			graph.add(new Triple( elementRootNode, SPINX.expr.asNode(), expr ));
		} else if (element instanceof ElementFilter) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.Filter.asNode()));
			Node expr = fromExpr( ((ElementFilter) element).getExpr(), graph );
			graph.add(new Triple( elementRootNode, SPINX.expr.asNode(), expr ));
		} else if (element instanceof ElementGroup) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SPINX.ElementGroup.asNode()));
			childList = ((ElementGroup) element).getElements();
		} else if (element instanceof ElementMinus) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.Minus.asNode()));
			Node childElement = fromElement( ((ElementMinus) element).getMinusElement(), graph );
			graph.add(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementNamedGraph) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.NamedGraph.asNode()));
			Node graphNameNode = fromNode( ((ElementNamedGraph) element).getGraphNameNode(), graph );
			graph.add(new Triple( elementRootNode, SP.graphNameNode.asNode(), graphNameNode ));
			Node childElement = fromElement( ((ElementNamedGraph) element).getElement(), graph );
			graph.add(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementOptional) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.Optional.asNode()));
			Node childElement = fromElement( ((ElementOptional) element).getOptionalElement(), graph );
			graph.add(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementPathBlock) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SPINX.ElementGroup.asNode()));
			Iterator<TriplePath> triplePaths = ((ElementPathBlock) element).patternElts();
			while (triplePaths.hasNext()) {
				Node childNode = fromTriplePath( triplePaths.next(), graph);;
				graph.add(new Triple(elementRootNode, SPINX.element.asNode(), childNode));
			}
		} else if (element instanceof ElementTriplesBlock) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SPINX.ElementGroup.asNode()));
			Iterator<Triple> triples = ((ElementTriplesBlock) element).patternElts();
			while (triples.hasNext()) {
				Node childNode = fromTriple( triples.next(), graph);;
				graph.add(new Triple(elementRootNode, SPINX.element.asNode(), childNode));
			}
		} else if (element instanceof ElementService) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.Service.asNode()));
			Node serviceNode = fromNode( ((ElementService) element).getServiceNode(), graph );
			graph.add(new Triple( elementRootNode, SP.serviceURI.asNode(), serviceNode ));
			Node childElement = fromElement( ((ElementService) element).getElement(), graph );
			graph.add(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementSubQuery) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.SubQuery.asNode()));
			Query subQuery = ((ElementSubQuery) element).getQuery();
			Node subQueryNode = Node.createAnon();
			graph.add(new Triple( elementRootNode, SP.query.asNode(), subQueryNode ));
			fromQuery( subQuery, graph, subQueryNode );
		} else if (element instanceof ElementUnion) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.Union.asNode()));
			childList = ((ElementUnion) element).getElements();
		} else {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SPINX.EmptyElement.asNode()));
		}
		if ( childList != null ) {
			Iterator<Element> elems = childList.iterator();
			while (elems.hasNext()) {
				Node childNode = fromElement( elems.next(), graph);;
				graph.add(new Triple(elementRootNode, SPINX.element.asNode(), childNode));
			}
		}
		return elementRootNode;
	}
	
	private static Node fromTemplateTriple(Triple triple, Graph graph) {
		Node tripleRootNode = Node.createAnon();
		Node subjNode = fromNode(triple.getSubject(), graph);
		graph.add(new Triple(tripleRootNode, SP.subject.asNode(), subjNode));
		Node predNode = fromNode(triple.getPredicate(), graph);
		graph.add(new Triple(tripleRootNode, SP.predicate.asNode(), predNode));
		Node objNode = fromNode(triple.getObject(), graph);
		graph.add(new Triple(tripleRootNode, SP.object.asNode(), objNode));
		return tripleRootNode;
	}	

	private static Node fromTemplate(Template template, Graph graph) {
		Iterator<Triple> templateTriples = template.getTriples().iterator();
		Node templateRootNode = Node.createAnon();
		while (templateTriples.hasNext()) {
			Node tripleNode = fromTemplateTriple(templateTriples.next(), graph);
			graph.add(new Triple(templateRootNode, SPINX.triple.asNode(), tripleNode));
		}
		return templateRootNode;
	}	

	private static void fromAsk(Query query, Graph graph, Node queryRootNode) {
		graph.add(new Triple(queryRootNode, RDF.type.asNode(), SP.Ask.asNode()));
	}

	private static void fromConstruct(Query query, Graph graph, Node queryRootNode) {
		graph.add(new Triple(queryRootNode, RDF.type.asNode(), SP.Construct.asNode()));
		graph.add( new Triple(
				queryRootNode,
				SP.templates.asNode(),
				fromTemplate(query.getConstructTemplate(), graph) ) );
	}

	private static void fromDescribe(Query query, Graph graph, Node queryRootNode) {
		graph.add(new Triple(queryRootNode, RDF.type.asNode(), SP.Describe.asNode()));
	}

	private static void fromSelect(Query query, Graph graph, Node queryRootNode) {
		graph.add(new Triple(queryRootNode, RDF.type.asNode(), SP.Select.asNode()));
//		List<String> varList = query.getResultVars();
//		if (varList != null) {
//			for (String var : varList) {
//				Node resultNode = Node.createAnon();
//				graph.add(new Triple(queryRootNode, SPINX.resultVariable.asNode(), resultNode));
//				Node varNode = fromVar(var, graph);
//				graph.add(new Triple(queryRootNode, SP.as.asNode(), varNode));
//				graph.add(new Triple(queryRootNode, SP.expression.asNode(), varNode));
//			}
//		}
		VarExprList projectedList = query.getProject();
		for ( Var projectedVar : projectedList.getVars() ) {
			Node projectedNode = Node.createAnon();
			graph.add(new Triple(queryRootNode, SPINX.resultVariable.asNode(), projectedNode));
			graph.add(new Triple(projectedNode, SP.as.asNode(), fromVar(projectedVar, graph)));
			Expr projectedExpr = projectedList.getExpr(projectedVar);
			if (projectedExpr != null)
				graph.add(new Triple(projectedNode, SP.expression.asNode(), fromExpr(projectedExpr, graph)));
		}
		if (query.isDistinct())
			graph.add(new Triple(queryRootNode, SP.distinct.asNode(), Node.createLiteral("true", XSDDatatype.XSDboolean)));

		VarExprList groupByExprList = query.getGroupBy();
		if (groupByExprList != null) {
			for (Var groupByVar : groupByExprList.getVars() ) {
				Node groupByNode = Node.createAnon();
				graph.add(new Triple(queryRootNode, SP.groupBy.asNode(), groupByNode));
				graph.add(new Triple(groupByNode, SP.as.asNode(), fromVar(groupByVar, graph)));
				Expr groupByExpr = groupByExprList.getExpr(groupByVar);
				if (groupByExpr != null)
					graph.add(new Triple(groupByNode, SP.expression.asNode(), fromExpr(groupByExpr, graph)));
			}
		}
	}

	public static void fromQuery(Query query, Graph graph, Node queryRootNode) {
		graph.add(new Triple(queryRootNode, RDF.type.asNode(), SP.Query.asNode()));
		/*
		Node whereNode = Node.createAnon();
		graph.add(new Triple(queryRootNode, SP.where.asNode(), whereNode));
		fromElement(query.getQueryPattern(), graph, whereNode);
		*/
		graph.add( new Triple(
				queryRootNode,
				SP.where.asNode(),
				fromElement(query.getQueryPattern(), graph) ) );
		if (query.isAskType())
			fromAsk(query, graph, queryRootNode);
		else if (query.isConstructType())
			fromConstruct(query, graph, queryRootNode);
		else if (query.isDescribeType())
			fromDescribe(query, graph, queryRootNode);
		else if (query.isSelectType())
			fromSelect(query, graph, queryRootNode);
		// if unknown?
	}

	public static Graph fromQuery(Query query) {
		Graph graph = GraphFactory.createGraphMem();
		Node queryRootNode = Node.createURI("#defaultQuery");
		fromQuery(query, graph, queryRootNode);
		return graph;
	}

}

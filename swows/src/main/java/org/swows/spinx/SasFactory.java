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
import org.swows.node.Skolemizer;
import org.swows.vocabulary.SP;
import org.swows.vocabulary.SPINX;
import org.swows.vocabulary.SWI;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.Quad;
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
import com.hp.hpl.jena.sparql.expr.aggregate.Accumulator;
import com.hp.hpl.jena.sparql.expr.aggregate.AggGroupConcat;
import com.hp.hpl.jena.sparql.expr.aggregate.AggGroupConcatDistinct;
import com.hp.hpl.jena.sparql.expr.aggregate.Aggregator;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueString;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.sparql.modify.request.UpdateAdd;
import com.hp.hpl.jena.sparql.modify.request.UpdateClear;
import com.hp.hpl.jena.sparql.modify.request.UpdateCopy;
import com.hp.hpl.jena.sparql.modify.request.UpdateCreate;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataDelete;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataInsert;
import com.hp.hpl.jena.sparql.modify.request.UpdateDeleteWhere;
import com.hp.hpl.jena.sparql.modify.request.UpdateDrop;
import com.hp.hpl.jena.sparql.modify.request.UpdateLoad;
import com.hp.hpl.jena.sparql.modify.request.UpdateModify;
import com.hp.hpl.jena.sparql.modify.request.UpdateMove;
import com.hp.hpl.jena.sparql.modify.request.UpdateVisitor;
import com.hp.hpl.jena.sparql.path.P_Alt;
import com.hp.hpl.jena.sparql.path.P_FixedLength;
import com.hp.hpl.jena.sparql.path.P_Inverse;
import com.hp.hpl.jena.sparql.path.P_Link;
import com.hp.hpl.jena.sparql.path.P_Mod;
import com.hp.hpl.jena.sparql.path.P_OneOrMore1;
import com.hp.hpl.jena.sparql.path.P_OneOrMoreN;
import com.hp.hpl.jena.sparql.path.P_Path0;
import com.hp.hpl.jena.sparql.path.P_Path1;
import com.hp.hpl.jena.sparql.path.P_Path2;
import com.hp.hpl.jena.sparql.path.P_ReverseLink;
import com.hp.hpl.jena.sparql.path.P_Seq;
import com.hp.hpl.jena.sparql.path.P_ZeroOrMore1;
import com.hp.hpl.jena.sparql.path.P_ZeroOrMoreN;
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
import com.hp.hpl.jena.update.Update;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SasFactory {
	
	private Graph graph;
	
	private Query query;
//	private Update update;
	
	private Map<Var,Node> varMap = new HashMap<Var,Node>();
	private Map<Var,Node> parentVarMap = null;
	
	private static Node createNode() {
		return Skolemizer.getInstance().getNode();
	}

	public SasFactory(Query query, Graph graph, Map<Var,Node> parentVarMap) {
		this.query = query;
		this.graph = graph;
		this.parentVarMap = parentVarMap;
	}

//	public SpinxFactory(Update update, Graph graph, Map<Var,Node> parentVarMap) {
//		this.update = update;
//		this.graph = graph;
//		this.parentVarMap = parentVarMap;
//	}

	public SasFactory(Graph graph, Map<Var,Node> parentVarMap) {
		this.graph = graph;
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
			graph.add(new Triple(varNode, SP.varName.asNode(), varNameNode));
			graph.add(new Triple(varNode, RDF.type.asNode(), SP.Variable.asNode()));
			varMap.put(var, varNode);
		}
		return varNode;
	}

	private Node fromParentVar(Var var) {
		Node varNode = parentVarMap.get(var);
		if (varNode == null) {
			varNode = createNode();
			Node varNameNode = NodeFactory.createLiteral(var.getVarName());
			graph.add(new Triple(varNode, SP.varName.asNode(), varNameNode));
			graph.add(new Triple(varNode, RDF.type.asNode(), SP.Variable.asNode()));
			parentVarMap.put(var, varNode);
		}
		return varNode;
	}

	private Node fromAggregator(Aggregator aggregator) {
		Node aggrNode = createNode();
		graph.add(new Triple( aggrNode, RDF.type.asNode(), SP.Expression.asNode() ));
		graph.add(new Triple(aggrNode, RDF.type.asNode(), SP.Aggregation.asNode()));
		graph.add(new Triple(aggrNode, RDF.type.asNode(), AggregatorSymbols.getUriNode(aggregator)));
		Expr expression = aggregator.getExpr();
		if (expression != null)
			graph.add(new Triple(aggrNode, SP.expression.asNode(), fromExpr(expression)));
		if (aggregator instanceof AggGroupConcat || aggregator instanceof AggGroupConcatDistinct) {
			Accumulator accumulator = aggregator.copy(new NodeValueString("")).createAccumulator();
			accumulator.accumulate(null, null);
			accumulator.accumulate(null, null);
			String separator = accumulator.getValue().asString();
			Node scalarvalNode = createNode();
			graph.add(new Triple( aggrNode, SPINX.scalarval.asNode(), scalarvalNode ));
			graph.add(new Triple( scalarvalNode, SPINX.key.asNode(), NodeFactory.createLiteral("separator") ));
			graph.add(new Triple( scalarvalNode, SPINX.value.asNode(), NodeFactory.createLiteral(separator) ));
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
			graph.add(new Triple( exprRootNode, RDF.type.asNode(), SP.Expression.asNode() ));
			graph.add(new Triple( exprRootNode, RDF.type.asNode(), SPINX.FunctionCall.asNode() ));
			String functionIRI = functionExpr.getFunctionIRI();
			if (functionIRI != null) {
				Node functionIRINode = NodeFactory.createURI( functionIRI );
				graph.add(new Triple( exprRootNode, SPINX.functionIRI.asNode(), functionIRINode ));
			}
			String opName = functionExpr.getOpName();
			if (opName != null) {
				Node opNameNode = NodeFactory.createLiteral( opName );
				graph.add(new Triple( exprRootNode, SPINX.opName.asNode(), opNameNode ));
			}
			FunctionLabel functionLabel = functionExpr.getFunctionSymbol();
			if (functionLabel != null) {
				String functionSymbol = functionLabel.getSymbol();
				if (functionSymbol != null) {
					Node functionSymbolNode = NodeFactory.createLiteral( functionSymbol );
					graph.add(new Triple( exprRootNode, SPINX.functionLabel.asNode(), functionSymbolNode ));
					KnownFunctionsMapping.set(functionSymbol, functionExpr.getClass());
				}
			}
			Iterator<Expr> args = functionExpr.getArgs().iterator();
			int argCount = 0;
			while (args.hasNext()) {
				Node argPredNode = NodeFactory.createURI( SP.getURI() + "arg" + ++argCount );
				Node argNode = fromExpr(args.next());
				graph.add(new Triple( exprRootNode, argPredNode, argNode ));
			}
			if (expr instanceof ExprFunctionOp) {
				graph.add(new Triple( exprRootNode, RDF.type.asNode(), SPINX.OpCall.asNode() ));
				Element subElem = ((ExprFunctionOp) expr).getElement();
				graph.add(new Triple( exprRootNode, SPINX.element.asNode(), fromElement(subElem) ));
			}
			return exprRootNode;
		} else if (expr instanceof ExprVar) {
			return fromVar( ((ExprVar) expr).asVar() );
		} else if (expr instanceof NodeValue) {
			return fromNode( ((NodeValue) expr).asNode() );
		} else if (expr instanceof ExprAggregator) {
			return fromAggregator( ((ExprAggregator) expr).getAggregator() );
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
				graph.add(new Triple(pathRootNode, RDF.type.asNode(), SP.Path.asNode()));
				graph.add(new Triple(pathRootNode, RDF.type.asNode(), SP.ReversePath.asNode()));
				graph.add(new Triple(pathRootNode, SP.subPath.asNode(), linkNode));
				return pathRootNode;
			} else {
				// TODO: exception for unrecognized Path
				return null;
			}
		} else {
			Node pathRootNode = createNode();
			graph.add(new Triple(pathRootNode, RDF.type.asNode(), SP.Path.asNode()));
			if (path instanceof P_Path1) {
				Node subPathNode = fromPath( ((P_Path1) path).getSubPath() );
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
					} else if (path instanceof P_OneOrMore1 || path instanceof P_OneOrMoreN) {
						min = 1;
					} else if (path instanceof P_ZeroOrMore1 || path instanceof P_ZeroOrMoreN) {
					} else if (path instanceof P_ZeroOrOne) {
						max = 1;
					} else {
						// TODO: exception for unrecognized Path
					}
					graph.add(new Triple(pathRootNode, RDF.type.asNode(), SP.ModPath.asNode()));
					if (min != 0) {
						Node minNode = NodeFactory.createLiteral(Long.toString(min), XSDDatatype.XSDinteger);
						graph.add(new Triple(pathRootNode, SP.modMin.asNode(), minNode));
					}
					if (max != 0) {
						Node maxNode = NodeFactory.createLiteral(Long.toString(max), XSDDatatype.XSDinteger);
						graph.add(new Triple(pathRootNode, SP.modMax.asNode(), maxNode));
					}
				}
			} else if (path instanceof P_Path2) {
				Node path1Node = fromPath( ((P_Path2) path).getLeft() );
				Node path2Node = fromPath( ((P_Path2) path).getRight() );
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
	
	private Node fromTriple(Triple triple) {
		Node elementRootNode = createNode();
		graph.add(new Triple(
				elementRootNode,
				RDF.type.asNode(),
				NodeFactory.createURI(SP.getURI() + "Element")));
		graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.TriplePattern.asNode()));
		Node subjectNode = fromNode(triple.getSubject());
		Node predicateNode = fromNode(triple.getPredicate());
		Node objectNode = fromNode(triple.getObject());
		graph.add(new Triple( elementRootNode, SP.subject.asNode(), subjectNode));
		graph.add(new Triple( elementRootNode, SP.predicate.asNode(), predicateNode));
		graph.add(new Triple( elementRootNode, SP.object.asNode(), objectNode));
		return elementRootNode;
	}

	private Node fromTriplePath(TriplePath triplePath) {
		if (triplePath.isTriple())
			return fromTriple(triplePath.asTriple());
		Node elementRootNode = createNode();
		graph.add(new Triple(
				elementRootNode,
				RDF.type.asNode(),
				NodeFactory.createURI(SP.getURI() + "Element")));
		Node subjectNode = fromNode(triplePath.getSubject());
		Node pathNode = fromPath(triplePath.getPath());
		Node objectNode = fromNode(triplePath.getObject());
		graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.TriplePath.asNode()));
		graph.add(new Triple( elementRootNode, SP.subject.asNode(), subjectNode));
		graph.add(new Triple( elementRootNode, SP.path.asNode(), pathNode));
		graph.add(new Triple( elementRootNode, SP.object.asNode(), objectNode));
		
		return elementRootNode;
	}

	private Node fromElement(Element element) {
		Node elementRootNode = createNode();
		graph.add(new Triple(
				elementRootNode,
				RDF.type.asNode(),
				NodeFactory.createURI(SP.getURI() + "Element")));
		List<Element> childList = null;
		if (element instanceof ElementExists) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SPINX.Exists.asNode()));
			Node childElement = fromElement( ((ElementExists) element).getElement() );
			graph.add(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementNotExists) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SPINX.NotExists.asNode()));
			Node childElement = fromElement( ((ElementNotExists) element).getElement() );
			graph.add(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementAssign) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SPINX.Assign.asNode()));
			Node var = fromVar( ((ElementAssign) element).getVar() );
			Node expr = fromExpr( ((ElementAssign) element).getExpr() );
			graph.add(new Triple( elementRootNode, SPINX.var.asNode(), var ));
			graph.add(new Triple( elementRootNode, SPINX.expr.asNode(), expr ));
		} else if (element instanceof ElementBind) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.Bind.asNode()));
			Node var = fromVar( ((ElementBind) element).getVar() );
			Node expr = fromExpr( ((ElementBind) element).getExpr() );
			graph.add(new Triple( elementRootNode, SPINX.var.asNode(), var ));
			graph.add(new Triple( elementRootNode, SPINX.expr.asNode(), expr ));
		} else if (element instanceof ElementFilter) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.Filter.asNode()));
			Node expr = fromExpr( ((ElementFilter) element).getExpr() );
			graph.add(new Triple( elementRootNode, SPINX.expr.asNode(), expr ));
		} else if (element instanceof ElementGroup) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SPINX.ElementGroup.asNode()));
			childList = ((ElementGroup) element).getElements();
		} else if (element instanceof ElementMinus) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.Minus.asNode()));
			Node childElement = fromElement( ((ElementMinus) element).getMinusElement() );
			graph.add(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementNamedGraph) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.NamedGraph.asNode()));
			Node graphNameNode = fromNode( ((ElementNamedGraph) element).getGraphNameNode() );
			graph.add(new Triple( elementRootNode, SP.graphNameNode.asNode(), graphNameNode ));
			Node childElement = fromElement( ((ElementNamedGraph) element).getElement() );
			graph.add(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementOptional) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.Optional.asNode()));
			Node childElement = fromElement( ((ElementOptional) element).getOptionalElement() );
			graph.add(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementPathBlock) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SPINX.ElementGroup.asNode()));
			Iterator<TriplePath> triplePaths = ((ElementPathBlock) element).patternElts();
			while (triplePaths.hasNext()) {
				Node childNode = fromTriplePath( triplePaths.next() );
				graph.add(new Triple(elementRootNode, SPINX.element.asNode(), childNode));
			}
		} else if (element instanceof ElementTriplesBlock) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SPINX.ElementGroup.asNode()));
			Iterator<Triple> triples = ((ElementTriplesBlock) element).patternElts();
			while (triples.hasNext()) {
				Node childNode = fromTriple( triples.next() );
				graph.add(new Triple(elementRootNode, SPINX.element.asNode(), childNode));
			}
		} else if (element instanceof ElementService) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.Service.asNode()));
			Node serviceNode = fromNode( ((ElementService) element).getServiceNode() );
			graph.add(new Triple( elementRootNode, SP.serviceURI.asNode(), serviceNode ));
			Node childElement = fromElement( ((ElementService) element).getElement() );
			graph.add(new Triple( elementRootNode, SPINX.element.asNode(), childElement ));
		} else if (element instanceof ElementSubQuery) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.SubQuery.asNode()));
			Query subQuery = ((ElementSubQuery) element).getQuery();
			Node subQueryNode = createNode();
			graph.add(new Triple( elementRootNode, SP.query.asNode(), subQueryNode ));
			fromQuery( subQuery, graph, subQueryNode, varMap );
		} else if (element instanceof ElementUnion) {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SP.Union.asNode()));
			childList = ((ElementUnion) element).getElements();
		} else {
			graph.add(new Triple( elementRootNode, RDF.type.asNode(), SPINX.EmptyElement.asNode()));
		}
		if ( childList != null ) {
			Iterator<Element> elems = childList.iterator();
			while (elems.hasNext()) {
				Node childNode = fromElement( elems.next());
				graph.add(new Triple(elementRootNode, SPINX.element.asNode(), childNode));
			}
		}
		return elementRootNode;
	}
	
	private Node fromTemplateTriple(Triple triple) {
		Node tripleRootNode = createNode();
		Node subjNode = fromNode(triple.getSubject());
		graph.add(new Triple(tripleRootNode, SP.subject.asNode(), subjNode));
		Node predNode = fromNode(triple.getPredicate());
		graph.add(new Triple(tripleRootNode, SP.predicate.asNode(), predNode));
		Node objNode = fromNode(triple.getObject());
		graph.add(new Triple(tripleRootNode, SP.object.asNode(), objNode));
		return tripleRootNode;
	}	

	private Node fromTemplate(Template template) {
		Iterator<Triple> templateTriples = template.getTriples().iterator();
		Node templateRootNode = createNode();
		while (templateTriples.hasNext()) {
			Node tripleNode = fromTemplateTriple(templateTriples.next());
			graph.add(new Triple(templateRootNode, SPINX.triple.asNode(), tripleNode));
		}
		return templateRootNode;
	}	

	private Node fromTriples(List<Triple> triples) {
		Node triplesRootNode = createNode();
		for (Triple triple : triples)
			graph.add(new Triple(triplesRootNode, SPINX.triple.asNode(), fromTemplateTriple(triple)));
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
			graph.add(new Triple(namedGraphNode, RDF.type.asNode(), SP.NamedGraph.asNode()));
			graph.add(new Triple(quadsRootNode, SP.named.asNode(), namedGraphNode));
		}
		return quadsRootNode;
	}	

	private void fromAsk(Node queryRootNode) {
		graph.add(new Triple(queryRootNode, RDF.type.asNode(), SP.Ask.asNode()));
	}

	private void fromConstruct(Node queryRootNode) {
		graph.add(new Triple(queryRootNode, RDF.type.asNode(), SP.Construct.asNode()));
		graph.add( new Triple(
				queryRootNode,
				SP.templates.asNode(),
				fromTemplate(query.getConstructTemplate()) ) );
	}

	private void fromDescribe(Node queryRootNode) {
		graph.add(new Triple(queryRootNode, RDF.type.asNode(), SP.Describe.asNode()));
	}

	private void fromSelect(Node queryRootNode) {
		graph.add(new Triple(queryRootNode, RDF.type.asNode(), SP.Select.asNode()));
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
			graph.add(new Triple(queryRootNode, SPINX.resultVariable.asNode(), projectedNode));
			graph.add(new Triple(projectedNode, SP.as.asNode(), fromParentVar(projectedVar)));
			Expr projectedExpr = projectedList.getExpr(projectedVar);
			if (projectedExpr != null)
				graph.add(new Triple(projectedNode, SP.expression.asNode(), fromExpr(projectedExpr)));
		}
		if (query.isDistinct())
			graph.add(new Triple(queryRootNode, SP.distinct.asNode(), NodeFactory.createLiteral("true", XSDDatatype.XSDboolean)));

		VarExprList groupByExprList = query.getGroupBy();
		if (groupByExprList != null) {
			for (Var groupByVar : groupByExprList.getVars() ) {
				Node groupByNode = createNode();
				graph.add(new Triple(queryRootNode, SP.groupBy.asNode(), groupByNode));
				graph.add(new Triple(groupByNode, SP.as.asNode(), fromParentVar(groupByVar)));
				Expr groupByExpr = groupByExprList.getExpr(groupByVar);
				if (groupByExpr != null)
					graph.add(new Triple(groupByNode, SP.expression.asNode(), fromExpr(groupByExpr)));
			}
		}
	}
	
	private void fromDeleteInsert(UpdateModify update, Node queryRootNode) {
		graph.add(new Triple(queryRootNode, RDF.type.asNode(), SP.Modify.asNode()));
		graph.add( new Triple(
				queryRootNode,
				SP.where.asNode(),
				fromElement(update.getWherePattern() ) ) );
//		Node deleteNode = GraphUtils.getSingleValueOptProperty(graph, queryRootNode, SP.deletePattern.asNode());
		List<Quad> deleteQuads = update.getDeleteQuads();
		if (deleteQuads != null && !deleteQuads.isEmpty()) {
			graph.add( new Triple(
					queryRootNode,
					SP.deletePattern.asNode(),
					fromQuads(deleteQuads) ) );
		}
		List<Quad> insertQuads = update.getInsertQuads();
		if (insertQuads != null && !insertQuads.isEmpty()) {
			graph.add( new Triple(
					queryRootNode,
					SP.insertPattern.asNode(),
					fromQuads(insertQuads) ) );
		}
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

	public void fromQuery(Node queryRootNode) {
		graph.add(new Triple(queryRootNode, RDF.type.asNode(), SP.Query.asNode()));
		graph.add( new Triple(
				queryRootNode,
				SP.where.asNode(),
				fromElement(query.getQueryPattern()) ) );
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
			
			@Override
			public void visit(UpdateModify update) {
				fromDeleteInsert(update, queryRootNode);
			}
			
			@Override
			public void visit(UpdateDeleteWhere update) {
//				fromDeleteWhere(update, queryRootNode);
			}
			
			@Override
			public void visit(UpdateDataDelete update) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(UpdateDataInsert update) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(UpdateMove update) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(UpdateCopy update) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(UpdateAdd update) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(UpdateLoad update) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(UpdateCreate update) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(UpdateClear update) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(UpdateDrop update) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Sink<Quad> createDeleteDataSink() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Sink<Quad> createInsertDataSink() {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	public static void fromUpdate(Update update, Graph graph, Node updateRootNode) {
		SasFactory factory = new SasFactory(graph, new HashMap<Var, Node>() );
		factory.fromUpdate(update, updateRootNode);
	}
	
	public static void fromUpdate(Update update, Graph graph) {
		fromUpdate(update, graph, SWI.GraphRoot.asNode());
	}
	
	public static void fromUpdateRequest(UpdateRequest updateRequest, Graph graph, Node requestRootNode) {
		List<Update> updates = updateRequest.getOperations();
        for (Update update : updates) {
        	Node updateRootNode = createNode();
        	graph.add(new Triple(requestRootNode, RDFS.member.asNode(), updateRootNode));
        	fromUpdate(update, graph, updateRootNode);
        }
	}

	public static void fromUpdateRequest(UpdateRequest updateRequest, Graph graph) {
		fromUpdateRequest(updateRequest, graph, SWI.GraphRoot.asNode());
	}

	public static void fromQuery(Query query, Graph graph, Node queryRootNode, Map<Var,Node> parentVarMap) {
		SasFactory factory = new SasFactory(query, graph, parentVarMap);
		factory.fromQuery(queryRootNode);
	}

	public static void fromQuery(Query query, Graph graph, Node queryRootNode) {
		fromQuery(query, graph, queryRootNode, new HashMap<Var, Node>() );
	}

	public static void fromQuery(Query query, Graph graph) {
		fromQuery(query, graph, SWI.GraphRoot.asNode());
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

}

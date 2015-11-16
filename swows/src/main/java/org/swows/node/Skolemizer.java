package org.swows.node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphUtil;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionEnv;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.util.iterator.Map1;
import org.swows.vocabulary.SWI;

public class Skolemizer {

	private static Skolemizer singleton = null;
	
	public static synchronized Skolemizer getInstance() {
		if (singleton == null)
			singleton = new Skolemizer();
		return singleton;
	}
	
	private long count = 0;

//	private Map<PhantomReference<Node>, Map<List<Node>, Node>> nodesMap = null;
//	private Map<Node, Map<List<Node>, Node>> nodesMap =
//			new WeakHashMap<Node, Map<List<Node>, Node>>();
//	private Map<Node, Node> singleNodesMap =
//			new WeakHashMap<Node, Node>();
//	private Map<FunctionEnv, Map<List<Node>, Node>> nodesMap =
//			new WeakHashMap<FunctionEnv, Map<List<Node>, Node>>();
//	private Map<FunctionEnv, Node> singleNodesMap =
//			new WeakHashMap<FunctionEnv, Node>();
	private Map<FunctionEnv, Map<Integer, Node>> varSingleNodesMap =
			new WeakHashMap<FunctionEnv, Map<Integer, Node>>();
	private Map<FunctionEnv, Map<List<NodeValue>, Map<Integer, Node>>> varNodesMap =
			new WeakHashMap<FunctionEnv, Map<List<NodeValue>, Map<Integer, Node>>>();
	private Map<FunctionEnv, Map<List<NodeValue>, Node>> nodesMap =
			new WeakHashMap<FunctionEnv, Map<List<NodeValue>, Node>>();
	private Map<FunctionEnv, Node> singleNodesMap =
			new WeakHashMap<FunctionEnv, Node>();
	
	private Node createNode() {
		return NodeFactory.createURI(SWI.BASE_URI + "/.well-known/genid/" + count++);
	}

//	public synchronized Node getNode(Node var, List<Node> list) {
//		if (list == null)
//			return getNode(var);
//		Map<List<Node>, Node> varMap = nodesMap.get(var);
//		Node resNode = null;
//		if (varMap == null) {
//			varMap = new HashMap<List<Node>, Node>();
//			nodesMap.put(var, varMap);
//		} else
//			resNode = varMap.get(list);
//		if (resNode == null) {
//			resNode = createNode();
//			varMap.put(list, resNode);
//		}
//		return resNode;
//	}

	public synchronized Node getNode(
			FunctionEnv env,
			List<NodeValue> list,
			int varNum) {
		if (list == null)
			return getNode(env, varNum);
		Map<List<NodeValue>, Map<Integer, Node>> paramMap = varNodesMap.get(env);
		Node resNode = null;
		Map<Integer, Node> varMap = null;
		if (paramMap == null) {
			paramMap = new HashMap<List<NodeValue>, Map<Integer, Node>>();
			varNodesMap.put(env, paramMap);
		} else
			varMap = paramMap.get(list);
		if (varMap == null) {
			varMap = new HashMap<Integer, Node>();
			varSingleNodesMap.put(env, varMap);
		} else
			resNode = varMap.get(varNum);
		if (resNode == null) {
			resNode = createNode();
			varMap.put(varNum, resNode);
		}
		return resNode;
	}

	public synchronized Node getNode(FunctionEnv env, List<NodeValue> list) {
		if (list == null)
			return getNode(env);
		Map<List<NodeValue>, Node> paramMap = nodesMap.get(env);
		Node resNode = null;
		if (paramMap == null) {
			paramMap = new HashMap<List<NodeValue>, Node>();
			nodesMap.put(env, paramMap);
		} else
			resNode = paramMap.get(list);
		if (resNode == null) {
			resNode = createNode();
			paramMap.put(list, resNode);
		}
		return resNode;
	}

	public synchronized Node getNode(FunctionEnv env) {
		Node resNode = singleNodesMap.get(env);
		if (resNode == null) {
			resNode = createNode();
			singleNodesMap.put(env, resNode);
		}
		return resNode;
	}
	
	public synchronized Node getNode() {
		return createNode();
	}
	
	public synchronized Node getNode(FunctionEnv env, int varNum) {
		Map<Integer, Node> varMap = varSingleNodesMap.get(env);
		Node resNode = null;
		if (varMap == null) {
			varMap = new HashMap<Integer, Node>();
			varSingleNodesMap.put(env, varMap);
		} else
			resNode = varMap.get(varNum);
		if (resNode == null) {
			resNode = createNode();
			varMap.put(varNum, resNode);
		}
		return resNode;
	}
	
//	public static int getSkolemizedId(Node node) {
//		if (!node.isURI())
//			return -1;
//		int baseLength = SWI.BASE_URI.length() + "/.well-known/genid/".length();
//		String uri = node.getURI();
//		if (uri.length() <= baseLength)
//			return -1;
//		String numberString = uri.substring(baseLength);
//		try {
//			int number = Integer.parseInt(numberString);
//			if (number < 0)
//				return -1;
//			return number;
//		} catch(NumberFormatException e) {
//			return -1;
//		}
//	}

	public static String getSkolemizedId(Node node) {
		if (!node.isURI())
			return null;
		int baseLength = SWI.BASE_URI.length() + "/.well-known/genid/".length();
		String uri = node.getURI();
		if (uri.length() > baseLength
				&& uri.substring(0, baseLength).equals(SWI.BASE_URI + "/.well-known/genid/"))
			return uri.substring(baseLength);
		return null;
	}

	public static Graph deSkolemize(Graph inputGraph) {
		Graph newGraph = GraphFactory.createDefaultGraph();
		GraphUtil.add(
				newGraph,
				inputGraph.find(Node.ANY, Node.ANY, Node.ANY).mapWith(new Map1<Triple, Triple>() {
//					Map<Integer, Node> bnodes = new HashMap<Integer, Node>();
					Map<String, Node> bnodes = new HashMap<String, Node>();
					private Node convert(Node inNode) {
//						int id = getSkolemizedId(inNode);
//						int id = getSkolemizedId(inNode);
//						if (id == -1)
						String id = getSkolemizedId(inNode);
						if (id == null)
							return inNode;
						else {
							Node bnode = bnodes.get(id);
							if (bnode == null) {
								bnode = NodeFactory.createAnon();
								bnodes.put(id, bnode);
							}
							return bnode;
						}
					}
					public Triple map1(Triple inTriple) {
						return new Triple(
								convert(inTriple.getSubject()),
								convert(inTriple.getPredicate()),
								convert(inTriple.getObject()));
					}
		}));
		return newGraph;
	}
	
	public Graph skolemize(Graph inputGraph) {
		Graph newGraph = GraphFactory.createDefaultGraph();
		GraphUtil.add(
				newGraph,
				inputGraph.find(Node.ANY, Node.ANY, Node.ANY).mapWith(new Map1<Triple, Triple>() {
//					Map<Integer, Node> bnodes = new HashMap<Integer, Node>();
					Map<Node, Node> bnodeIds = new HashMap<Node, Node>();
					private Node convert(Node inNode) {
//						int id = getSkolemizedId(inNode);
//						int id = getSkolemizedId(inNode);
//						if (id == -1)
						if (!inNode.isBlank())
							return inNode;
						else {
							Node id = bnodeIds.get(inNode);
							if (id == null) {
								id = getNode();
								bnodeIds.put(inNode, id);
							}
							return id;
						}
					}
					public Triple map1(Triple inTriple) {
						return new Triple(
								convert(inTriple.getSubject()),
								convert(inTriple.getPredicate()),
								convert(inTriple.getObject()));
					}
		}));
		return newGraph;
	}
	
//	public synchronized Node getNode(Node var) {
//		Node resNode = singleNodesMap.get(var);
//		if (resNode == null) {
//			resNode = createNode();
//			singleNodesMap.put(var, resNode);
//		}
//		return resNode;
//	}
	
}

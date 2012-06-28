package org.swows.node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.swows.vocabulary.SWI;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionEnv;

public class Skolemizer {

	private static Skolemizer singleton = null;
	
	public static Skolemizer getInstance() {
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
	private Map<FunctionEnv, Map<List<NodeValue>, Node>> nodesMap =
			new WeakHashMap<FunctionEnv, Map<List<NodeValue>, Node>>();
	private Map<FunctionEnv, Node> singleNodesMap =
			new WeakHashMap<FunctionEnv, Node>();
	
	private Node createNode() {
		return Node.createURI(SWI.BASE_URI + "/.well-known/genid/" + count++);
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

	public synchronized Node getNode(FunctionEnv env, List<NodeValue> list) {
		if (list == null)
			return getNode(env);
		Map<List<NodeValue>, Node> varMap = nodesMap.get(env);
		Node resNode = null;
		if (varMap == null) {
			varMap = new HashMap<List<NodeValue>, Node>();
			nodesMap.put(env, varMap);
		} else
			resNode = varMap.get(list);
		if (resNode == null) {
			resNode = createNode();
			varMap.put(list, resNode);
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
	
//	public synchronized Node getNode(Node var) {
//		Node resNode = singleNodesMap.get(var);
//		if (resNode == null) {
//			resNode = createNode();
//			singleNodesMap.put(var, resNode);
//		}
//		return resNode;
//	}
	
}

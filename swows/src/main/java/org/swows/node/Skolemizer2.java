package org.swows.node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.Create;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.swows.vocabulary.SWI;

public class Skolemizer2 {

	private static Skolemizer2 singleton = null;
	
	public static synchronized Skolemizer2 getInstance() {
		if (singleton == null)
			singleton = new Skolemizer2();
		return singleton;
	}
	
	private long count = 0;

//	private Map<FunctionEnv, Map<Integer, Node>> varSingleNodesMap =
//			new WeakHashMap<FunctionEnv, Map<Integer, Node>>();
//	private Map<FunctionEnv, Map<List<NodeValue>, Map<Integer, Node>>> varNodesMap =
//			new WeakHashMap<FunctionEnv, Map<List<NodeValue>, Map<Integer, Node>>>();
//	private Map<FunctionEnv, Map<List<NodeValue>, Node>> nodesMap =
//			new WeakHashMap<FunctionEnv, Map<List<NodeValue>, Node>>();
//	private Map<FunctionEnv, Node> singleNodesMap =
//			new WeakHashMap<FunctionEnv, Node>();
	
	private URI createNode(ValueFactory valFactory) {
		return valFactory.createURI(SWI.BASE_URI + "/.well-known/genid/" + count++);
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

	public synchronized URI getNode(ValueFactory valFactory) {
		return createNode(valFactory);
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

	public RDFHandler skolemizerHandler(final RDFHandler outputHandler, final ValueFactory valFactory) {
		return new RDFHandler() {
			private Map<BNode, URI> bnodes2uris = new HashMap<BNode, URI>();
			private URI getURI(BNode bNode) {
				URI uri = bnodes2uris.get(bNode);
				if (uri == null) {
					uri = createNode(valFactory);
					bnodes2uris.put(bNode, uri);
				}
				return uri;
			}
			private Value convert(Value in) {
				return (in instanceof BNode) ? getURI((BNode) in) : in;
			}
			@Override
			public void startRDF() throws RDFHandlerException {
				outputHandler.startRDF();
			}
			
			@Override
			public void handleStatement(Statement st) throws RDFHandlerException {
				outputHandler.handleStatement(
						valFactory.createStatement(
								(Resource) convert(st.getSubject()),
								st.getPredicate(),
								convert(st.getObject()),
								(Resource) convert(st.getContext()) ));
			}
			
			@Override
			public void handleNamespace(String prefix, String uri)
					throws RDFHandlerException {
				outputHandler.handleNamespace(prefix, uri);
			}
			
			@Override
			public void handleComment(String comment) throws RDFHandlerException {
				outputHandler.handleComment(comment);
			}
			
			@Override
			public void endRDF() throws RDFHandlerException {
				outputHandler.endRDF();
			}
		};
	}
	
	public static String getSkolemizedId(URI node) {
		int baseLength = SWI.BASE_URI.length() + "/.well-known/genid/".length();
		String uri = node.stringValue();
		if (uri.length() > baseLength
				&& uri.substring(0, baseLength).equals(SWI.BASE_URI + "/.well-known/genid/"))
			return uri.substring(baseLength);
		return null;
	}
	
	public static RDFHandler deskolemizerHandler(
			final RDFHandler outputHandler, final ValueFactory valFactory) {
		return new RDFHandler() {
			private Map<String, BNode> id2bnodes = new HashMap<String, BNode>();
			private Resource getResource(URI uri) {
				String id = getSkolemizedId(uri);
				if (id == null)
					return uri;
				else {
					BNode bnode = id2bnodes.get(id);
					if (bnode == null) {
						bnode = valFactory.createBNode();
						id2bnodes.put(id, bnode);
					}
					return bnode;
				}
			}
			private Value convert(Value in) {
				return (in instanceof URI) ? getResource((URI) in) : in;
			}
			@Override
			public void startRDF() throws RDFHandlerException {
				outputHandler.startRDF();
			}
			
			@Override
			public void handleStatement(Statement st) throws RDFHandlerException {
				outputHandler.handleStatement(
						valFactory.createStatement(
								(Resource) convert(st.getSubject()),
								st.getPredicate(),
								convert(st.getObject()),
								(Resource) convert(st.getContext()) ));
			}
			
			@Override
			public void handleNamespace(String prefix, String uri)
					throws RDFHandlerException {
				outputHandler.handleNamespace(prefix, uri);
			}
			
			@Override
			public void handleComment(String comment) throws RDFHandlerException {
				outputHandler.handleComment(comment);
			}
			
			@Override
			public void endRDF() throws RDFHandlerException {
				outputHandler.endRDF();
			}
		};
	}
	
}

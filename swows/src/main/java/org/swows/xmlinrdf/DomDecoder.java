package org.swows.xmlinrdf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.swows.graph.PushGraphListener;
import org.swows.runnable.RunnableContext;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.xml;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.vocabulary.RDF;

public class DomDecoder extends PushGraphListener implements RunnableContext {

	public static Document decode(Graph graph, Node docRootNode) {
		try {
			return decode(graph, docRootNode, DOMImplementationRegistry.newInstance().getDOMImplementation("XML 1.0"));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	public static Document decodeOne(Graph graph) {
		return decodeAll(graph).next();
	}

	public static ExtendedIterator<Document> decodeAll(final Graph graph) {
		return graph
				.find(Node.ANY, RDF.type.asNode(), xml.Document.asNode())
				.mapWith(new Map1<Triple, Document>() {
					@Override
					public Document map1(Triple triple) {
						return decode(graph, triple.getSubject());
					}
				});
	}

	private static String qName(final Graph graph, final Node elementNode) {
		return
				GraphUtils.getSingleValueProperty(graph, elementNode, xml.nodeName.asNode())
				.getLiteralLexicalForm();
	}
	
	private static String namespace(final Graph graph, final Node elementNode) {
		try {
			return
				graph.find(elementNode, xml.namespace.asNode(), Node.ANY)
				.next().getObject().getURI();
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
	private static String value(final Graph graph, final Node elementNode) {
		try {
			return
				graph.find(elementNode, xml.nodeValue.asNode(), Node.ANY)
				.next().getObject().getLiteralLexicalForm();
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
	private Text decodeText(Graph graph, Node elementNode) {
		return document.createTextNode(value(graph, elementNode));
	}
	
	private Comment decodeComment(Graph graph, Node elementNode) {
		return document.createComment(value(graph, elementNode));
	}
	
	private ProcessingInstruction decodeProcessingInstruction(Graph graph, Node elementNode) {
		return document.createProcessingInstruction(
				qName(graph, elementNode),
				value(graph, elementNode) );
	}
	
	private Attr decodeAttr(Graph graph, Node elementNode) {
		Attr attr =
				document.createAttributeNS(
						namespace(graph, elementNode),
						qName(graph, elementNode) );
		attr.setValue(value(graph, elementNode));
		return attr;
	}

	private void decodeElementAttrsAndChildren(final Element element, final Graph graph, final Node elementNode) {
		ExtendedIterator<Attr> attrs =
				graph.find(elementNode, xml.hasAttribute.asNode(), Node.ANY)
				.mapWith(new Map1<Triple, Attr>() {
					@Override
					public Attr map1(Triple triple) {
						Attr newAttr = decodeAttr(graph, triple.getObject());
						addNodeMapping(triple.getObject(), newAttr);
						return newAttr;
					}
				});
		while (attrs.hasNext())
			element.setAttributeNodeNS(attrs.next());
		Set<Node> orderedChildren = new HashSet<Node>();
		{
			Node child = GraphUtils.getSingleValueOptProperty(graph, elementNode, xml.firstChild.asNode());
			while (child != null) {
				orderedChildren.add(child);
				org.w3c.dom.Node newChild = decodeNode(graph, child);
				if (newChild != null) {
					addNodeMapping(child, newChild);
					element.appendChild(newChild);
				}
				child = GraphUtils.getSingleValueOptProperty(graph, child, xml.nextSibling.asNode());
			}
		}
		ExtendedIterator<Node> children = GraphUtils.getPropertyValues(graph, elementNode, xml.hasChild.asNode());
		while (children.hasNext()) {
			Node child = children.next();
			if (!orderedChildren.contains(child)) {
				org.w3c.dom.Node domChild = decodeNode(graph, child);
				if (domChild != null) {
					addNodeMapping(child, domChild);
					element.appendChild(domChild);
				}
			}
		}
		//return element;
	}

	private Element decodeElement(final Graph graph, final Node elementNode) {
		Element element =
				document.createElementNS(
						namespace(graph, elementNode),
						qName(graph, elementNode) );
		addNodeMapping(elementNode, element);
		decodeElementAttrsAndChildren(element, graph, elementNode);
		return element;
	}

	private org.w3c.dom.Node decodeNode(Graph graph, Node elementNode) {
		try {
			Node nodeType = graph.find(elementNode, xml.nodeType.asNode(), Node.ANY).next().getObject();
			if (nodeType.equals(xml.Attr.asNode()))
				return decodeAttr(graph, elementNode);
			if (nodeType.equals(xml.Comment.asNode()))
				return decodeComment(graph, elementNode);
			if (nodeType.equals(xml.Element.asNode()))
				return decodeElement(graph, elementNode);
			if (nodeType.equals(xml.ProcessingInstruction.asNode()))
				return decodeProcessingInstruction(graph, elementNode);
			if (nodeType.equals(xml.Text.asNode()))
				return decodeText(graph, elementNode);
			throw new RuntimeException("Type not recognised for node " + elementNode);
		} catch(NoSuchElementException e) {
			throw new RuntimeException("Type not found for node " + elementNode);
		}
	}

	private void decodeWorker(Graph graph, Node docRootNode, DOMImplementation domImpl) {
		Node elementNode = graph.find(docRootNode, xml.hasChild.asNode(), Node.ANY).next().getObject();
		document =
				domImpl.createDocument(
						namespace(graph, elementNode),
						qName(graph, elementNode),
						null);
		Element docElement = document.getDocumentElement();
		addNodeMapping(docRootNode, document);
		addNodeMapping(elementNode, docElement);
		decodeElementAttrsAndChildren( docElement, graph, elementNode );
		graph.getEventManager().register(this);
	}

	public static Document decode(Graph graph, Node docRootNode, DOMImplementation domImpl) {
		return (new DomDecoder(graph, docRootNode, domImpl)).getDocument();
	}

	public static Document decode(Graph graph, Node docRootNode, DOMImplementation domImpl, RunnableContext updatesContext) {
		return (new DomDecoder(graph, docRootNode, domImpl, updatesContext)).getDocument();
	}

	public static Document decodeOne(Graph graph, DOMImplementation domImpl) {
		return decodeAll(graph, domImpl).next();
	}

	public static Document decodeOne(Graph graph, DOMImplementation domImpl, RunnableContext updatesContext) {
		return decodeAll(graph, domImpl, updatesContext).next();
	}

	public static ExtendedIterator<Document> decodeAll(final Graph graph, final DOMImplementation domImpl) {
		return graph
				.find(Node.ANY, RDF.type.asNode(), xml.Document.asNode())
				.mapWith(new Map1<Triple, Document>() {
					@Override
					public Document map1(Triple triple) {
						return decode(graph, triple.getSubject(), domImpl);
					}
				});
	}
	
	public static ExtendedIterator<Document> decodeAll(final Graph graph, final DOMImplementation domImpl, final RunnableContext updatesContext) {
		return graph
				.find(Node.ANY, RDF.type.asNode(), xml.Document.asNode())
				.mapWith(new Map1<Triple, Document>() {
					@Override
					public Document map1(Triple triple) {
						return decode(graph, triple.getSubject(), domImpl, updatesContext);
					}
				});
	}
	
	Document document;
	RunnableContext updatesContext;
	Map<Node, Set<org.w3c.dom.Node>> graph2domNodeMapping = new HashMap<Node, Set<org.w3c.dom.Node>>();
	Map<org.w3c.dom.Node, Node> dom2graphNodeMapping = new HashMap<org.w3c.dom.Node, Node>();
	
	private void addNodeMapping(Node graphNode, org.w3c.dom.Node domNode) {
//		System.out.println(this + ": adding mapping ( " + graphNode + " -> " + domNode + " )");
		Set<org.w3c.dom.Node> domeNodeSet = graph2domNodeMapping.get(graphNode);
		if (domeNodeSet == null) {
			domeNodeSet = new HashSet<org.w3c.dom.Node>();
			graph2domNodeMapping.put(graphNode, domeNodeSet);
		}
		domeNodeSet.add(domNode);
		dom2graphNodeMapping.put(domNode, graphNode);
	}
	
	private void removeNodeMapping(org.w3c.dom.Node domNode) {
		Node graphNode = dom2graphNodeMapping.get(domNode);
		if (graphNode != null) {
			dom2graphNodeMapping.remove(domNode);
			Set<org.w3c.dom.Node> domeNodeSet = graph2domNodeMapping.get(graphNode);
			domeNodeSet.remove(domNode);
			if (domeNodeSet.isEmpty())
				graph2domNodeMapping.remove(graphNode);
		}
	}
	
	private void removeSubtreeMapping(org.w3c.dom.Node domNode) {
//		System.out.println(this + ": removing subtree mapping of " + domNode );
		removeNodeMapping(domNode);
		NamedNodeMap attrMap = domNode.getAttributes();
		if (attrMap != null) {
			for (int i = 0; i < attrMap.getLength(); i++) {
				removeSubtreeMapping(attrMap.item(i));
			}
		}
		NodeList children = domNode.getChildNodes();
		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				removeSubtreeMapping(children.item(i));
			}
		}
	}
	
	private DomDecoder(Graph graph) {
		super(graph);
		this.updatesContext = this;
	}

	private DomDecoder(Graph graph, Node docRootNode, DOMImplementation domImpl) {
		super(graph);
		decodeWorker(graph, docRootNode, domImpl);
		this.updatesContext = this;
	}
	
	private DomDecoder(Graph graph, Node docRootNode, DOMImplementation domImpl, RunnableContext updatesContext) {
		super(graph);
		decodeWorker(graph, docRootNode, domImpl);
		this.updatesContext = updatesContext;
	}
	
	public void run(Runnable runnable) {
		runnable.run();
	}
	
	private Document getDocument() {
		return document;
	}

	/*
	private void execMap(Set<org.w3c.dom.Node> domNodeSet, Map1<org.w3c.dom.Node,Void> map) {
		//ExtendedIterator<org.w3c.dom.Node> iter;
	//	ExtendedIterator<Void> iter = (new NiceIterator<org.w3c.dom.Node>()).andThen(domNodeSet.iterator()).mapWith(map);
		Iterator<org.w3c.dom.Node> iter = domNodeSet.iterator();
//		while (iter.hasNext()) iter.next();
		while (iter.hasNext()) {
			map.map1(iter.next());
		}
	}
*/
	
	@Override
	protected void notifyEvents() {
		/*
		Graph newGraph =
				new Difference(
						new Union(sourceGraph, addEvents),
						deleteEvents);
						*/

//		System.out.println(this + ": *** ADD EVENTS ******");
//		ModelFactory.createModelForGraph(addEvents).write(System.out,"N3");
//		System.out.println(this + ": *********************");
//		System.out.println(this + ": *** DELETE EVENTS ***");
//		ModelFactory.createModelForGraph(deleteEvents).write(System.out,"N3");
//		System.out.println(this + ": *********************");
		
		ExtendedIterator<Triple> addEventsIter = addEvents.find(Node.ANY, Node.ANY, Node.ANY);
		
		DomDecoder newDom = new DomDecoder(sourceGraph);
		newDom.dom2graphNodeMapping = (Map<org.w3c.dom.Node, Node>) ((HashMap<org.w3c.dom.Node, Node>) dom2graphNodeMapping).clone();
		newDom.graph2domNodeMapping = (Map<Node, Set<org.w3c.dom.Node>>) ((HashMap<Node, Set<org.w3c.dom.Node>>) graph2domNodeMapping).clone();
		newDom.document = document;
		
		while (addEventsIter.hasNext()) {
			final Triple newTriple = addEventsIter.next();
//			org.w3c.dom.Node xmlSubj = nodeMapping.get(newTriple.getSubject());
			//System.out.println("Checking add event " + newTriple);
			Set<org.w3c.dom.Node> domSubjs = graph2domNodeMapping.get(newTriple.getSubject());
//			if (domSubjs == null)
//				System.out.println(this + ": managing add event " + newTriple + ", domSubjs is null");
			if (domSubjs != null) {
				//System.out.println("Managing add event " + newTriple + " for domSubjs " + domSubjs);
				Iterator<org.w3c.dom.Node> domSubjIter = domSubjs.iterator();
				if ( 	( 	newTriple.getPredicate().equals(xml.nodeName)
							|| newTriple.getPredicate().equals(xml.namespace) )
						&& !deleteEvents.contains(newTriple.getSubject(), xml.nodeType.asNode(), Node.ANY) ) {
					//org.w3c.dom.Node parentNode = null;
					Node nodeType =
							sourceGraph
							.find(newTriple.getSubject(), xml.nodeType.asNode(), Node.ANY)
							.next().getObject();
					if (nodeType.equals(xml.Attr.asNode())) {
						while (domSubjIter.hasNext()) {
							org.w3c.dom.Node domSubj = domSubjIter.next();
							Attr oldAttr = (Attr) domSubj;
							//Attr newAttr = decodeAttr(sourceGraph, newTriple.getSubject());
							Attr newAttr = newDom.decodeAttr(sourceGraph, newTriple.getSubject());
							Element ownerElement = oldAttr.getOwnerElement();
							ownerElement.removeAttributeNode(oldAttr);
							//removeSubtreeMapping(oldAttr);
							newDom.removeSubtreeMapping(oldAttr);
							ownerElement.setAttributeNodeNS(newAttr);
							//addNodeMapping(newTriple.getSubject(), newAttr);
							newDom.addNodeMapping(newTriple.getSubject(), newAttr);
						}
					} else if (!nodeType.equals(xml.Document.asNode())) {
						while (domSubjIter.hasNext()) {
							org.w3c.dom.Node domSubj = domSubjIter.next();
							org.w3c.dom.Node parentNode = domSubj.getParentNode();
							if (parentNode != null) {
								org.w3c.dom.Node newNode = newDom.decodeNode(sourceGraph, newTriple.getSubject());
								parentNode.replaceChild(newNode, domSubj);
								newDom.removeSubtreeMapping(domSubj);
								newDom.addNodeMapping(newTriple.getSubject(), newNode);
							}
						}
					}
				} else if ( newTriple.getPredicate().equals(xml.nodeValue.asNode()) ) {
					while (domSubjIter.hasNext()) {
						org.w3c.dom.Node domSubj = domSubjIter.next();
//						System.out.println(this + ": for node " + domSubj + " setting new value: " + newTriple.getObject().getLiteralLexicalForm());
						domSubj.setNodeValue(
								newTriple.getObject().getLiteralLexicalForm());
					}
				} else if ( newTriple.getPredicate().equals(xml.hasAttribute.asNode()) ) {
					while (domSubjIter.hasNext()) {
						Element element = (Element) domSubjIter.next();
						Attr newAttr = newDom.decodeAttr(sourceGraph, newTriple.getObject());
						newDom.addNodeMapping(newTriple.getObject(), newAttr);
						element.setAttributeNodeNS(newAttr);
					}
				} else if ( newTriple.getPredicate().equals(xml.hasChild.asNode()) ) {
					Node nodeType =
							sourceGraph
							.find(newTriple.getSubject(), xml.nodeType.asNode(), Node.ANY)
							.next().getObject();
					//System.out.println("Managing add hasChild (" + newTriple + ") for domSubjs " + domSubjs + " and node type " + nodeType);
					if (nodeType.equals(xml.Element.asNode())) {
						while (domSubjIter.hasNext()) {
							Element element = (Element) domSubjIter.next();
							org.w3c.dom.Node newChild = newDom.decodeNode(sourceGraph, newTriple.getObject());
							newDom.addNodeMapping(newTriple.getObject(), newChild);
							element.appendChild(newChild);
						}
					}
				}
			}			
		}

		ExtendedIterator<Triple> deleteEventsIter = deleteEvents.find(Node.ANY, Node.ANY, Node.ANY);
		while (deleteEventsIter.hasNext()) {
			Triple oldTriple = deleteEventsIter.next();
			//org.w3c.dom.Node xmlSubj = nodeMapping.get(oldTriple.getSubject());
			Set<org.w3c.dom.Node> domSubjs = graph2domNodeMapping.get(oldTriple.getSubject());
			//System.out.println("Checking for " + oldTriple.getSubject() + " contained in " + sourceGraph);
			if (domSubjs != null && sourceGraph.contains(oldTriple.getSubject(), RDF.type.asNode(), Node.ANY)) {
				//System.out.println("Found " + oldTriple.getSubject() + " contained in " + sourceGraph);
				//System.out.println("Managing " + oldTriple.getPredicate() + "/" + oldTriple.getObject());
				Iterator<org.w3c.dom.Node> domSubjIter = domSubjs.iterator();
				if ( 		(	oldTriple.getPredicate().equals(xml.nodeName.asNode())
								&& !addEvents.contains(oldTriple.getSubject(), xml.nodeName.asNode(), Node.ANY) )
						||	(	oldTriple.getPredicate().equals(xml.nodeType.asNode())
								&& !addEvents.contains(oldTriple.getSubject(), xml.nodeType.asNode(), Node.ANY) ) ) {
					while (domSubjIter.hasNext()) {
						org.w3c.dom.Node domSubj = domSubjIter.next();
						switch (domSubj.getNodeType()) {
						case org.w3c.dom.Node.ATTRIBUTE_NODE:
							Attr oldAttr = (Attr) domSubj;
							Element ownerElement = oldAttr.getOwnerElement();
							ownerElement.removeAttributeNode(oldAttr);
							newDom.removeSubtreeMapping(oldAttr);
							break;
						case org.w3c.dom.Node.DOCUMENT_NODE: 
							break;
						default:
							org.w3c.dom.Node parentNode = domSubj.getParentNode();
							if (parentNode != null) {
								parentNode.removeChild(domSubj);
								newDom.removeSubtreeMapping(domSubj);
							}
						}
							
					}
				} else if (
						oldTriple.getPredicate().equals(xml.nodeValue.asNode())
						&& !sourceGraph.contains(oldTriple.getSubject(), xml.nodeValue.asNode(), Node.ANY)) {
					while (domSubjIter.hasNext()) {
						org.w3c.dom.Node domSubj = domSubjIter.next();
						domSubj.setNodeValue("");
					}
				} else if ( oldTriple.getPredicate().equals(xml.hasAttribute.asNode()) ) {
					Set<org.w3c.dom.Node> domObjs = graph2domNodeMapping.get(oldTriple.getObject());
					if (domObjs != null) {
						while (domSubjIter.hasNext()) {
							Element element = (Element) domSubjIter.next();
							Iterator<org.w3c.dom.Node> domObjsIter = domObjs.iterator();
							while (domObjsIter.hasNext()) {
								try {
									Attr oldAttr = (Attr) domObjsIter.next();
									element.removeAttributeNode(oldAttr);
									newDom.removeSubtreeMapping(oldAttr);
								} catch(DOMException e) {
									if (!e.equals(DOMException.NOT_FOUND_ERR))
										throw e;
								}
							}
						}
					}
				} else if ( oldTriple.getPredicate().equals(xml.hasChild.asNode()) ) {
					Set<org.w3c.dom.Node> domObjs = graph2domNodeMapping.get(oldTriple.getObject());
					if (domObjs != null) {
						while (domSubjIter.hasNext()) {
							Element element = (Element) domSubjIter.next();
							Iterator<org.w3c.dom.Node> domObjsIter = domObjs.iterator();
							while (domObjsIter.hasNext()) {
								try {
									org.w3c.dom.Node domObj = domObjsIter.next();
									element.removeChild(domObj);
									newDom.removeSubtreeMapping(domObj);
								} catch(DOMException e) {
									if (!e.equals(DOMException.NOT_FOUND_ERR))
										throw e;
								}
							}
						}
					}
				}
			}
//	        System.out.println("End of notifyEvents() in " + this);
		}
		
		dom2graphNodeMapping = newDom.dom2graphNodeMapping;
		graph2domNodeMapping = newDom.graph2domNodeMapping;

	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.util.graph.GraphListenerBase#finishRead()
	 */
	@Override
    protected synchronized void finishRead() {
//		System.out.println("Begin of finishRead() in " + this);
		if (!addEvents.isEmpty() || !deleteEvents.isEmpty()) {
			updatesContext.run(
					new Runnable() {
						@Override
						public void run() {
							notifyEvents();
						}
					});
		}
		addEvents = null;
		deleteEvents = null;
    }


	/*
	protected void notifyAdd(Triple t) {
		boolean
		Node subj = t.getSubject();
		org.w3c.dom.Node xmlSubj = nodeMapping.get(subj);
		if (xmlSubj == null) {
			// new node
			// defer management until is created
		} else {
			// existing node
		}
		Node pNode = t.getPredicate();
		if (pNode.equals(xml.hasChild.asNode())) {
			// new child
		} else if (pNode.equals(xml.hasAttribute.asNode())) {
			// new attribute
			// Remove delete event of same attribute
			Map<Node, Set<Node>> prevPredMap = deleteEventsHash.get(t.getSubject());
			if (prevPredMap != null) {
				Set<Node> objSet = prevPredMap.get(t.getPredicate());
				if (objSet != null) {
					
				}
			}
		}
	}
	
	@Override
	protected void notifyDelete(Triple t) {}
*/

}

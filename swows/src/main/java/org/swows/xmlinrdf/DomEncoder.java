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
package org.swows.xmlinrdf;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.swows.vocabulary.xml;
import org.swows.vocabulary.xmlInstance;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphStatisticsHandler;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.impl.SimpleEventManager;
import com.hp.hpl.jena.graph.impl.SimpleTransactionHandler;
import com.hp.hpl.jena.graph.query.QueryHandler;
import com.hp.hpl.jena.graph.query.SimpleQueryHandler;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.DeleteDeniedException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.sparql.graph.Reifier2;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.util.iterator.NiceIterator;
import com.hp.hpl.jena.util.iterator.NullIterator;
import com.hp.hpl.jena.util.iterator.SingletonIterator;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO: Auto-generated Javadoc
/**
 * The Class DomEncoder.
 */
public class DomEncoder {

/*
	private static interface NodeReference {
		public abstract org.w3c.dom.Node getNode();
	}
*/

	static class EncodedDocument implements Graph {
		
		private static final NullIterator<Triple> emptyTripleIterator = new NullIterator<Triple>();
		private static final NullIterator<org.w3c.dom.Node> emptyNodeIterator = new NullIterator<org.w3c.dom.Node>();

		private Document document;
		private Node rootNode = null;
		
		private boolean closed = false;
/*
		private Map<AnonId, Element> mapBlankId2Element = new ConcurrentHashMap<AnonId, Element>();
		private Map<Element, AnonId> mapElement2BlankId = new ConcurrentHashMap<Element, AnonId>();
		private Map<AnonId, Element> mapBlankId2Attr = new ConcurrentHashMap<AnonId, Element>();
		private Map<Element, AnonId> mapAttr2BlankId = new ConcurrentHashMap<Element, AnonId>();
*/

		private Map<Node, org.w3c.dom.Node> mapBlankId2XmlNode = new ConcurrentHashMap<Node, org.w3c.dom.Node>();
		private Map<org.w3c.dom.Node, Node> mapXmlNode2BlankId = new ConcurrentHashMap<org.w3c.dom.Node, Node>();

		public EncodedDocument(Document document) {
			this.document = document;
		}

		public EncodedDocument(Document document, String rootUri) {
			this.document = document;
			rootNode = Node.createURI(rootUri);
		}

/*
		private Node mapElement2Node(Element element) {
			Attr idAttr = element.getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "id");
			if (idAttr != null)
				return Node.createURI(document.getBaseURI() + "#" + idAttr.getValue());
			AnonId anonId = mapElement2BlankId.get(element);
			if (anonId != null)
				return Node.createAnon(anonId);
			Node newNode = Node.createAnon();
			mapElement2BlankId.put(element, anonId);
			mapBlankId2Element.put(anonId, element);
			return newNode;
		}

		private Element mapNode2Element(Node node) {
			if (node.isURI())
				return document.getElementById(node.getURI());
			if (node.isBlank())
				return mapBlankId2Element.get(node.getBlankNodeId());
			return null;
		}
*/

		private Node mapXmlNode2GraphNode(org.w3c.dom.Node node) {
			Node graphNode = mapXmlNode2BlankId.get(node);
			if (graphNode != null)
				return graphNode;
			if (node.getNodeType() == org.w3c.dom.Node.DOCUMENT_NODE && rootNode != null)
				graphNode = rootNode;
			if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				Attr idAttr = ((Element) node).getAttributeNode("id");
				if (idAttr != null)
					graphNode = Node.createURI(xmlInstance.getURI() + idAttr.getValue());
				else {
					Attr xmlIdAttr = ((Element) node).getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "id");
					if (xmlIdAttr != null)
						graphNode = Node.createURI(xmlInstance.getURI() + xmlIdAttr.getValue());
					else {
						Attr svgIdAttr = ((Element) node).getAttributeNodeNS("http://www.w3.org/2000/svg", "id");
						if (svgIdAttr != null)
							graphNode = Node.createURI(xmlInstance.getURI() + svgIdAttr.getValue());
					}
				}
				
			}
			if (graphNode == null)
				graphNode = Node.createAnon();
			mapXmlNode2BlankId.put(node, graphNode);
			mapBlankId2XmlNode.put(graphNode, node);
			return graphNode;
		}

		private org.w3c.dom.Node mapGraphNode2XmlNode(Node node) {
//			if (node.isURI()) {
//				if (rootNode != null && rootNode.equals(node))
//					return document;
//				return document.getElementById(node.getURI());
//			}
//			if (node.isBlank())
				return mapBlankId2XmlNode.get(node);
//			return null;
		}

		private Iterator<org.w3c.dom.Node> listNodesExceptDocument() {
			return Utils.listSubtreeNodes(document.getDocumentElement());
		}

		private Iterator<org.w3c.dom.Node> listNodes() {
			return new NullIterator<org.w3c.dom.Node>()
					.andThen(new SingletonIterator<org.w3c.dom.Node>(document))
					.andThen(listNodesExceptDocument());
		}

		@Override
		public void close() {
			closed = true;
		}

		@Override
		public boolean contains(Triple t) {
			return contains(t.getSubject(), t.getPredicate(), t.getObject());
		}

		@Override
		public boolean contains(Node s, Node p, Node o) {

			if (s == null || !s.isConcrete()) {
				
				if (p == null || !p.isConcrete()) {

					if (o == null || !o.isConcrete()) {

						// Schema: * * *
						return true;

					} else  {
						
						// Schema: * * o
						return // namescape
								( o.isURI() && ( xml.getURI().equals(o.getURI()) || document.getElementsByTagNameNS(o.getURI(), "*").getLength() > 0 ) )
								|| // nodeName
								( document.getElementsByTagNameNS("*", o.getLiteralLexicalForm()).getLength() > 0 )
								|| // nodeVallue
								Utils.containsNodeValue(document, o.getLiteralLexicalForm())
								|| // nodeType
								( o.isURI() && Utils.containsNodeType( document, Utils.mapResource2nodeType( ResourceFactory.createResource( o.getURI() ) ) ) )
								|| mapGraphNode2XmlNode(o) != null;

					}
					
				} else {

					if (o == null || !o.isConcrete()) {
						
						// Schema: * p *
						if ( p.equals(xml.namespace.asNode()) )
							return true;
						if ( p.equals(xml.nodeName.asNode()) )
							return true;
						if ( p.equals(xml.nodeValue.asNode()) )
							return Utils.containsNodeValue(document);
						if ( p.equals(xml.nodeType.asNode()) )
							return true;
						if (p.equals(xml.hasChild.asNode()))
							return true;
						if (p.equals(xml.firstChild.asNode()))
							return true;
						if (p.equals(xml.lastChild.asNode()))
							return true;
						if (p.equals(xml.previousSibling.asNode()))
							return Utils.containsSiblings(document);
						if (p.equals(xml.nextSibling.asNode()))
							return Utils.containsSiblings(document);
						if (p.equals(xml.parentNode.asNode()))
							return true;
						if (p.equals(xml.hasAttribute.asNode()))
							return Utils.containsAttributes(document);
						if (p.equals(xml.ownerDocument.asNode()))
							return true;
						return false;

					} else {

						// Schema: * p o
						
						if ( p.equals(xml.namespace.asNode()) )
							return o.isURI()
									&& ( xml.getURI().equals(o.getURI()) || document.getElementsByTagNameNS(o.getURI(), "*").getLength() > 0 );
						if ( p.equals(xml.nodeName.asNode()) )
							return (document.getElementsByTagNameNS("*", o.getLiteralLexicalForm()).getLength() > 0 );
						if ( p.equals(xml.nodeValue.asNode()) )
							return Utils.containsNodeValue(document, o.getLiteralLexicalForm());
						if ( p.equals(xml.nodeType.asNode()) )
							return o.isURI() && Utils.containsNodeType(document, Utils.mapResource2nodeType(ResourceFactory.createResource(o.getURI())));

						// Part in which object must represent an xml node
						org.w3c.dom.Node objXmlNode = mapGraphNode2XmlNode(o);
						if (objXmlNode == null)
							return false;
						if (p.equals(xml.hasChild.asNode()))
							return objXmlNode.getParentNode() != null;
						if (p.equals(xml.firstChild.asNode()))
							return objXmlNode.getParentNode() != null
								&& objXmlNode.getPreviousSibling() == null;
						if (p.equals(xml.lastChild.asNode()))
							return objXmlNode.getParentNode() != null
								&& objXmlNode.getNextSibling() == null;
						if (p.equals(xml.previousSibling.asNode()))
							return objXmlNode.getNextSibling() != null;
						if (p.equals(xml.nextSibling.asNode()))
							return objXmlNode.getPreviousSibling() != null;
						if (p.equals(xml.parentNode.asNode()))
							return objXmlNode.hasChildNodes();
						if (p.equals(xml.hasAttribute.asNode()))
							return (objXmlNode.getNodeType() == org.w3c.dom.Node.ATTRIBUTE_NODE);
						if (p.equals(xml.ownerDocument.asNode()))
							return objXmlNode.getNodeType() == org.w3c.dom.Node.DOCUMENT_NODE;
						return false;

					}
						
				}
				
			} else {
			
				if (p == null || !p.isConcrete()) {

					if (o == null || !o.isConcrete()) {

						// Schema: s * *
						return mapGraphNode2XmlNode(s) != null;

					} else  {
						
						// Schema: s * o
						org.w3c.dom.Node subjXmlNode = mapGraphNode2XmlNode(s);
						if (subjXmlNode == null)
							return false;
						if ( 	// namespace
								//o.hasURI(subjXmlNode.lookupNamespaceURI(subjXmlNode.getPrefix()))
								o.hasURI(subjXmlNode.getNamespaceURI())
								// nodeName
								|| o.getLiteralLexicalForm().equals(subjXmlNode.getNodeName())
								// nodeValue
								|| subjXmlNode.getNodeValue() != null && o.getLiteralLexicalForm().equals(subjXmlNode.getNodeValue())
								// nodeType
								|| o.equals(Utils.mapNodeType2resource(subjXmlNode.getNodeType()).asNode())
							) return true;

						// Part in which object must represent an xml node
						org.w3c.dom.Node objXmlNode = mapGraphNode2XmlNode(o);
						if (objXmlNode == null)
							return false;
						return 	// hasChild
								objXmlNode.getParentNode().equals(subjXmlNode)
								// firstChild
								|| subjXmlNode.getFirstChild().equals(objXmlNode)
								// lastChild
								|| subjXmlNode.getLastChild().equals(objXmlNode)
								// previousSibling
								|| subjXmlNode.getPreviousSibling().equals(objXmlNode)
								// nextSibling
								|| subjXmlNode.getNextSibling().equals(objXmlNode)
								// parentNode
								|| subjXmlNode.getParentNode().equals(objXmlNode)
								// hasAttribute
								|| ( objXmlNode.getNodeType() == org.w3c.dom.Node.ATTRIBUTE_NODE
										&& subjXmlNode
											.getAttributes()
											.getNamedItemNS(
												//objXmlNode.lookupNamespaceURI(objXmlNode.getPrefix()),
												objXmlNode.getNamespaceURI(),
												objXmlNode.getLocalName() ) != null )
								// ownerDocument
								|| subjXmlNode.getOwnerDocument().equals(objXmlNode);
					
					}
					
				} else {
						
					if (o == null || !o.isConcrete()) {

						// Schema: s p *
						return ( mapGraphNode2XmlNode(s) == null )
									? false
									: xml.isNodeProperty(p);

					} else  {
						
						// Schema: s p o
					
						org.w3c.dom.Node subjXmlNode = mapGraphNode2XmlNode(s);
						if (subjXmlNode == null)
							return false;
						if ( p.equals(xml.namespace.asNode()) )
							//return o.hasURI(subjXmlNode.lookupNamespaceURI(subjXmlNode.getPrefix()));
							return o.hasURI(subjXmlNode.getNamespaceURI());
						if ( p.equals(xml.nodeName.asNode()) )
							return o.getLiteralLexicalForm().equals(subjXmlNode.getNodeName());
						if ( p.equals(xml.nodeValue.asNode()) )
							return subjXmlNode.getNodeValue() != null && o.getLiteralLexicalForm().equals(subjXmlNode.getNodeValue());
						if ( p.equals(xml.nodeType.asNode()) )
							return o.equals(Utils.mapNodeType2resource(subjXmlNode.getNodeType()).asNode());

						// Part in which object must represent an xml node
						org.w3c.dom.Node objXmlNode = mapGraphNode2XmlNode(o);
						if (objXmlNode == null)
							return false;
						if (p.equals(xml.hasChild.asNode())) {
                                
							org.w3c.dom.Node parentNode = objXmlNode.getParentNode();
                                                        return (parentNode != null && parentNode.equals(subjXmlNode));
                                                }
                                                        if (p.equals(xml.firstChild.asNode()))
							return subjXmlNode.getFirstChild().equals(objXmlNode);
						if (p.equals(xml.lastChild.asNode()))
							return subjXmlNode.getLastChild().equals(objXmlNode);
						if (p.equals(xml.previousSibling.asNode()))
							return subjXmlNode.getPreviousSibling().equals(objXmlNode);
						if (p.equals(xml.nextSibling.asNode()))
							return subjXmlNode.getNextSibling().equals(objXmlNode);
						if (p.equals(xml.parentNode.asNode()))
							return subjXmlNode.getParentNode().equals(objXmlNode);
						if (p.equals(xml.hasAttribute.asNode()))
							return (objXmlNode.getNodeType() == org.w3c.dom.Node.ATTRIBUTE_NODE
								&& subjXmlNode.getAttributes().getNamedItemNS(
										objXmlNode.lookupNamespaceURI(objXmlNode.getPrefix()),
										objXmlNode.getLocalName()) != null);
						if (p.equals(xml.ownerDocument.asNode()))
							return subjXmlNode.getOwnerDocument().equals(objXmlNode);
						return false;

					}
				}
					
			}
		}

		@Override
		public void delete(Triple t) throws DeleteDeniedException {
			throw new DeleteDeniedException("XML DOM derived read only graph");
		}

		@Override
		public boolean dependsOn(Graph other) {
			return false;
		}

		@Override
		public ExtendedIterator<Triple> find(TripleMatch m) {
			Triple triple = m.asTriple();
			return find( triple.getSubject(), triple.getPredicate(), triple.getObject() );
		}

		@Override
		public ExtendedIterator<Triple> find(final Node s, final Node p, final Node o) {
			if (s == null || !s.isConcrete()) {
				
				if (p == null || !p.isConcrete()) {

					if (o == null || !o.isConcrete()) {

						// Schema: * * *
						Iterator<org.w3c.dom.Node> documentNodes = listNodes();
						ExtendedIterator<Triple> resultIterator = new NullIterator<Triple>();
						while ( documentNodes.hasNext() ) {
							resultIterator =
									resultIterator.andThen( find( mapXmlNode2GraphNode( documentNodes.next() ), p, o ) );
						}
						return resultIterator;

					} else  {
						
						// Schema: * * o
						ExtendedIterator<Triple> resultIterator = new NullIterator<Triple>();
						Iterator<Node> availableProps = xml.nodeProperties();
						while (availableProps.hasNext()) {
							resultIterator = resultIterator.andThen( find( s, availableProps.next(), o ) );
						}
						return resultIterator;

					}
					
				} else {

					if (o == null || !o.isConcrete()) {
						
						// Schema: * p *
						Iterator<org.w3c.dom.Node> documentNodes = listNodes();
						ExtendedIterator<Triple> resultIterator = new NullIterator<Triple>();
						while ( documentNodes.hasNext() ) {
							resultIterator =
									resultIterator.andThen( find( mapXmlNode2GraphNode( documentNodes.next() ), p, o ) );
						}
						return resultIterator;

					} else {

						// Schema: * p o
						ExtendedIterator<org.w3c.dom.Node> subjIterator = null;
						if ( p.equals(xml.namespace.asNode()) && o.isURI())
							subjIterator =
									o.isLiteral()
										? Utils.listSubtreeNodes( document, null, null, o.getURI(), null )
										: emptyNodeIterator;
						else if ( p.equals(xml.nodeName.asNode()) )
							subjIterator =
									o.isLiteral()
										? Utils.listSubtreeNodes( document, null, o.getLiteralLexicalForm(), null, null )
										: emptyNodeIterator;
						else if ( p.equals(xml.nodeValue.asNode()) )
							subjIterator =
									o.isLiteral()
										? Utils.listSubtreeNodes( document, null, null, null, o.getLiteralLexicalForm() )
										: emptyNodeIterator;
						else if ( p.equals(xml.nodeType.asNode()) ) {
							if (o.isURI()) {
								short nodeType = Utils.mapResource2nodeType( ResourceFactory.createResource(o.getURI()) );
								if (nodeType != -1)
									subjIterator = Utils.listSubtreeNodes( document, new Short(nodeType), null, null, null );
								else
									subjIterator = emptyNodeIterator;
							} else
								subjIterator = emptyNodeIterator;
						}
						if (subjIterator != null) {
							return subjIterator.mapWith(new Map1<org.w3c.dom.Node, Triple>() {
								@Override
								public Triple map1(org.w3c.dom.Node currNode) {
									return new Triple( mapXmlNode2GraphNode(currNode), p, o );
								}
							});
						}
						org.w3c.dom.Node objXmlNode = mapGraphNode2XmlNode(o);
						if ( objXmlNode == null )
							return emptyTripleIterator;
						final NodeList subjList = xml.getRevNodeListProperty(objXmlNode, p);
						return new NiceIterator<Triple>() {
							private int subjectIndex = 0;
							@Override
							public boolean hasNext() {
								return subjectIndex < subjList.getLength();
							}
							@Override
							public Triple next() {
								return new Triple(s,p,mapXmlNode2GraphNode(subjList.item(subjectIndex++)));
							}
						};

					}
						
				}
				
			} else {
			
				if (p == null || !p.isConcrete()) {

					// Schema: s * X
					org.w3c.dom.Node subjXmlNode = mapGraphNode2XmlNode(s);
					if ( subjXmlNode == null )
						return emptyTripleIterator;
					ExtendedIterator<Triple> resultIterator = new NullIterator<Triple>();
					Iterator<Node> availableProps = xml.nodeProperties();
					while (availableProps.hasNext()) {
						resultIterator = resultIterator.andThen( find( s, availableProps.next(), o ) );
					}
					return resultIterator;

				} else {
						
					if (o == null || !o.isConcrete()) {

						// Schema: s p *
						org.w3c.dom.Node subjXmlNode = mapGraphNode2XmlNode(s);
						if (subjXmlNode == null)
							return emptyTripleIterator;
						Node singleObjectNode = null;
						String singleObjectString = null;
						if ( p.equals(xml.nodeName.asNode()) )
							singleObjectString = subjXmlNode.getNodeName();
						else if ( p.equals(xml.nodeValue.asNode()) )
							singleObjectString = subjXmlNode.getNodeValue();
						if (singleObjectString != null)
							singleObjectNode = Node.createLiteral(singleObjectString);
						else {
							if ( p.equals(xml.namespace.asNode()) ) {
								singleObjectNode =
										subjXmlNode.getNamespaceURI() == null
											? null
											: Node.createURI(subjXmlNode.getNamespaceURI());
							}
							else if ( p.equals(xml.nodeType.asNode()) ) {
								singleObjectNode = Utils.mapNodeType2resource(subjXmlNode.getNodeType()).asNode();
								if (singleObjectNode == null)
									return emptyTripleIterator;
							} 
						}
						if (singleObjectNode != null)
							return new SingletonIterator<Triple>(new Triple(s, p, singleObjectNode));
						else {
							final NodeList objList = xml.getNodeListProperty(subjXmlNode, p);
							return new NiceIterator<Triple>() {
								private int objectIndex = 0;
								@Override
								public boolean hasNext() {
									return objectIndex < objList.getLength();
								}
								@Override
								public Triple next() {
									if (!hasNext())
										throw new NoSuchElementException();
									return new Triple(s,p,mapXmlNode2GraphNode(objList.item(objectIndex++)));
								}
							};
						}

					} else  {
						// Schema: s p o
						if ( contains(s, p, o) )
							return new SingletonIterator<Triple>(new Triple(s, p, o));
						else
							return emptyTripleIterator;
					}
				}
					
			}
		}

		@Override
		public BulkUpdateHandler getBulkUpdateHandler() {
			// Should be ok to return null for a readonly graph
			return null;
		}

		@Override
		public Capabilities getCapabilities() {
			return new Capabilities() {
				
				@Override
				public boolean sizeAccurate() {
					return false;
				}
				
				@Override
				public boolean iteratorRemoveAllowed() {
					return false;
				}
				
				@Override
				public boolean handlesLiteralTyping() {
					return true;
				}
				
				@Override
				public boolean findContractSafe() {
					// ???
					return false;
				}
				
				@Override
				public boolean deleteAllowed(boolean everyTriple) {
					return false;
				}
				
				@Override
				public boolean deleteAllowed() {
					return false;
				}
				
				@Override
				public boolean canBeEmpty() {
					// there should be at least the root xml element
					return false;
				}
				
				@Override
				public boolean addAllowed(boolean everyTriple) {
					return false;
				}
				
				@Override
				public boolean addAllowed() {
					return false;
				}
				
			};
		}
		
		private GraphEventManager eventManager = new SimpleEventManager(this);

		@Override
		public GraphEventManager getEventManager() {
			return eventManager;
		}
		
		private PrefixMapping prefixMapping = new PrefixMappingImpl();

		@Override
		public PrefixMapping getPrefixMapping() {
			return prefixMapping;
		}
		
		Reifier reifier = new Reifier2(this);

		@Override
		public Reifier getReifier() {
			return reifier;
		}

		GraphStatisticsHandler graphStatisticsHandler =
				new GraphStatisticsHandler() {
					@Override
					public long getStatistic(Node S, Node P, Node O) {
						return -1;
					}
				};
		
		@Override
		public GraphStatisticsHandler getStatisticsHandler() {
			return graphStatisticsHandler;
		}

		TransactionHandler transactionHandler = new SimpleTransactionHandler();
		
		@Override
		public TransactionHandler getTransactionHandler() {
			return transactionHandler;
		}

		@Override
		public boolean isClosed() {
			return closed;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public boolean isIsomorphicWith(Graph g) {
			if (g == this)
				return true;
			if (g instanceof EncodedDocument) 
				return document.equals(((EncodedDocument) g).document);
			ExtendedIterator<Triple> extTriples = g.find(null, null, null);
			while (extTriples.hasNext())
				if (!contains(extTriples.next()))
					return false;
			ExtendedIterator<Triple> intTriples = find(null, null, null);
			while (intTriples.hasNext())
				if (!g.contains(intTriples.next()))
					return false;
			return true;
		}

		QueryHandler queryHandler = new SimpleQueryHandler(this);
		
		@Override
		public QueryHandler queryHandler() {
			return queryHandler;
		}

		@Override
		public int size() {
			/* By graph interface contract, implementors are
			 * forced just to give a lower bound, we return
			 * 0 for simplicity. 
			 */
			return 0;
		}

		@Override
		public void add(Triple t) throws AddDeniedException {
			throw new AddDeniedException("XML DOM derived read only graph");
		}
	};

	/**
	 * Encode.
	 *
	 * @param document the document
	 * @return the graph
	 */
	public static Graph encode(Document document) {
		return new EncodedDocument(document);
	}

	public static Graph encode(Document document, String rootUri) {
		return new EncodedDocument(document, rootUri);
	}

	public static void developInRDF(Graph graph) {
		Model model = ModelFactory.createModelForGraph(graph);
		Iterator<Resource> xmlResources =
				model.listResourcesWithProperty(RDF.type, xml.Document)
				.filterKeep( new Filter<Resource>() {
					@Override
					public boolean accept(Resource res) {
						StmtIterator stmtIterator = res.listProperties();
						boolean textProp = false;
						while (stmtIterator.hasNext()) {
							Property prop = stmtIterator.next().getPredicate();
							if (prop.equals(xml.text)) {
								// TODO: check for single literal string value of xml.text
								textProp = true;
							}
							else if (!prop.equals(RDF.type))
								return false;
						}
						return textProp;
					}
				});
		//if (!xmlResources.hasNext())
		//	System.out.println("Empty Iterator!");
		
		List<Resource> xmlResourcesList = new Vector<Resource>();
		while (xmlResources.hasNext())
			xmlResourcesList.add(xmlResources.next());
		xmlResources = xmlResourcesList.iterator();
		while (xmlResources.hasNext()) {
			Resource xmlRes = xmlResources.next();
			String xmlString = xmlRes.getRequiredProperty(xml.text).getString();
			InputSource xmlInputSource = new InputSource(new StringReader(xmlString));
			String rootUri = null;
			Node rootNode = null;
			while (rootUri == null || graph.contains(rootNode, Node.ANY, Node.ANY) || graph.contains(Node.ANY, Node.ANY, rootNode) ) {
				rootUri = getRandomString();
				rootNode = Node.createURI(rootUri);
			}
			Document newDoc;
			try {
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
				docBuilderFactory.setCoalescing(true);
				//docBuilderFactory.setFeature("XML 2.0", true);
				docBuilderFactory.setNamespaceAware(true);
				newDoc = docBuilderFactory.newDocumentBuilder().parse(xmlInputSource);
				Graph newGraph = encode(newDoc, rootUri);
				graph.getBulkUpdateHandler().add(newGraph);
				
				Iterator<Triple> tripleIterator =
						graph.find(rootNode, null, null).andThen(graph.find(null, null, rootNode));
				List<Triple> tripleList = new Vector<Triple>();
				while (tripleIterator.hasNext()) {
					Triple triple = tripleIterator.next();
					tripleList.add(triple);
				}
				tripleIterator = tripleList.iterator();
				while (tripleIterator.hasNext()) {
					Triple triple = tripleIterator.next();
					graph.add(
							new Triple(
									triple.getSubject().equals(rootNode) ? xmlRes.asNode() : triple.getSubject(),
									triple.getPredicate(),
									triple.getObject().equals(rootNode) ? xmlRes.asNode() : triple.getObject()
								));
					graph.delete(triple);
				}
//SparqlJenaQuery query = new SparqlJenaQuery(queryString);
				//query.addRootedGraph(graph, xmlRes.asNode());
			} catch (SAXException e) {
			} catch (IOException e) {
			} catch (ParserConfigurationException e) {
			}
		}

	}

	//public static final String URI = "http://www.swows.org/datatypes/sparql/jena";
	//public static final Resource namespace = ResourceFactory.createResource("http://www.swows.org/datatypes/sparql/jena");
	public static final int RANDOM_HEX_SIZE = 8;
	private static final Random random = new Random();
	
	private static char nibble2hexDigit(byte input) {
		input |= 15;
		switch(input) {
		case 10:
			return 'a';
		case 11:
			return 'b';
		case 12:
			return 'c';
		case 13:
			return 'd';
		case 14:
			return 'e';
		case 15:
			return 'f';
		default:
			return Byte.toString(input).charAt(0);
		}
	}
	
	private static String getRandomString() {
		byte randomBytes[] = new byte[RANDOM_HEX_SIZE * 2];
		random.nextBytes(randomBytes);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < RANDOM_HEX_SIZE * 2; i++) {
			sb.append( nibble2hexDigit( (byte) (randomBytes[i] >>> 8) ) );
			sb.append( nibble2hexDigit( randomBytes[i] ) );
		}
		return sb.toString();
	}

}

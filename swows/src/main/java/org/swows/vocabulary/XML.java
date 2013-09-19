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
package org.swows.vocabulary;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.swows.xmlinrdf.Utils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO: Auto-generated Javadoc
/**
 * The Class xml.
 */
public class XML {

	private static final String uri = "http://www.swows.org/2013/07/xml-dom#";

	/**
	 * Gets the uRI.
	 *
	 * @return the uRI
	 */
	public static String getURI() {
		return uri;
	}

    /**
     * Resource.
     *
     * @param local the local
     * @return the resource
     */
    protected static final Resource resource( String local ) {
    	return ResourceFactory.createResource( uri + local );
    }

    /**
     * Property.
     *
     * @param local the local
     * @return the property
     */
    protected static final Property property( String local ) {
    	return ResourceFactory.createProperty( uri, local );
    }

    /** The Constant Node. */
    public static final Resource Node = resource( "Node" );

    /** The Constant Attr. */
    public static final Resource Attr = resource( "Attr" );
    
    /** The Constant Comment. */
    public static final Resource Comment = resource( "Comment" );
    
    /** The Constant CDATASection. */
    public static final Resource CDATASection = resource( "CDATASection" );
    
    /** The Constant Document. */
    public static final Resource Document = resource( "Document" );
    
    /** The Constant DocumentFragment. */
    public static final Resource DocumentFragment = resource( "DocumentFragment" );
    
    /** The Constant DocumentType. */
    public static final Resource DocumentType = resource( "DocumentType" );
    
    /** The Constant Element. */
    public static final Resource Element = resource( "Element" );
    
    /** The Constant Entity. */
    public static final Resource Entity = resource( "Entity" );
    
    /** The Constant EntityReference. */
    public static final Resource EntityReference = resource( "EntityReference" );
    
    /** The Constant Notation. */
    public static final Resource Notation = resource( "Notation" );
    
    /** The Constant ProcessingInstruction. */
    public static final Resource ProcessingInstruction = resource( "ProcessingInstruction" );
    
    /** The Constant Text. */
    public static final Resource Text = resource( "Text" );

    public static final Resource Descending = resource( "Descending" );
    public static final Resource Ascending = resource( "Ascending" );

    /** The Constant hasChild. */
    public static final Property hasChild = property( "hasChild" );
    
    /** The Constant firstChild. */
    public static final Property firstChild = property( "firstChild" );
    
    /** The Constant lastChild. */
    public static final Property lastChild = property( "lastChild" );
    
    public static final Property orderKey = property( "orderKey" );
    public static final Property childrenOrderType = property( "childrenOrderType" );

    /** The Constant nextSibling. */
    public static final Property nextSibling = property( "nextSibling" );
    
    /** The Constant nodeName. */
    public static final Property nodeName = property( "nodeName" );
    
    /** The Constant nodeValue. */
    public static final Property nodeValue = property( "nodeValue" );
    
    /** The Constant nodeType. */
//    public static final Property nodeType = property( "nodeType" );
    public static final Property nodeType = RDF.type;
    
    /** The Constant ownerDocument. */
    public static final Property ownerDocument = property( "ownerDocument" );
    
    /** The Constant parentNode. */
    public static final Property parentNode = property( "parentNode" );
    
    /** The Constant previousSibling. */
    public static final Property previousSibling = property( "previousSibling" );
    
    /** The Constant hasAttribute. */
    public static final Property hasAttribute = property( "hasAttribute" );
    //public static final Property name = property( "name" );
    /** The Constant namespace. */
    public static final Property namespace = property( "namespace" );
    
    public static final Property text = property( "text" );

    public static final Property listenedEventType = property( "listenedEventType" );
    public static final Property documentURI = property( "documentURI" );

    public static final Property document = property( "document" );

    public static final Resource AttrType = resource( "AttrType" );

    public static final List<Property> allProperties = new Vector<Property>();
    
    static {
    	allProperties.add(nodeName);
    	allProperties.add(nodeValue);
    	allProperties.add(nodeType);
    	allProperties.add(namespace);
    	allProperties.add(hasAttribute);
    	allProperties.add(hasChild);
    	allProperties.add(firstChild);
    	allProperties.add(lastChild);
    	allProperties.add(previousSibling);
    	allProperties.add(nextSibling);
    	allProperties.add(parentNode);
    	allProperties.add(ownerDocument);
    	allProperties.add(documentURI);
    	allProperties.add(listenedEventType);
    }
    
    public static final boolean isNodeNeededProperty(com.hp.hpl.jena.graph.Node propNode) {
    	return ( 	propNode.equals(nodeName.asNode())
    				|| propNode.equals(nodeType.asNode())
    			);
    }

    /*
    public static final Iterator<Property> nodeProperties() {
    	//Iterator<Property> propertyIterator = allProperties.iterator();
    	return allProperties.iterator();
    }
    */
    
    public static final Iterator<com.hp.hpl.jena.graph.Node> nodeProperties() {
    	final Iterator<Property> propertyIterator = allProperties.iterator();
    	return new Iterator<com.hp.hpl.jena.graph.Node>() {
			@Override
			public boolean hasNext() {
				return propertyIterator.hasNext();
			}
			@Override
			public com.hp.hpl.jena.graph.Node next() {
				return propertyIterator.next().asNode();
			}
			@Override
			public void remove() {
				propertyIterator.remove();
			}
		};
    }
    
    public static final boolean isNodeProperty(com.hp.hpl.jena.graph.Node propNode) {
    	return ( 	propNode.equals(hasChild.asNode())
    				|| propNode.equals(firstChild.asNode())
    				|| propNode.equals(lastChild.asNode())
    				|| propNode.equals(previousSibling.asNode())
    				|| propNode.equals(nextSibling.asNode())
    				|| propNode.equals(nodeName.asNode())
    				|| propNode.equals(nodeValue.asNode())
    				|| propNode.equals(nodeType.asNode())
    				|| propNode.equals(ownerDocument.asNode())
    				|| propNode.equals(parentNode.asNode())
    				|| propNode.equals(previousSibling.asNode())
    				|| propNode.equals(hasAttribute.asNode())
    				|| propNode.equals(namespace.asNode())
    				|| propNode.equals(documentURI.asNode())
    				|| propNode.equals(listenedEventType.asNode())
    			);
    }
    
    public static final boolean isNodeToNodeProperty(com.hp.hpl.jena.graph.Node propNode) {
    	return ( 	propNode.equals(hasChild.asNode())
    				|| propNode.equals(firstChild.asNode())
    				|| propNode.equals(lastChild.asNode())
    				|| propNode.equals(previousSibling.asNode())
    				|| propNode.equals(nextSibling.asNode())
    				|| propNode.equals(ownerDocument.asNode())
    				|| propNode.equals(parentNode.asNode())
    				|| propNode.equals(previousSibling.asNode())
    			);
    }
    
    private static final NodeList emptyNodeList =
    		new NodeList() {
    			@Override
    			public Node item(int index) {
    				return null;
    			}
    			@Override
    			public int getLength() {
    				return 0;
    			}
			};
    
	private static class SingleNodeList implements NodeList {
		private Node node;
		public SingleNodeList(Node node) {
			this.node = node;
		}
		@Override
		public int getLength() {
			return 1;
		}
		@Override
		public Node item(int index) {
			return (index == 0) ? node : null;
		}
	}

	public static final org.w3c.dom.Node getNodeProperty(org.w3c.dom.Node node, com.hp.hpl.jena.graph.Node propNode) {
		if (propNode.equals(XML.firstChild.asNode()))
			return node.getFirstChild();
		if (propNode.equals(XML.lastChild.asNode()))
			return node.getLastChild();
		if (propNode.equals(XML.previousSibling.asNode()))
			return node.getPreviousSibling();
		if (propNode.equals(XML.nextSibling.asNode()))
			return node.getNextSibling();
		if (propNode.equals(XML.parentNode.asNode()))
			return node.getParentNode();
		if (propNode.equals(XML.ownerDocument.asNode()))
			return node.getOwnerDocument();
    	return null;
    }
    
    public static final org.w3c.dom.Node getRevNodeProperty(org.w3c.dom.Node node, com.hp.hpl.jena.graph.Node propNode) {
		if (propNode.equals(XML.firstChild.asNode()))
			return (node.getPreviousSibling() == null) ? node.getParentNode() : null;
		if (propNode.equals(XML.lastChild.asNode()))
			return (node.getNextSibling() == null) ? node.getParentNode() : null;
		if (propNode.equals(XML.previousSibling.asNode()))
			return node.getNextSibling();
		if (propNode.equals(XML.nextSibling.asNode()))
			return node.getPreviousSibling();
		if (propNode.equals(XML.hasChild.asNode()))
			return node.getParentNode();
		if (propNode.equals(XML.hasAttribute.asNode()))
			return (node.getNodeType() == org.w3c.dom.Node.ATTRIBUTE_NODE)
						? ( (org.w3c.dom.Attr) node ).getOwnerElement()
						: null;
    	return null;
    }
    
    public static final org.w3c.dom.NodeList getNodeListProperty(final org.w3c.dom.Node node, com.hp.hpl.jena.graph.Node propNode) {
		if (propNode.equals(XML.hasChild.asNode()))
			return node.getChildNodes();
		if (propNode.equals(XML.hasAttribute.asNode())) {
			final NamedNodeMap attrMap = node.getAttributes();
			if (attrMap == null)
				return emptyNodeList;
			return new NodeList() {
				@Override
				public Node item(int index) {
					return attrMap.item(index);
				}
				@Override
				public int getLength() {
					return attrMap.getLength();
				}
			};
		}
		final Node singleNode = getNodeProperty(node, propNode);
		if (singleNode == null) {
			return emptyNodeList;
		}
		return new SingleNodeList(singleNode);
    }

    public static final org.w3c.dom.NodeList getRevNodeListProperty(final org.w3c.dom.Node node, com.hp.hpl.jena.graph.Node propNode) {
		if (propNode.equals(XML.parentNode.asNode()))
			return node.getChildNodes();
		if (propNode.equals(XML.ownerDocument.asNode())) {
			if (node.getNodeType() == org.w3c.dom.Node.DOCUMENT_NODE) {
				final Iterator<Node> docNodes = Utils.listSubtreeNodes(((org.w3c.dom.Document) node).getDocumentElement());
				return new NodeList() {
					Vector<Node> nodeVector = new Vector<Node>();
					{
						while (docNodes.hasNext())
							nodeVector.add( docNodes.next() );
					}
					@Override
					public Node item(int index) {
						try {
							return nodeVector.get(index);
						} catch (ArrayIndexOutOfBoundsException e) {
							return null;
						}
					}
					@Override
					public int getLength() {
						return nodeVector.size();
					}
				};
			}
			return emptyNodeList;
		}
		final Node singleNode = getRevNodeProperty(node, propNode);
		if (singleNode == null) 
			return emptyNodeList;
		return new SingleNodeList(singleNode);
    }

}

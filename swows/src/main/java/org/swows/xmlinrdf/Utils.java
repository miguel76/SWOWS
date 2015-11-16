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
package org.swows.xmlinrdf;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.NullIterator;
import org.apache.jena.util.iterator.SingletonIterator;
import org.swows.vocabulary.XML;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class Utils {

	static Resource mapNodeType2resource(short nodeType) {
		switch (nodeType) {
		case org.w3c.dom.Node.ATTRIBUTE_NODE:
			return XML.Attr;
		case org.w3c.dom.Node.CDATA_SECTION_NODE:
			return XML.CDATASection;
		case org.w3c.dom.Node.COMMENT_NODE:
			return XML.Comment;
		case org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE:
			return XML.DocumentFragment;
		case org.w3c.dom.Node.DOCUMENT_NODE:
			return XML.Document;
		case org.w3c.dom.Node.DOCUMENT_TYPE_NODE:
			return XML.DocumentType;
		case org.w3c.dom.Node.ELEMENT_NODE:
			return XML.Element;
		case org.w3c.dom.Node.ENTITY_NODE:
			return XML.Entity;
		case org.w3c.dom.Node.ENTITY_REFERENCE_NODE:
			return XML.EntityReference;
		case org.w3c.dom.Node.NOTATION_NODE:
			return XML.Notation;
		case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
			return XML.ProcessingInstruction;
		case org.w3c.dom.Node.TEXT_NODE:
			return XML.Text;
		default:
			return null;
		}
	}

	static short mapResource2nodeType(Resource typeRes) {
		if (typeRes.equals(XML.Attr))
			return org.w3c.dom.Node.ATTRIBUTE_NODE;
		else if (typeRes.equals(XML.CDATASection))
			return org.w3c.dom.Node.CDATA_SECTION_NODE;
		else if (typeRes.equals(XML.Comment))
			return org.w3c.dom.Node.COMMENT_NODE;
		else if (typeRes.equals(XML.DocumentFragment))
			return org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE;
		else if (typeRes.equals(XML.Document))
			return org.w3c.dom.Node.DOCUMENT_NODE;
		else if (typeRes.equals(XML.DocumentType))
			return org.w3c.dom.Node.DOCUMENT_TYPE_NODE;
		else if (typeRes.equals(XML.Element))
			return org.w3c.dom.Node.ELEMENT_NODE;
		else if (typeRes.equals(XML.Entity))
			return org.w3c.dom.Node.ENTITY_NODE;
		else if (typeRes.equals(XML.EntityReference))
			return org.w3c.dom.Node.ENTITY_REFERENCE_NODE;
		else if (typeRes.equals(XML.Notation))
			return org.w3c.dom.Node.NOTATION_NODE;
		else if (typeRes.equals(XML.ProcessingInstruction))
			return org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE;
		else if (typeRes.equals(XML.Text))
			return org.w3c.dom.Node.TEXT_NODE;
		else
			return -1;
	}

	public static ExtendedIterator<org.w3c.dom.Node> listSubtreeNodes(org.w3c.dom.Node node) {
		ExtendedIterator<org.w3c.dom.Node> subtreeNodes = new NullIterator<org.w3c.dom.Node>(); 
		subtreeNodes = subtreeNodes.andThen(new SingletonIterator<org.w3c.dom.Node>(node));
		NamedNodeMap attrs = node.getAttributes();
		if (attrs != null)
			for (int attrIndex = 0; attrIndex < attrs.getLength(); attrIndex++ )
				subtreeNodes = subtreeNodes.andThen(listSubtreeNodes(attrs.item(attrIndex)));
		NodeList childNodes = node.getChildNodes();
		if (childNodes != null)
			for (int childIndex = 0; childIndex < childNodes.getLength(); childIndex++ )
				subtreeNodes = subtreeNodes.andThen(listSubtreeNodes(childNodes.item(childIndex)));
		return subtreeNodes;
	}

	public static ExtendedIterator<org.w3c.dom.Node> listSubtreeNodes(
			org.w3c.dom.Node node, Short type,
			String localName, String namespace, String value ) {
		ExtendedIterator<org.w3c.dom.Node> subtreeNodes = new NullIterator<org.w3c.dom.Node>(); 
		if ( 	( type == null || type.shortValue() == node.getNodeType() )
				&& ( localName == null || localName.equals( node.getLocalName() ) ) 
				&& ( namespace == null || namespace.equals( node.lookupNamespaceURI(node.getPrefix()) ) ) 
				&& ( value == null || value.equals( node.getNodeValue() ) ) )
			subtreeNodes = subtreeNodes.andThen(new SingletonIterator<org.w3c.dom.Node>(node));
		NamedNodeMap attrs = node.getAttributes();
		if (attrs != null)
			for (int attrIndex = 0; attrIndex < attrs.getLength(); attrIndex++ )
				subtreeNodes = subtreeNodes.andThen(listSubtreeNodes(attrs.item(attrIndex)));
		NodeList childNodes = node.getChildNodes();
		if (childNodes != null)
			for (int childIndex = 0; childIndex < childNodes.getLength(); childIndex++ )
				subtreeNodes = subtreeNodes.andThen(listSubtreeNodes(childNodes.item(childIndex)));
		return subtreeNodes;
	}

	static boolean containsNodeType(org.w3c.dom.Node node, short nodeType) {
		if (	node.getNodeType() == nodeType
				|| (nodeType == org.w3c.dom.Node.ATTRIBUTE_NODE && node.getAttributes().getLength() > 0) )
			return true;
		else {
			NodeList childNodes = node.getChildNodes();
			for (int childIndex = 0; childIndex < childNodes.getLength(); childIndex++ )
				if (containsNodeType(childNodes.item(childIndex), nodeType))
					return true;
		}
		return false;
	}

	static boolean containsNodeValue(org.w3c.dom.Node node, String nodeValue) {
		if ( node.getNodeValue() == nodeValue )
			return true;
		else {
			NamedNodeMap attrs = node.getAttributes();
			if (attrs != null)
				for (int attrIndex = 0; attrIndex < attrs.getLength(); attrIndex++ )
					if (containsNodeValue(attrs.item(attrIndex), nodeValue))
						return true;
			NodeList childNodes = node.getChildNodes();
			if (childNodes != null)
				for (int childIndex = 0; childIndex < childNodes.getLength(); childIndex++ )
					if (containsNodeValue(childNodes.item(childIndex), nodeValue))
						return true;
		}
		return false;
	}

	static boolean containsNodeValue(org.w3c.dom.Node node) {
		if ( node.getNodeValue() != null )
			return true;
		NamedNodeMap attrs = node.getAttributes();
		if (attrs != null && attrs.getLength() > 0 )
			return true;
		NodeList childNodes = node.getChildNodes();
		if (childNodes != null)
			for (int childIndex = 0; childIndex < childNodes.getLength(); childIndex++ )
				if (containsNodeValue(childNodes.item(childIndex)))
					return true;
		return false;
	}

	static boolean containsAttributes(org.w3c.dom.Node node) {
		NamedNodeMap attrs = node.getAttributes();
		if (attrs != null && attrs.getLength() > 0 )
			return true;
		NodeList childNodes = node.getChildNodes();
		if (childNodes != null)
			for (int childIndex = 0; childIndex < childNodes.getLength(); childIndex++ )
				if (containsAttributes(childNodes.item(childIndex)))
					return true;
		return false;
	}

	static boolean containsSiblings(org.w3c.dom.Node node) {
		NodeList childNodes = node.getChildNodes();
		if ( childNodes != null )
			if ( childNodes.getLength() > 1 )
				return true;
			else
				if (containsSiblings(childNodes.item(0)))
					return true;
		return false;
	}

}

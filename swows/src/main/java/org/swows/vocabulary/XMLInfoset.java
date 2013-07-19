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

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLInfoset.
 */
public class XMLInfoset {

	private static final String uri = "http://www.w3.org/2001/04/infoset#";

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

    public static final Resource AttributeType = resource( "AttributeType" );
    public static final Resource Boolean = resource( "Boolean" );
    public static final Resource Standalone = resource( "Standalone" );
    public static final Resource Unknown = resource( "Unknown" );
    public static final Resource NoValue = resource( "NoValue" );
    public static final Resource Literal = resource( "Literal" );
    public static final Resource Integer = resource( "Integer" );
    
    public static final Resource InfoItem = resource( "InfoItem" );
    public static final Resource Document = resource( "Document" );
    public static final Resource Element = resource( "Element" );
    public static final Resource Attribute = resource( "Attribute" );
    public static final Resource ProcessingInstruction = resource( "ProcessingInstruction" );
    public static final Resource Character = resource( "Character" );
    public static final Resource UnexpandedEntityReference = resource( "UnexpandedEntityReference" );
    public static final Resource Comment = resource( "Comment" );
    public static final Resource DocumentTypeDeclaration = resource( "DocumentTypeDeclaration" );
    public static final Resource UnparsedEntity = resource( "UnparsedEntity" );
    public static final Resource Notation = resource( "Notation" );
    public static final Resource Namespace = resource( "Namespace" );
    
    public static final Resource InfoItemSeq = resource( "InfoItemSeq" );
    public static final Resource References = resource( "References" );
    
    public static final Property allDeclarationsProcessed = property( "allDeclarationsProcessed" );
    public static final Property attributes = property( "attributes" );
    public static final Property attributeType = property( "attributeType" );
    public static final Property baseURI = property( "baseURI" );
    public static final Property characterCode = property( "characterCode" );
    public static final Property characterEncodingScheme = property( "characterEncodingScheme" );
    public static final Property children = property( "children" );
    public static final Property content = property( "content" );
    public static final Property namespaceAttributes = property( "namespaceAttributes" );
    public static final Property declarationBaseURI = property( "declarationBaseURI" );
    public static final Property documentElement = property( "documentElement" );
    public static final Property elementContentWhitespace = property( "elementContentWhitespace" );
    public static final Property unparsedEntities = property( "unparsedEntities" );
    public static final Property inScopeNamespaces = property( "inScopeNamespaces" );
    public static final Property localName = property( "localName" );
    public static final Property name = property( "name" );
    public static final Property namespaceName = property( "namespaceName" );
    public static final Property normalizedValue = property( "normalizedValue" );
    public static final Property notation = property( "notation" );
    public static final Property notationName = property( "notationName" );
    public static final Property notations = property( "notations" );
    public static final Property ownerElement = property( "ownerElement" );
    public static final Property parent = property( "parent" );
    public static final Property prefix = property( "prefix" );
    public static final Property publicIdentifier = property( "publicIdentifier" );
    public static final Property references = property( "references" );
    public static final Property specified = property( "specified" );
    public static final Property standalone = property( "standalone" );
    public static final Property systemIdentifier = property( "systemIdentifier" );
    public static final Property target = property( "target" );
    public static final Property version = property( "version" );

}

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
public class MouseEvents {

	private static final String uri = "http://www.swows.org/DOMEvents/MouseEvents#";

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

    /** The Constant hasChild. */
    public static final Property hasChild = property( "hasChild" );
    
    /** The Constant firstChild. */
    public static final Property firstChild = property( "firstChild" );
    
    /** The Constant lastChild. */
    public static final Property lastChild = property( "lastChild" );
    
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

    public static final List<Property> allProperties = new Vector<Property>();
    
}

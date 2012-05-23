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

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class xml.
 */
public class DOMEvents {

	private static final String uri = "http://www.swows.org/DOM/Events#";
	private static final String instanceUri = "http://www.swows.org/DOM/Events/Instance#";

	/**
	 * Gets the uRI.
	 *
	 * @return the uRI
	 */
	public static String getURI() {
		return uri;
	}

	public static String getInstanceURI() {
		return instanceUri;
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

    public static final Resource Event = resource( "Event" );
    public static final Resource UIEvent = resource( "UIEvent" );
    public static final Resource MouseEvent = resource( "MouseEvent" );
    public static final Resource MutationEvent = resource( "MutationEvent" );

    public static final Property target = property( "target" );
    public static final Property currentTarget = property( "currentTarget" );
    public static final Property timeStamp = property( "timeStamp" );
    public static final Property detail = property( "detail" );

    public static final Property screenX = property( "screenX" );
    public static final Property screenY = property( "screenY" );
    public static final Property clientX = property( "clientX" );
    public static final Property clientY = property( "clientY" );
    public static final Property ctrlKey = property( "ctrlKey" );
    public static final Property shiftKey = property( "shiftKey" );
    public static final Property altKey = property( "altKey" );
    public static final Property metaKey = property( "metaKey" );
    public static final Property button = property( "button" );
    public static final Property relatedTarget = property( "relatedTarget" );
    
    public static final Property relatedNode = property( "relatedNode" );
    public static final Property prevValue = property( "prevValue" );
    public static final Property newValue = property( "newValue" );
    public static final Property attrName = property( "attrName" );
    public static final Property attrChange = property( "attrChange" );

}

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

/**
 * The Class TUIO maintains {@link com.hp.hpl.jena.rdf.model.Resource}
 * and {@link com.hp.hpl.jena.rdf.model.Property} instances for
 * the TUIO ontology
 * (<a href="http://www.swows.org/tuio">http://www.swows.org/tuio</a>).
 */
public class TUIO {

	private static final String uri = "http://www.swows.org/tuio#";

	private static final String instance_uri = "http://www.swows.org/tuio/instance#";

	/**
	 * Gets the ontology URI.
	 *
	 * @return "http://www.swows.org/tuio#"
	 */
	public static String getURI() {
		return uri;
	}

	public static String getInstanceURI() {
		return instance_uri;
	}

    /**
     * Creates a new resource based on the TUIO uri and a local name
     *
     * @param localName the local name of the resource
     * @return the created resource
     */
    protected static final Resource resource( String localName ) {
    	return ResourceFactory.createResource( uri + localName );
    }

    /**
     * Creates a new property based on the TUIO uri and a local name.
     *
     * @param localName the local name of the property
     * @return the created property
     */
    protected static final Property property( String localName ) {
    	return ResourceFactory.createProperty( uri, localName );
    }

    public final static Resource Tracked = resource("Tracked");
    public final static Resource Object = resource("Object");
    public final static Resource Cursor = resource("Cursor");
    public final static Resource Blob = resource("Blob");
    public final static Resource Source = resource("Source");
    public final static Resource Point2D = resource("Point2D");
    public final static Resource Point3D = resource("Point3D");
    public final static Resource Angle2D = resource("Angle2D");
    public final static Resource Angle3D = resource("Angle3D");
    
    public final static Resource defaultSource = resource("defaultSource");

    public final static Property isOn = property("isOn");
    public final static Property source = property("source");
    public final static Property id = property("id");
    public final static Property markerId = property("markerId");
    public final static Property position = property("position");
    public final static Property direction = property("direction");
    public final static Property angle = property("angle");
    public final static Property x = property("x");
    public final static Property y = property("y");
    public final static Property z = property("z");
    public final static Property a = property("a");
    public final static Property b = property("b");
    public final static Property c = property("c");
    public final static Property creationTime = property("creationTime");
    public final static Property updateTime = property("updateTime");

}

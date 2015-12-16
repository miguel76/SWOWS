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

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * The Class TUIO maintains {@link org.apache.jena.rdf.model.Resource}
 * and {@link org.apache.jena.rdf.model.Property} instances for
 * the TUIO ontology
 * (<a href="http://www.swows.org/tuio">http://www.swows.org/tuio</a>).
 */
public class PROV {

	private static final String uri = "http://www.w3.org/ns/prov#";

	/**
	 * Gets the ontology URI.
	 *
	 * @return "http://www.swows.org/tuio#"
	 */
	public static String getURI() {
		return uri;
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

    public final static Resource Entity = resource("Entity");
    public final static Resource Collection = resource("Collection");

    public final static Property wasDerivedFrom = property("wasDerivedFrom");
    public final static Property wasGeneratedBy = property("wasGeneratedBy");
    public final static Property hadMember = property("hadMember");
      
}

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

/**
 * The Class SPINX maintains {@link com.hp.hpl.jena.rdf.model.Resource}
 * and {@link com.hp.hpl.jena.rdf.model.Property} instances for
 * the SPINX ontology
 * (<a href="http://www.swows.org/spinx">http://www.swows.org/spinx</a>).
 */
public class SAS {

	private static final String uri = "http://www.swows.org/sas#";

	/**
	 * Gets the ontology URI.
	 *
	 * @return "http://www.swows.org/sas#"
	 */
	public static String getURI() {
		return uri;
	}

    /**
     * Creates a new resource based on the SPINX uri and a local name
     *
     * @param localName the local name of the resource
     * @return the created resource
     */
    protected static final Resource resource( String localName ) {
    	return ResourceFactory.createResource( uri + localName );
    }

    /**
     * Creates a new property based on the SPINX uri and a local name.
     *
     * @param localName the local name of the property
     * @return the created property
     */
    protected static final Property property( String localName ) {
    	return ResourceFactory.createProperty( uri, localName );
    }


    /** Constants for sparql algebra elements. */
    public final static Resource Op = resource("Op");
    public final static Resource BGP = resource("BGP");
    public final static Resource Union = resource("Union");
    public final static Resource Join = resource("Join");
    public final static Resource TriplePattern = resource("TriplePattern");
    public final static Resource QuadPattern = resource("QuadPattern");
    public final static Resource FromTable = resource("FromTable");
    public final static Resource Table = resource("Table");
    public final static Resource Column = resource("Column");
    public final static Resource Row = resource("Row");
    public final static Resource Binding = resource("Binding");
    public final static Resource VarName = resource("VarName");
    public final static Resource Null = resource("Null");
    public final static Resource Procedure = resource("Procedure");
    public final static Resource PropertyFunction = resource("PropertyFunction");
    public final static Resource Filter = resource("Filter");
    public final static Resource Graph = resource("Graph");
    public final static Resource Service = resource("Service");
    public final static Resource DatasetNames = resource("DatasetNames");
    public final static Resource Labelled = resource("Labelled");
    public final static Resource Assign = resource("Assign");
    public final static Resource Assignment = resource("Assign");

    public final static Property op = property("op");
    public final static Property subOp = property("subOp");
    public final static Property name = property("name");
    public final static Property triplePattern = property("triplePattern");
    public final static Property quadPattern = property("quadPattern");
    public final static Property table = property("table");
    public final static Property firstColumn = property("firstColumn");
    public final static Property firstRow = property("firstRow");
    public final static Property nextColumn = property("nextColumn");
    public final static Property nextRow = property("nextRow");
    public final static Property prevColumn = property("prevColumn");
    public final static Property prevRow = property("prevRow");
    public final static Property lastColumn = property("lastColumn");
    public final static Property lastRow = property("lastRow");
    public final static Property binding = property("binding");
    public final static Property value = property("value");
    public final static Property variable = property("variable");
    public final static Property varName = property("varName");
    public final static Property procedureName = property("procedureName");
    public final static Property propertyFunctionName = property("propertyFunctionName");
    public final static Property subjectArg = property("subjectArg");
    public final static Property objectArg = property("objectArg");
    public final static Property expr = property("expr");
    public final static Property graphNameNode = property("graphNameNode");
    public final static Property serviceNode = property("serviceNode");
    public final static Property label = property("label");
    public final static Property assignment = property("assignment");

}

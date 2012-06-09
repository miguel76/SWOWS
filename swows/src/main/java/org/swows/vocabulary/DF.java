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
public class DF {

	private static final String uri = "http://www.swows.org/dataflow#";

	/**
	 * Gets the ontology URI.
	 *
	 * @return "http://www.swows.org/spinx#"
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

    /** The Constant Graph. */
    public final static Resource Graph = resource("Graph");
    public final static Resource UnionGraph = resource("UnionGraph");
    public final static Resource DatasetProjGraph = resource("DatasetProjGraph");
    public final static Resource InputGraph = resource("InputGraph");
    public final static Resource OutputGraph = resource("OutputGraph");
    public final static Resource ConstructGraph = resource("ConstructGraph");
    public final static Resource IdentityGraph = resource("IdentityGraph");
    public final static Resource InferenceGraph = resource("InferenceGraph");
    public final static Resource EmptyGraph = resource("EmptyGraph");
    public final static Resource UpdatableGraph = resource("UpdatableGraph");
    public final static Resource UpdatableFromEventsGraph = resource("UpdatableFromEventsGraph");
    public final static Resource UpdatableFromEventsGraph2 = resource("UpdatableFromEventsGraph2");
    public final static Resource LoggedGraph = resource("LoggedGraph");
    public final static Resource NamedGraph = resource("NamedGraph");
    public final static Resource IntegerRange = resource("IntegerRange");
    public final static Resource InlineGraph = resource("InlineGraph");
    public final static Resource LoadGraph = resource("LoadGraph");
    public final static Resource SelectGraph = resource("SelectGraph");
    public final static Resource IntegerRangeFromGraph = resource("IntegerRangeFromGraph");
    public final static Resource DataflowGraph = resource("DataflowGraph");

    public final static Resource IncludedGraph = resource("IncludedGraph");
    public final static Resource TwitterGraph = resource("TwitterGraph");
    public final static Resource JmsInputGraph = resource("JmsInputGraph");
    
    /** The Constant Dataset. */
    public final static Resource Dataset = resource("Dataset");
    public final static Resource InlineDataset = resource("InlineDataset");

    /** The Constant Update. */
    public final static Resource Update = resource("Update");

    //public final static Property isConstructedBy = property("isConstructedBy");
    //public final static Property unionOf = property("unionOf");
    /** The Constant constructQuery. */
    public final static Property constructQuery = property("constructQuery");
    
    /** The Constant input. */
    public final static Property input = property("input");
    
    /** The Constant namedInput. */
    public final static Property namedInput = property("namedInput");

    /** The Constant config. */
    public final static Property config = property("config");
    //public final static Property source = property("source");
    /** The Constant id. */
    public final static Property id = property("id");
    
    /** The Constant from. */
    public final static Property from = property("from");
    
    /** The Constant fromNamed. */
    public final static Property fromNamed = property("fromNamed");
    //public final static Property ontModelSpec = property("ontModelSpec");

    /** The Constant reasonerType. */
    public final static Property reasonerType = property("reasonerType");
    
    /** The Constant reasonerConfig. */
    public final static Property reasonerConfig = property("reasonerConfig");
    
    /** The Constant schema. */
    public final static Property schema = property("schema");

    public final static Property baseGraph = property("baseGraph");
    public final static Property addGraph = property("addGraph");
    public final static Property deleteGraph = property("deleteGraph");

    public final static Property addConstruct = property("addConstruct");
    public final static Property deleteConstruct = property("deleteConstruct");
    public final static Property eventsFrom = property("eventsFrom");

    public final static Property url = property("url");
    public final static Property syntax = property("syntax");
    public final static Property baseUri = property("baseUri");
    public final static Property pollingPeriod = property("pollingPeriod");

    public final static Property user = property("user");
    public final static Property password = property("password");
    public final static Property subject = property("subject");

    /** Constants for inner sparql elements. */
//    public final static Resource Exists = resource("Exists");
//    public final static Resource NotExists = resource("NotExists");
//    public final static Resource Assign = resource("Assign");
//    public final static Resource ElementGroup = resource("ElementGroup");
//    public final static Resource EmptyElement = resource("EmptyElement");

//    public final static Resource FunctionCall = resource("FunctionCall");
//    public final static Resource OpCall = resource("FunctionCall");

//    public final static Property UnknownExpr = property("UnknownExpr");

//    public final static Property element = property("element");
//    public final static Property var = property("var");
//    public final static Property expr = property("expr");
//    public final static Property triple = property("triple");
//
//    public final static Property functionIRI = property("functionIRI");
//    public final static Property functionLabel = property("functionLabel");
//    public final static Property opName = property("opName");

    public final static Property intervalStart = property("intervalStart");
    public final static Property intervalEnd = property("intervalEnd");
    
//    public final static Property resultVariable = property("resultVariable");
    

     public final static Property twitterUsername = property("twitterUsername");
    public final static Property tweetNumber = property("tweetNumber");
    public final static Property eventType = property("eventType");
    public final static Property xCoord = property("xCoord");
    public final static Property yCoord = property("yCoord");
    
    //public final static Property reasonerFactory = property("reasonerFactory");

    //	public final static Property all = ResourceFactory.createProperty(NS + "all");

}

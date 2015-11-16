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

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Vocabulary of the SPIN SPARQL Syntax schema.
 * 
 * @author Holger Knublauch
 */
public class SWI {

    public final static String BASE_URI = "http://www.swows.org/instance";

    public final static String NS = BASE_URI + "#";

    public final static String PREFIX = "swi";
    
//    public final static String VAR_NS = "http://spinrdf.org/var#";
//
//    public final static String VAR_PREFIX = "var";


    public final static Resource GraphRoot = ResourceFactory.createResource(NS + "GraphRoot");

    public final static Resource InputDataset = ResourceFactory.createResource(NS + "InputDataset");
    public final static Resource OutputDataset = ResourceFactory.createResource(NS + "OutputDataset");

    public final static Resource ThisGraph = ResourceFactory.createResource(NS + "ThisGraph");
    public final static Resource AddedGraph = ResourceFactory.createResource(NS + "AddedGraph");
    public final static Resource DeletedGraph = ResourceFactory.createResource(NS + "DeletedGraph");

    
//    public static Property getArgProperty(int index) {
//    	return ResourceFactory.createProperty(NS + "arg" + index);
//    }
//    
//    
//    public static Property getArgProperty(String varName) {
//    	return ResourceFactory.createProperty(NS + varName);
//    }
//    
//    
//    public static Integer getArgPropertyIndex(String varName) {
//    	if(varName.startsWith("arg")) {
//    		String subString = varName.substring(3);
//    		try {
//    			return Integer.getInteger(subString);
//    		}
//    		catch(Throwable t) {
//    		}
//    	}
//    	return null;
//    }
//	
	
	public static String getURI() {
        return NS;
    }


//	public static void toStringElementList(StringBuffer buffer, Resource resource) {
//		RDFList list = (RDFList) resource.as(RDFList.class);
//		for(ExtendedIterator<RDFNode> it = list.iterator(); it.hasNext(); ) {
//			Resource item = (Resource) it.next();
//			Element e = SPINFactory.asElement(item);
//			buffer.append(e.toString());
//			if(it.hasNext()) {
//				buffer.append(" .\n");
//			}
//		}
//	}
}

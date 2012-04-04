package org.swows.vocabulary;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Vocabulary of the SPIN SPARQL Syntax schema.
 * 
 * @author Holger Knublauch
 */
public class Instance {

    public final static String BASE_URI = "http://www.swows.org/instance";

    public final static String NS = BASE_URI + "#";

    public final static String PREFIX = "swi";
    
//    public final static String VAR_NS = "http://spinrdf.org/var#";
//
//    public final static String VAR_PREFIX = "var";


    public final static Resource GraphRoot = ResourceFactory.createResource(NS + "GraphRoot");

    public final static Resource InputDataset = ResourceFactory.createResource(NS + "InputDataset");

    public final static Resource OutputDataset = ResourceFactory.createResource(NS + "OutputDataset");

    
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

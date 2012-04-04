package org.swows.pfunction;

import com.hp.hpl.jena.sparql.pfunction.PropertyFunction;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionFactory;

public class Factory implements PropertyFunctionFactory {
	
	private static final String BASE_URI = "http://www.swows.org/pfunction#";
	private static final int BASE_URI_LENGTH = BASE_URI.length();
	private static Factory singleton;
	
	//private static List<String uri>
	static  {
		singleton = new Factory();
	}

	public static Factory getInstance() {
		return singleton;
	}
	
	public static String getBaseURI() {
		return BASE_URI;
	}

	@Override
	public PropertyFunction create(String uri) {
		if (uri.startsWith(BASE_URI)) {
			String pfunctionName = uri.substring(BASE_URI_LENGTH);
			if (pfunctionName.equals("bnode"))
				return bnode.getInstance();
		}
		return null;
	}

}

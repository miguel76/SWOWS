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

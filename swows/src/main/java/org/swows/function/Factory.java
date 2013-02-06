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
package org.swows.function;

import com.hp.hpl.jena.sparql.function.Function;
import com.hp.hpl.jena.sparql.function.FunctionFactory;
import com.hp.hpl.jena.sparql.function.FunctionRegistry;

public class Factory implements FunctionFactory {
	
	static final String BASE_URI = "http://www.swows.org/function#";
	private static final int BASE_URI_LENGTH = BASE_URI.length();
	private static Factory singleton;
	
	//private static List<String uri>
	static  {
		singleton = new Factory();
	}

	private Factory() {
		FunctionRegistry.get().put(BASE_URI + "to", this);
		FunctionRegistry.get().put(BASE_URI + "sin", this);
		FunctionRegistry.get().put(BASE_URI + "cos", this);
		FunctionRegistry.get().put(BASE_URI + "atan", this);
		FunctionRegistry.get().put(BASE_URI + "analyze-string", this);
		FunctionRegistry.get().put(BASE_URI + "recur", this);
	}
	
	public static Factory getInstance() {
		return singleton;
	}
	
	public static String getBaseURI() {
		return BASE_URI;
	}

	@Override
	public Function create(String uri) {
		if (uri.startsWith(BASE_URI)) {
			String functionName = uri.substring(BASE_URI_LENGTH);
			if (functionName.equals("to"))
				return new to();
			if (functionName.equals("sin"))
				return new sin();
			if (functionName.equals("cos"))
				return new cos();
			if (functionName.equals("atan"))
				return new atan();
			if (functionName.equals("analyze-string"))
				return new AnalyzeString();
			if (functionName.equals("recur"))
				return new recur();
		}
		return null;
	}

}

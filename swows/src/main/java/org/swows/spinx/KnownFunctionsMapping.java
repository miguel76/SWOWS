package org.swows.spinx;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.sparql.expr.ExprFunction;

public class KnownFunctionsMapping {

	static Map<String,Class<? extends ExprFunction>> mapping = new HashMap<String, Class<? extends ExprFunction>>();
	
	static void set(String symbol, Class<? extends ExprFunction> functClass) {
		mapping.put(symbol, functClass);
	}

	static Class<? extends ExprFunction> get(String symbol) {
		return mapping.get(symbol);
	}

	static boolean contains(String symbol) {
		return mapping.containsKey(symbol);
	}

}

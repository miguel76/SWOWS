package org.swows.spinx;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.sparql.expr.aggregate.Aggregator;

public class KnownAggregatorsMapping {

	static Map<String,Class<? extends Aggregator>> mapping = new HashMap<String, Class<? extends Aggregator>>();
	
	static void set(String symbol, Class<? extends Aggregator> functClass) {
		mapping.put(symbol, functClass);
	}

	static Class<? extends Aggregator> get(String symbol) {
		return mapping.get(symbol);
	}

	static boolean contains(String symbol) {
		return mapping.containsKey(symbol);
	}

}

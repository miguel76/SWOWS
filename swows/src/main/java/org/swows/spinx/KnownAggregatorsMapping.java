/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open Web Server (SWOWS).

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

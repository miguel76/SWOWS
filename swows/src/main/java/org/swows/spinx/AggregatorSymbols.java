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
package org.swows.spinx;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.swows.vocabulary.SPINX;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.aggregate.AggAvg;
import com.hp.hpl.jena.sparql.expr.aggregate.AggAvgDistinct;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCount;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCountDistinct;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCountVar;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCountVarDistinct;
import com.hp.hpl.jena.sparql.expr.aggregate.AggGroupConcat;
import com.hp.hpl.jena.sparql.expr.aggregate.AggGroupConcatDistinct;
import com.hp.hpl.jena.sparql.expr.aggregate.AggMax;
import com.hp.hpl.jena.sparql.expr.aggregate.AggMaxDistinct;
import com.hp.hpl.jena.sparql.expr.aggregate.AggMin;
import com.hp.hpl.jena.sparql.expr.aggregate.AggMinDistinct;
import com.hp.hpl.jena.sparql.expr.aggregate.AggSample;
import com.hp.hpl.jena.sparql.expr.aggregate.AggSampleDistinct;
import com.hp.hpl.jena.sparql.expr.aggregate.AggSum;
import com.hp.hpl.jena.sparql.expr.aggregate.AggSumDistinct;
import com.hp.hpl.jena.sparql.expr.aggregate.Aggregator;

public class AggregatorSymbols {
	
	private static final String baseUri = SPINX.getURI();
	
	private static Map< Class<? extends Aggregator>, Node > aggregators2uris = new HashMap<Class<? extends Aggregator>, Node>();
	private static Set< Class<? extends Aggregator> > distinctAggregators = new HashSet<Class<? extends Aggregator>>();
	private static Set< Class<? extends Aggregator> > withExprAggregators = new HashSet<Class<? extends Aggregator>>();
	private static Map< Node, Map<Boolean, Map<Boolean, Class<? extends Aggregator>> > > uris2aggregators = new HashMap<Node, Map<Boolean, Map<Boolean, Class<? extends Aggregator> > > >();
	
	static {
		add(AggAvg.class, "Avg", false, true);
		add(AggAvgDistinct.class, "Avg", true, true);
		add(AggCount.class, "Count", false, false);
		add(AggCountDistinct.class, "Count", true, false);
		add(AggCountVar.class, "Count", false, true);
		add(AggCountVarDistinct.class, "Count", true, true);
		add(AggGroupConcat.class, "GroupConcat", false, true);
		add(AggGroupConcatDistinct.class, "GroupConcat", true, true);
		add(AggMax.class, "Max", false, true);
		add(AggMaxDistinct.class, "Max", true, true);
		add(AggMin.class, "Min", false, true);
		add(AggMinDistinct.class, "Min", true, true);
		add(AggSample.class, "Sample", false, true);
		add(AggSampleDistinct.class, "Sample", true, true);
		add(AggSum.class, "Sum", false, true);
		add(AggSumDistinct.class, "Sum", true, true);
	}
	
	private static void add( Class<? extends Aggregator> aggregatorClass, String name, boolean distinct, boolean withExpr ) {
		Node aggrUriNode = Node.createURI(baseUri + name);
		aggregators2uris.put(aggregatorClass, aggrUriNode);
		if (distinct)
			distinctAggregators.add(aggregatorClass);
		Map<Boolean, Map<Boolean, Class<? extends Aggregator> > > nodeMap = uris2aggregators.get(aggrUriNode);
		if (nodeMap == null) {
			nodeMap = new HashMap<Boolean,  Map<Boolean, Class<? extends Aggregator> > >();
			uris2aggregators.put(aggrUriNode, nodeMap);
		}
		Map<Boolean, Class<? extends Aggregator> > distinctMap = nodeMap.get(distinct);
		if (distinctMap == null) {
			distinctMap = new HashMap<Boolean,  Class<? extends Aggregator> >();
			nodeMap.put(distinct, distinctMap);
		}
		distinctMap.put(withExpr, aggregatorClass);
	}
	
	public static Node getUriNode(Class<? extends Aggregator> aggregatorClass) {
		return aggregators2uris.get(aggregatorClass);
	}

	public static boolean isDistinct(Class<? extends Aggregator> aggregatorClass) {
		return distinctAggregators.contains(aggregatorClass);
	}
	
	public static boolean hasExpr(Class<? extends Aggregator> aggregatorClass) {
		return withExprAggregators.contains(aggregatorClass);
	}
	
	public static Node getUriNode(Aggregator aggregator) {
		return getUriNode(aggregator.getClass());
	}

	public static boolean isDistinct(Aggregator aggregator) {
		return isDistinct(aggregator.getClass());
	}
	
	public static boolean hasExpr(Aggregator aggregator) {
		return hasExpr(aggregator.getClass());
	}
	
	public static Class<? extends Aggregator> getAggregatorClass(Node uriNode, boolean distinct, boolean withExpr) {
		return uris2aggregators.get(uriNode).get(distinct).get(withExpr);
	}

	public static Aggregator getAggregator(Node uriNode, boolean distinct, Expr expr) {
		Class<? extends Aggregator> aggregatorClass = getAggregatorClass(uriNode, distinct, expr != null);
		try {
			if (expr == null) {
				try {
					return ((Constructor<Aggregator>) aggregatorClass.getConstructor()).newInstance();
				} catch(NoSuchMethodException e) {
					Constructor<Aggregator>[] constructors = (Constructor<Aggregator>[]) aggregatorClass.getConstructors();
					for (Constructor<Aggregator> currConstr : constructors) {
						Class[] params = currConstr.getParameterTypes();
						if (!params[0].equals(Expr.class)) {
							Object[] nullValues = new Object[params.length];
							for (int i = 0; i < params.length; i++) nullValues[i] = null;
							return currConstr.newInstance(nullValues);
						}
					}
					throw new RuntimeException("Could not find empty constructor for " + aggregatorClass);
				}
			} else {
				try {
					return ((Constructor<Aggregator>) aggregatorClass.getConstructor(Expr.class)).newInstance(expr);
				} catch(NoSuchMethodException e) {
					Constructor<Aggregator>[] constructors = (Constructor<Aggregator>[]) aggregatorClass.getConstructors();
					for (Constructor<Aggregator> currConstr : constructors) {
						Class[] params = currConstr.getParameterTypes();
						if (params[0].equals(Expr.class)) {
							Object[] exprAndNullValues = new Object[params.length];
							exprAndNullValues[0] = expr;
							for (int i = 1; i < params.length; i++) exprAndNullValues[i] = null;
							return currConstr.newInstance(exprAndNullValues);
						}
					}
					throw new RuntimeException("Could not find Expr constructor for " + aggregatorClass);
				}
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException)
					throw (RuntimeException) e; 
			throw new RuntimeException(e); 
		}
	}

}

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

import java.math.BigInteger;
import java.util.List;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.vocabulary.RDFS;
import org.swows.vocabulary.SWI;

public class to extends GraphReturningFunction {

	@Override
	public int getMinArgNum() {
		return 2;
	}

	@Override
	public int getMaxArgNum() {
		return 2;
	}

	@Override
	public Graph exec(List<NodeValue> args) {
		//System.out.println("Executing to() function...");
        NodeValue fromNV = args.get(0);
        NodeValue toNV = args.get(1);
        BigInteger index = fromNV.getInteger();
        BigInteger to = toNV.getInteger();
        Graph newGraph = GraphFactory.createGraphMem();
		//System.out.println("Empty graph created");
		Node root = SWI.GraphRoot.asNode();
		//System.out.println("Filling graph...");
        while(index.compareTo(to) <= 0) {
        	newGraph.add(
        			new Triple(
        					root,
        					RDFS.member.asNode(),
        					NodeFactory.createLiteral(index.toString(),XSDDatatype.XSDinteger) ) );
        	index = index.add(BigInteger.ONE);
        }
		//System.out.println("Graph filled: " + newGraph);
		//System.out.println("End of to() function execution");
		return newGraph;
	}

}

package org.swows.function;

import java.math.BigInteger;
import java.util.List;

import org.swows.vocabulary.Instance;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

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
		Node root = Instance.GraphRoot.asNode();
		//System.out.println("Filling graph...");
        while(index.compareTo(to) <= 0) {
        	newGraph.add(
        			new Triple(
        					root,
        					RDFS.member.asNode(),
        					Node.createLiteral(index.toString(),XSDDatatype.XSDinteger) ) );
        	index = index.add(BigInteger.ONE);
        }
		//System.out.println("Graph filled: " + newGraph);
		//System.out.println("End of to() function execution");
		return newGraph;
	}

}

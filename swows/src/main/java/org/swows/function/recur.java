package org.swows.function;

import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.sparql.util.Symbol;

public class recur extends GraphReturningFunction {

	@Override
	public int getMinArgNum() {
		return 1;
	}

	@Override
	public int getMaxArgNum() {
		return 1;
	}

	@Override
	public Graph exec(List<NodeValue> args) {
		// The other exec() is doing the job!
		return null;
	}

	@Override
	public Graph exec(List<NodeValue> args, FunctionEnv env) {
        //NodeValue defaultGraphNodeValue = args.get(0);
		for (Symbol s : env.getContext().keys())
			System.out.println("Found symbol in context: " + s);
		return GraphFactory.createDefaultGraph();
	}

}

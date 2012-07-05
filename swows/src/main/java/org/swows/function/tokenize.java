package org.swows.function;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.swows.node.Skolemizer;
import org.swows.vocabulary.SWI;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.hp.hpl.jena.sparql.graph.GraphFactory;

public class tokenize extends GraphReturningFunction {

	@Override
	public int getMinArgNum() {
		return 2; // input, pattern
	}

	@Override
	public int getMaxArgNum() {
		return 2; // input, pattern
//		return 3; // input, pattern, flags
	}

	private Node createMatching(
			MatchResult matchResult, 
			FunctionEnv env,
			List<NodeValue> params,
			int nodeCount) {
		Node node = Skolemizer.getInstance().getNode(env, params, nodeCount);
		// TODO ADD EVERYTHING
		return node;
	}
	
	private Node createNonMatching(
			String token,
			FunctionEnv env,
			List<NodeValue> params,
			int nodeCount) {
		Node node = Skolemizer.getInstance().getNode(env, params, nodeCount);
		// TODO ADD EVERYTHING
		return node;
	}
	
	@Override
	public Graph exec(List<NodeValue> args, FunctionEnv env) {
        NodeValue inputNV = args.get(0);
        NodeValue patternNV = args.get(1);
        String inputStr = inputNV.asString();
        String patternStr = patternNV.asString();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);
        Graph newGraph = GraphFactory.createGraphMem();
		Node root = SWI.GraphRoot.asNode();
//		boolean first = true, firstMatching = true, firstNonMatching = true;
		Node last = null, lastMatching = null, lastNonMatching = null;
		int lastMatchedPos = 0;
		int nodeCount = 0;
		while (matcher.find()) {
			if (matcher.start() > lastMatchedPos) {
				Node nonMatching =
						createNonMatching(
								inputStr.substring(lastMatchedPos, matcher.start()),
								env, args, nodeCount++);
				// TODO ADD EVERYTHING
			}
			// TODO ADD EVERYTHING
		}
		// TODO ADD EVERYTHING
		return null;
	}

	@Override
	public Graph exec(List<NodeValue> args) {
		// The other exec() is doing the job!
		return null;
	}

}

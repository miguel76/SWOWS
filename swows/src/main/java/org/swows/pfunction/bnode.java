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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.pfunction.PropFuncArg;
import org.apache.jena.sparql.util.IterLib;

/**
 * The Class bnode realize the property function
 * {@code bnode} that creates a new blank node associated
 * to a specific tuple-value of a list of variables
 */
public class bnode extends PFuncListAndSimple {
	
	private static bnode singleton = null;
	
	public static bnode getInstance() {
		if (singleton == null)
			singleton = new bnode();
		return singleton;
	}

//	private Map<PhantomReference<Node>, Map<List<Node>, Node>> blankNodesMap = null;
	private Map<Node, Map<List<Node>, Node>> blankNodesMap = null;
//	private static ReferenceQueue<Node> refQueue = new ReferenceQueue<Node>();
	private bnode() {
		
	}

	private void setBlankNode(Node var, List<Node> list, Node blankNode) {
		if (blankNodesMap == null)
			blankNodesMap = new WeakHashMap<Node, Map<List<Node>, Node>>();
//		PhantomReference<Node> phantomRef = new PhantomReference<Node>(var, refQueue);
		Map<List<Node>, Node> varMap = blankNodesMap.get(var);
		if (varMap == null) {
			varMap = new HashMap<List<Node>, Node>();
			blankNodesMap.put(var, varMap);
		}
		varMap.put(list, blankNode);
	}

	private Node getBlankNode(Node var, List<Node> list) {
		if (blankNodesMap != null) {
			Map<List<Node>, Node> varMap = blankNodesMap.get(var);
			if (varMap != null)
				return varMap.get(list);
		}
		return null;
	}

    /* (non-Javadoc)
     * @see org.swows.pfunction.PFuncListAndSimple#execEvaluated(org.apache.jena.sparql.engine.binding.Binding, org.apache.jena.sparql.pfunction.PropFuncArg, org.apache.jena.graph.Node, org.apache.jena.graph.Node, org.apache.jena.sparql.engine.ExecutionContext)
     */
    @Override
    public QueryIterator execEvaluated(
    		Binding binding,
    		PropFuncArg subject, Node predicate, Node object,
            ExecutionContext execCxt) {

        if ( ! Var.isVar(object) )
            throw new ExprEvalException("Object is not a variable ("+object+")") ;

        List<Node> list = subject.getArgList();
        Node blankNode = getBlankNode(object, list);

        if (blankNode == null) {
        	blankNode = NodeFactory.createAnon();
        	setBlankNode(object, list, blankNode);
        }

        return IterLib.oneResult(binding, Var.alloc(object), blankNode, execCxt) ;
    }

}

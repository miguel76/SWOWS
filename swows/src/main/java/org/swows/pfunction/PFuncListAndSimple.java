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
package org.swows.pfunction;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArgType;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionEval;

/**
 * Common, simple case, of a property function:
 * <ul>
 * <li>subject is a list</li>
 * <li>object arguments is not a list</li>
 * <li>call the implementation with one binding at a time</li>
 * </ul>.
 * 
 * @see com.hp.hpl.jena.sparql.pfunction.PFuncSimpleAndList
 */

public abstract
class PFuncListAndSimple extends PropertyFunctionEval {

    /**
     * Instantiates a new instance.
     */
    protected PFuncListAndSimple() {
        super(PropFuncArgType.PF_ARG_LIST, PropFuncArgType.PF_ARG_SINGLE) ;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.sparql.pfunction.PropertyFunctionEval#execEvaluated(com.hp.hpl.jena.sparql.engine.binding.Binding, com.hp.hpl.jena.sparql.pfunction.PropFuncArg, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.sparql.pfunction.PropFuncArg, com.hp.hpl.jena.sparql.engine.ExecutionContext)
     */
    @Override
    public QueryIterator execEvaluated(Binding binding, PropFuncArg argSubject, Node predicate, PropFuncArg argObject, ExecutionContext execCxt) {
        return execEvaluated(binding, argSubject, predicate, argObject.getArg(), execCxt) ;
    }

    /**
     * @param binding   Current solution from previous query stage
     * @param subject   List in subject slot, after substitution if a bound variable in this binding
     * @param predicate This predicate
     * @param object    Node in object slot, after substitution if a bound variable in this binding
     * @param execCxt   Execution context
     * @return          QueryIterator
     */
    public abstract QueryIterator execEvaluated(
    		Binding binding,
    		PropFuncArg subject, Node predicate, Node object,
            ExecutionContext execCxt) ;

}


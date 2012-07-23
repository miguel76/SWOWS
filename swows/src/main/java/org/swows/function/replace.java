/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swows.function;

/**
 *
 * @author dario
 */

import java.util.List;

import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.sparql.expr.ExprEvalException;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase;
import com.hp.hpl.jena.sparql.util.Utils;

/** sin(expression) */ 

public class replace extends FunctionBase {

	public replace() { 
		super() ; 
	}

    @Override 
    public void checkBuild(String uri, ExprList args) {
        if ( args.size() != 3 ) 
        	throw new QueryBuildException("Function '"+Utils.className(this)+"' takes three arguments") ;
    }

    @Override
    public NodeValue exec(List<NodeValue> args) {
        if ( args.size() > 3 )
        	throw new ExprEvalException("replace: Wrong number of arguments: "+args.size()+" : [wanted 3]") ;
        
        NodeValue v1 = args.get(0) ;
        NodeValue v2 = args.get(1) ;
        NodeValue v3 = args.get(2) ;
        
        
        return javaReplace(v1, v2, v3) ;
    }
    
    private static NodeValue javaReplace(NodeValue nvString, NodeValue nvRegex, NodeValue nvReplacement)
    {
        try {
            
            String string = nvString.getString() ;
            String regex = nvRegex.getString() ;
            String replacement = nvReplacement.getString() ;
            
            return NodeValue.makeString(string.replaceAll(regex, replacement)) ;
        } catch (IndexOutOfBoundsException ex) {
            throw new ExprEvalException("IndexOutOfBounds", ex) ;
        }
    }
        
}
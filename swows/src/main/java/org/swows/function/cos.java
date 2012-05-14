package org.swows.function;

import com.hp.hpl.jena.sparql.ARQInternalErrorException;
import com.hp.hpl.jena.sparql.expr.NodeValue ;
import com.hp.hpl.jena.sparql.function.FunctionBase1 ;

/** cos(expression) */ 

public class cos extends FunctionBase1 {

	public cos() {
		super();
	}
	    
	@Override
	public NodeValue exec(NodeValue v) { 
        if ( v.isInteger() || v.isDecimal() ) {
            double dec = v.getDecimal().doubleValue() ;
            return NodeValue.makeDecimal( Math.cos(dec) ) ;
        }
        if ( v.isFloat() )
            // NB - returns a double
            return NodeValue.makeDouble( Math.cos(v.getDouble()) ) ;
        if ( v.isDouble() )
            return NodeValue.makeDouble( Math.cos(v.getDouble()) ) ;
        throw new ARQInternalErrorException("Unrecognized numeric operation : "+v) ;   
    }

}

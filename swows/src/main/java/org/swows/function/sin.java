package org.swows.function;

import com.hp.hpl.jena.sparql.ARQInternalErrorException;
import com.hp.hpl.jena.sparql.expr.NodeValue ;
import com.hp.hpl.jena.sparql.function.FunctionBase1 ;

/** sin(expression) */ 

public class sin extends FunctionBase1 {

	public sin() {
		super();
	}
	    
	@Override
	public NodeValue exec(NodeValue v) { 
        if ( v.isInteger() || v.isDecimal() ) {
            double dec = v.getDecimal().doubleValue() ;
            return NodeValue.makeDecimal( Math.sin(dec) ) ;
        }
        if ( v.isFloat() )
            // NB - returns a double
            return NodeValue.makeDouble( Math.sin(v.getDouble()) ) ;
        if ( v.isDouble() )
            return NodeValue.makeDouble( Math.sin(v.getDouble()) ) ;
        throw new ARQInternalErrorException("Unrecognized numeric operation : "+v) ;   
    }

}

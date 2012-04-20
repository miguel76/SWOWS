package org.swows.function;

import com.hp.hpl.jena.sparql.ARQInternalErrorException;
import com.hp.hpl.jena.sparql.expr.NodeValue ;
import com.hp.hpl.jena.sparql.function.FunctionBase1 ;

/** sin(expression) */ 

public class atan extends FunctionBase1 {

	public atan() {
		super();
	}
	    
	@Override
	public NodeValue exec(NodeValue v) { 
        if ( v.isInteger() || v.isDecimal() ) {
            double dec = v.getDecimal().doubleValue() ;
            return NodeValue.makeDecimal( Math.atan(dec) ) ;
        }
        if ( v.isFloat() )
            // NB - returns a double
            return NodeValue.makeDouble( Math.atan(v.getDouble()) ) ;
        if ( v.isDouble() )
            return NodeValue.makeDouble( Math.atan(v.getDouble()) ) ;
        throw new ARQInternalErrorException("Unrecognized numeric operation : "+v) ;   
    }

}

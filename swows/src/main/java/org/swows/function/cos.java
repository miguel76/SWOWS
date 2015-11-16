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

import org.apache.jena.sparql.ARQInternalErrorException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;

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

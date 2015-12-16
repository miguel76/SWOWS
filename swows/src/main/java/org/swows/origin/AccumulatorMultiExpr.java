package org.swows.origin;

import java.util.Collection;
import java.util.Iterator;

import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.aggregate.Accumulator;
import org.apache.jena.sparql.function.FunctionEnv;

abstract public class AccumulatorMultiExpr implements Accumulator {
    private long count = 0 ;
    protected long errorCount = 0 ; 
    private final Collection<Expr> exprs;
    
    protected AccumulatorMultiExpr(Collection<Expr> exprs) {
        this.exprs = exprs;
    }
    
    @Override
    final public void accumulate(Binding binding, FunctionEnv functionEnv) {
        try { 
        	accumulate(
        			exprs
        				.stream()
        				.map(expr -> expr.eval(binding, functionEnv)).iterator(),
        			binding, functionEnv) ;
            count++ ;
        } catch (ExprEvalException ex)
        {
            errorCount++ ;
            accumulateError(binding, functionEnv) ;
        }
    }
    
    
    // Count(?v) is different
    @Override
    public NodeValue getValue() {
        if ( errorCount == 0 )
            return getAccValue() ;  
        return null ;
    }

    protected long getErrorCount() { return errorCount ; }
    
    /** Called if no errors to get the accumulated result */
    protected abstract NodeValue getAccValue() ; 

    /** Called when the expression beeing aggregated evaluates OK.
     * Can throw ExprEvalException - in which case the accumulateError is called */
    protected abstract void accumulate(
    		Iterator<NodeValue> nodeValues,
    		Binding binding, FunctionEnv functionEnv) ;
    /** Called when an evaluation of the expression causes an error
     * or when the accumulation step throws ExprEvalException  
     */
    protected abstract void accumulateError(Binding binding, FunctionEnv functionEnv) ;

}

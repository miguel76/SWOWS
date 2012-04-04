package org.swows.function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.sparql.ARQInternalErrorException;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprEvalException;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.function.Function;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.hp.hpl.jena.sparql.util.Utils;

public abstract class GraphReturningFunction implements Function {

	public abstract int getMinArgNum();
	public abstract int getMaxArgNum();
    public abstract Graph exec(List<NodeValue> args) ;
	
    private String wantedArgNum = null;
    
    public String getWantedArgNum() {
    	if (wantedArgNum == null)
    		wantedArgNum =
    				( getMinArgNum() < 0 )
    					? "less than " + getMaxArgNum()
    					: ( ( getMaxArgNum() < 0 )
    							? "more than " + getMinArgNum()
    							: ( ( getMaxArgNum() == getMinArgNum() )
    									? "" + getMinArgNum()
    									: "between " + getMinArgNum() + " and " + getMaxArgNum() ) );
    	return wantedArgNum;
    }

	@Override
	public NodeValue exec(
			Binding binding,
            ExprList args,
            String uri,
            FunctionEnv env) {

        //this.env = env ;
        
		System.out.println("Executing a graph returning function...");

		if ( args == null )
            // The contract on the function interface is that this should not happen.
            throw new ARQInternalErrorException(Utils.className(this)+": Null args list") ;
        
        if ( args.size() < getMinArgNum() || ( getMaxArgNum() >=0 && args.size() > getMaxArgNum() ) ) {
            throw new ExprEvalException(Utils.className(this)+": Wrong number of arguments: Wanted " + getWantedArgNum() + ", got "+args.size()) ;
        }
        
        List<NodeValue> evalArgs = new ArrayList<NodeValue>() ;
        for ( Iterator<Expr> iter = args.iterator() ; iter.hasNext() ; )
        {
            Expr e = iter.next() ;
            NodeValue x = e.eval(binding, env) ;
            evalArgs.add(x) ;
        }
        
        Graph newGraph = exec(evalArgs) ;
		System.out.println("Graph created: " + newGraph);
        Node graphName = Node.createAnon();
        // TODO find another way to add this graph!!!!
        env.getDataset().addGraph(graphName, newGraph);
        //arguments = null ;
		System.out.println("Graph returning function executed and returning " + graphName);
        return new NodeValueNode(graphName);
		
	}
	
    public void checkBuild(String uri, ExprList args)
    { 
        if ( args.size() < getMinArgNum() || ( getMaxArgNum() >=0 && args.size() > getMaxArgNum() ) )
            throw new QueryBuildException("Function '"+Utils.className(this)+"' takes " + getWantedArgNum() + " arguments") ;
    }

	@Override
	public void build(String uri, ExprList args) {
        //this.uri = uri ;
        //arguments = args ;
        checkBuild(uri, args) ;
	}

}

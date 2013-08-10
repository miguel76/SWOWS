package org.swows.graph.algebra;

import java.util.List;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.algebra.Table;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.ExprList;

public class BGP implements Table {

	@Override
	public QueryIterator matchRightLeft(Binding bindingLeft,
			boolean includeOnNoMatch, ExprList condition,
			ExecutionContext execCxt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		
		// TODO Auto-generated method stub

	}

	@Override
	public List<Var> getVars() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getVarNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public QueryIterator iterator(ExecutionContext execCxt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addBinding(Binding binding) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean contains(Binding binding) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResultSet toResultSet() {
		// TODO Auto-generated method stub
		return null;
	}

}

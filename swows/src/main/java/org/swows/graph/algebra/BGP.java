package org.swows.graph.algebra;

import java.util.Iterator;
import java.util.List;

import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.algebra.Table;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.ExprList;

public class BGP implements Table {

	public QueryIterator matchRightLeft(Binding bindingLeft,
			boolean includeOnNoMatch, ExprList condition,
			ExecutionContext execCxt) {
		// TODO Auto-generated method stub
		return null;
	}

	public void close() {
		
		// TODO Auto-generated method stub

	}

	public List<Var> getVars() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getVarNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public QueryIterator iterator(ExecutionContext execCxt) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addBinding(Binding binding) {
		// TODO Auto-generated method stub

	}

	public boolean contains(Binding binding) {
		// TODO Auto-generated method stub
		return false;
	}

	public ResultSet toResultSet() {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<Binding> rows() {
		// TODO Auto-generated method stub
		return null;
	}

}

package org.swows.provenance;

import org.apache.jena.atlas.logging.Log;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.engine.Plan;
import org.apache.jena.sparql.engine.QueryEngineFactory;
import org.apache.jena.sparql.engine.QueryEngineRegistry;
import org.apache.jena.sparql.engine.QueryExecutionBase;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingRoot;
import org.apache.jena.sparql.engine.binding.BindingUtils;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.DatasetUtils;

public class QueryExecutionFactory extends org.apache.jena.query.QueryExecutionFactory {

    static public QueryExecution create(Op op, Dataset dataset)
    {
        //checkArg(dataset) ; // Allow null
        return create(op, dataset, null) ;
    }

    static public QueryExecution create(Op op, Dataset dataset, QuerySolution initialBinding) {
//        checkArg(query) ;
        QueryExecution qe = make(op, dataset, null) ;
        if ( initialBinding != null )
            qe.setInitialBinding(initialBinding) ;
        return qe ;
    }
    
    protected static QueryExecution make(Op op, Dataset dataset, Context context) {
//        query.setResultVars() ;
        if ( context == null )
            context = ARQ.getContext();  // .copy done in QueryExecutionBase -> Context.setupContext. 
        DatasetGraph dsg = null ;
        if ( dataset != null )
            dsg = dataset.asDatasetGraph() ;
        QueryEngineFactory f = findFactory(op, dsg, context);
        if ( f == null )
        {
            Log.warn(QueryExecutionFactory.class, "Failed to find a QueryEngineFactory for query: "+op) ;
            return null ;
        }
        return new LocalQueryExecution(op, dataset, context, f) ;
    }
    
    static private QueryEngineFactory findFactory(Op op, DatasetGraph dataset, Context context) {
        return QueryEngineRegistry.get().find(op, dataset, context);
    }
    
    private static class LocalQueryExecution extends QueryExecutionBase {
    	
    	private Op op;
        private Plan plan = null;
        private QueryEngineFactory qeFactory = null;
        private QuerySolution initialBinding = null ; 
   	
        public LocalQueryExecution(Op op, Dataset dataset, Context context, QueryEngineFactory qeFactory) {
        	super(OpAsQuery.asQuery(op), dataset, context, qeFactory);
        	this.op = op;
        	this.qeFactory = qeFactory;
		}

        private static DatasetGraph prepareDataset(Dataset dataset, Query query) {
            if ( dataset != null )
                return dataset.asDatasetGraph() ;
            
            if ( ! query.hasDatasetDescription() ) 
                //Query.Log.warn(this, "No data for query (no URL, no model)");
                throw new QueryExecException("No dataset description for query");
            
            String baseURI = query.getBaseURI() ;
            if ( baseURI == null )
                baseURI = IRIResolver.chooseBaseURI().toString() ;
            
            DatasetGraph dsg = DatasetUtils.createDatasetGraph(query.getDatasetDescription(), baseURI ) ;
            return dsg ;
        }
        
        @Override
        public void setInitialBinding(QuerySolution startSolution) { 
            initialBinding = startSolution ;
        }

        public Plan getPlan() {
            if ( plan == null ) {
                DatasetGraph dsg = prepareDataset(getDataset(), getQuery()) ;
                Binding inputBinding = null ;
                if ( initialBinding != null )
                    inputBinding = BindingUtils.asBinding(initialBinding) ;
                if ( inputBinding == null )
                    inputBinding = BindingRoot.create() ;

                plan = qeFactory.create(op, dsg, inputBinding, getContext()) ;
            }            
            return plan ;
        }


    }
}

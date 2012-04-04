package org.swows.test;

import java.util.List;
import java.util.Vector;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;

public class QueryTest {

    public static void main(final String[] args)  {

    	String baseUri = "/home/miguel/TUIO/circles/";
    	
//    	String defaultGraphUri = baseUri + "tuio_input_2.n3";
//    	
//		List<String> namedGraphUris = new Vector<String>();
//		namedGraphUris.add(baseUri + "viewPortInfo.n3");
//		namedGraphUris.add(baseUri + "config.n3");
//		namedGraphUris.add(baseUri + "circles.n3");
//		namedGraphUris.add(baseUri + "circleAges.n3");
//
//		Query query = QueryFactory.read(baseUri + "circleAgesTest.sparql");

    	String defaultGraphUri = baseUri + "circleAges.n3";
    	
		List<String> namedGraphUris = new Vector<String>();
		namedGraphUris.add(baseUri + "tuio_input_1.n3");
		namedGraphUris.add(baseUri + "config.n3");

		Query query = QueryFactory.read(baseUri + "circlesTest.sparql");

		//tuio_input_new.n3
    	Dataset inputDataset = DatasetFactory.create(defaultGraphUri, namedGraphUris);
    	QueryExecution qexec = QueryExecutionFactory.create(query, inputDataset) ;
    	Model resultModel = qexec.execConstruct() ;
    	qexec.close() ;
    	resultModel.write(System.out,"N3");
    }
    
}

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
package org.swows.test;

import java.util.List;
import java.util.Vector;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;

public class QueryTest {

    public static void main(final String[] args)  {

    	String baseUri = "/Users/miguel/Dropbox/Demo/normalize.sparql";
    	
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

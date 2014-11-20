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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.swows.function.Factory;
import org.swows.reader.ReaderFactory;
import org.swows.spinx.SpinxFactory;
import org.swows.vocabulary.SWI;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.sse.SSE;

public class FromWFtoDFtest {

    public static void main(final String[] args) throws TransformerException {
    	
    	String baseUri = "resources/sparql/fromWFtoDF/";
    	Query transfQuery = QueryFactory.read(baseUri + "first.rq");
    	     	
//    	String defaultGraphUri = baseUri + "tuio_input_new.n3";
//    	
//		List<String> namedGraphUris = new Vector<String>();
//		namedGraphUris.add(baseUri + "viewPortInfo.n3");
//		namedGraphUris.add(baseUri + "config.n3");
//		namedGraphUris.add(baseUri + "circleAges.n3");
//		namedGraphUris.add(baseUri + "circles.n3");

//    	String defaultGraphUri = baseUri + "circleAges.n3";
//    	String defaultGraphUri = baseUri + "defaultInput.n3";
//    	
//		List<String> namedGraphUris = new Vector<String>();
//		namedGraphUris.add(baseUri + "tuio_input.n3");
//		namedGraphUris.add(baseUri + "config.n3");
    	
//    	String defaultGraphUri = baseUri + "input.n3";
//		List<String> namedGraphUris = new Vector<String>();
//		namedGraphUris.add(baseUri + "range.n3");
		
//    	String defaultGraphUri = baseUri + "quantityHistoryStart_T.n3";
//		List<String> namedGraphUris = new Vector<String>();
		
//    	String defaultGraphUri = baseUri + "input.n3";
    	String defaultGraphUri = baseUri + "data/fao.rdf";
		List<String> namedGraphUris = new Vector<String>();
//		Model defaultModel = FileManager.get().loadModel(defaultGraphUri,defaultGraphUri,"http://www.swows.org/syntaxes/XML");
//		namedGraphUris.add(baseUri + "config.n3");
//		namedGraphUris.add(baseUri + "selectedPage.n3");

//		System.out.println();
//    	System.out.println("**************************");
//    	System.out.println("*** Input Model ***");
//    	System.out.println("**************************");
//    	defaultModel.write(System.out,"N3");
//    	System.out.println("****************************");
//    	System.out.println();

//		Dataset inputDataset = DatasetFactory.create(defaultGraphUri, namedGraphUris);
    	//Dataset inputDataset = DatasetFactory.create(defaultModel);
		Dataset inputDataset =
				DatasetFactory.create(
						ModelFactory.createModelForGraph(Graph.emptyGraph));
		
    	long queryStart, queryEnd;
    	
		queryStart = System.currentTimeMillis();
		QueryExecution queryExecution =
				QueryExecutionFactory.create(transfQuery, inputDataset);
		Model inputQueryResult = queryExecution.execConstruct();
		queryEnd = System.currentTimeMillis();
		System.out.println("Input Query execution time: " + (queryEnd - queryStart) );

		System.out.println();
    	System.out.println("**************************");
    	System.out.println("*** Input Query Result ***");
    	System.out.println("**************************");
    	inputQueryResult.write(System.out,"N3");
    	System.out.println("****************************");
    	System.out.println();
    	
//		queryStart = System.currentTimeMillis();
//		QueryExecution outQueryExecution =
//				QueryExecutionFactory.create(outputQuery, inputDataset);
//		Model outputQueryResult = outQueryExecution.execConstruct();
//		queryEnd = System.currentTimeMillis();
//		System.out.println("Output Query execution time: " + (queryEnd - queryStart) );
//		
//    	System.out.println();
//    	System.out.println("**************************");
//    	System.out.println("*** Output Query Result ***");
//    	System.out.println("**************************");
//    	outputQueryResult.write(System.out,"N3");
//    	System.out.println("****************************");
//    	System.out.println();

    }
		
}

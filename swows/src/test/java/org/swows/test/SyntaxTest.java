package org.swows.test;

import org.swows.graph.LoadGraph;
import org.swows.reader.ReaderFactory;

import com.hp.hpl.jena.graph.Graph;

public class SyntaxTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	String baseUri = "/home/miguel/pampers4/dataflow/";
//    	ReaderFactory.initialize();
		Graph test = new LoadGraph(
				baseUri + "flyingBoxesNew.sparql",
				null, "http://jena.hpl.hp.com/2003/07/query/SPARQL_11/Update", 1000);
	}

}

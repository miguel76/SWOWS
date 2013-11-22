package org.swows.processor;

import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

public interface ChannelData {
	
	public Graph asGraph();
	public Model asModel();
	
	public DatasetGraph asDatasetGraph();
	public Dataset asDataset();
	
	public Query asQuery();
	
	public Document asDocument();
	public void asSAX(ContentHandler contentHandler);

}

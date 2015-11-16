package org.swows.processor;

import org.apache.jena.graph.Graph;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.DatasetGraph;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;

public interface ChannelData {
	
	public Graph asGraph();
	public Model asModel();
	
	public DatasetGraph asDatasetGraph();
	public Dataset asDataset();
	
	public Query asQuery();
	
	public Document asDocument();
	public void asSAX(ContentHandler contentHandler);

}

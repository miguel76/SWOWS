package org.swows.processor;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;

public abstract class ChannelDataFromGraph implements ChannelData {
	
	@Override
	public Model asModel() {
		return ModelFactory.createModelForGraph(asGraph());
	}

	@Override
	public DatasetGraph asDatasetGraph() {
		return DatasetGraphFactory.createOneGraph(asGraph());
	}

	@Override
	public Dataset asDataset() {
		return DatasetFactory.create(ModelFactory.createModelForGraph(asGraph()));
	}

	@Override
	public Query asQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document asDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void asSAX(ContentHandler contentHandler) {
		// TODO Auto-generated method stub

	}

}

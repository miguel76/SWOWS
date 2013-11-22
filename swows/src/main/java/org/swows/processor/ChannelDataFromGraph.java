package org.swows.processor;

import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;

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

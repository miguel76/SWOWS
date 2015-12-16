package org.swows.provenance;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.swows.origin.GraphOriginManager;

public class ProvenanceTest {
	
    private static final Logger logger =
            Logger.getLogger(ProvenanceTest.class.getName());

    public static void main(String[] args) throws IOException {
	            
        String baseUri = "src/test/resources/transformation/";

        logger.info("Test started");
		
    	String defaultGraphUri = baseUri + "faoOntologySample.rdf";
   		List<String> namedGraphUris = new Vector<String>();
//		namedGraphUris.add(baseUri + "tuio_input_1.n3");
    	Dataset inputDataset = DatasetFactory.create(defaultGraphUri, namedGraphUris);
    	
        logger.info("Input Loaded");

    	String queryUri = baseUri + "countryValuesNoSrv.sparql";
    	Query query = QueryFactory.read(queryUri);
    	
        logger.info("Query loaded");
//        logger.info(query.toString());
        
//        Model outputModel = QueryExecutionFactory.create(query, inputDataset).execConstruct();
//        
//      	logger.info("Simple Query Executed");

//      	Provenance.addProvenanceToQuery(query);
//        
//        logger.info("Query transformed");
//        logger.info(query.toString());
    	        
//        Model outputModelWithProv = QueryExecutionFactory.create(query, inputDataset).execConstruct();
//        
//      	logger.info("Query with Provenance Executed");
//      	
//      	outputModelWithProv.write(System.out,"Turtle");

        Model provModel = ModelFactory.createDefaultModel();
        Model outputModel = ModelFactory.createDefaultModel();
        
//        Pair<Model,Model> outputModelAndProv = Provenance.execWithProvenance(query, inputDataset);
        Provenance.execConstructWithProvenance(query, inputDataset, outputModel, new GraphOriginManager(provModel.getGraph()));
      
    	logger.info("Query with Provenance Executed");
    	
    	outputModel.write(System.out,"Turtle");
    	
    	provModel.write(System.out,"Turtle");
    	
    }


}

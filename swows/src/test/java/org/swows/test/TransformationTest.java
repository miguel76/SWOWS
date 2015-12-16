package org.swows.test;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.swows.source.DatasetSource;
import org.swows.source.DatasetSourceFromDatasets;
import org.swows.transformation.Transformation;
import org.swows.transformation.TransformationRegistry;

public class TransformationTest {

    private static final Logger logger =
            Logger.getLogger(TransformationTest.class.getName());

    public static void main(String[] args) throws IOException {
		
//		String current = new java.io.File( "." ).getCanonicalPath();
//        System.out.println("Current dir:"+current);
//        String currentDir = System.getProperty("user.dir");
//        System.out.println("Current dir using System:" +currentDir);

//        logger.entering(getClass().getName(), "doIt");
//	    logger.exiting(getClass().getName(), "doIt");
	            
        logger.info("Test started");
		
		String baseUri = "src/test/resources/transformation/";

    	String defaultGraphUri = baseUri + "faoOntologySample.rdf";
   		List<String> namedGraphUris = new Vector<String>();
//		namedGraphUris.add(baseUri + "tuio_input_1.n3");
    	Dataset inputDataset = DatasetFactory.create(defaultGraphUri, namedGraphUris);
    	
        logger.info("Input Loaded");
    	
//    	System.out.println(inputDataset.getDefaultModel());
    	
    	String configGraphUri = baseUri + "countryValues.n3";
    	String configGraphRootUri =
    			"file://" +
    			new java.io.File( configGraphUri + "#dataflow" ).getCanonicalPath();
    	Dataset configDataset = DatasetFactory.create(configGraphUri);
    	Model configModel = configDataset.getDefaultModel();
    	
        logger.info("Transformation Loaded");

    	Transformation transformation =
    			TransformationRegistry.get().transformationFromGraph(
    					configModel.getGraph(),
    					configModel.createResource(configGraphRootUri).asNode() );
    	
    	logger.info("Transformation Built");
 
    	DatasetSource outputDatasetSource =
    			transformation.apply(new DatasetSourceFromDatasets(inputDataset.asDatasetGraph()) {
    				@Override
    				protected void readyForExecution() {
    					// TODO Auto-generated method stub
    				}
    			});
    	
//    	logger.info("Execution Prepared");
    	 
    	DatasetGraph outputDataset = outputDatasetSource.lastDataset();
      	logger.info("Transformation Executed");
          	
      	ModelFactory.createModelForGraph(outputDataset.getDefaultGraph()).write(System.out,"Turtle");

   	}

}

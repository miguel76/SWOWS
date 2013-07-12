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
package org.swows.parser;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.util.Stack;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * The Class ModelGenerator parses text files with a format derived
 * from SPARQL 1.1 text format and allowing full dataflow
 * specification.
 * Upon parsing, generates a new model, corresponding to the dataflow graph
 */
public class ModelGenerator extends Parser {

	private static String read(String input) throws Exception {
	    if (input.startsWith("{") && input.endsWith("}")) {
	    	return input.substring(1, input.length() - 1);
	    } else {
	    	byte buffer[] = new byte[(int) new java.io.File(input).length()];
	    	InputStream in = new java.io.FileInputStream(input);
	    	in.read(buffer);
	    	in.close();
	    	String content = new String(buffer, System.getProperty("file.encoding"));
	    	return content.length() > 0 && content.charAt(0) == '\uFEFF'
	    			? content.substring(1)
	    					: content;
	    }
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String args[]) throws Exception {
		if (args.length == 0) {
			System.out.println("Usage: java SparqlExt INPUT...");
			System.out.println();
			System.out.println("  parse INPUT, which is either a filename or literal text enclosed in curly braces\n");
	    }
	    else {
	    	for (String arg : args) {
	    		ModelGenerator parser = new ModelGenerator(read(arg),uri);
	    		try {
	    			parser.writeTrace("<?xml version=\"1.0\" encoding=\"UTF-8\"?" + ">\n<trace>\n");
	          //parser.writeOutput("<?xml version=\"1.0\" encoding=\"UTF-8\"?" + ">");
	    			parser.parse_GraphDefinitionUnit();
	    			parser.writeTrace("</trace>\n");
	    			parser.flushTrace();
	    		} catch (ParseException pe) {
	    			throw new RuntimeException(parser.getErrorMessage(pe));
	    		}
	    		Model resultModel = parser.getModel();
	    		Writer fw = new FileWriter("/home/miguel/out.n3");
	    		resultModel.write(fw,"N3");
	    	}
	    }
	}

	/* TODO: set the correct URI of namespace */
	private static String charUri = "http://www.w3.org/XML#";
	private static String uri = "http://www.swows.org/sparqlx#";

	private static Property charProperty = ResourceFactory.createProperty( charUri, "character" );
	private static Resource emptyResource = ResourceFactory.createResource();

	private String outputUri;
	private Model outputModel;

	/**
	 * Instantiates a new model generator.
	 *
	 * @param string the input string
	 * @param outputUri the uri used as namespace to build the model
	 */
	public ModelGenerator(String string, String outputUri) {
		super(string);
	    this.outputUri = outputUri;
	    outputModel = ModelFactory.createDefaultModel();
	    resourceStack = new Stack<Resource>();
	    resourceStack.push(outputModel.createResource(outputUri + "root"));
	}

	private Stack<Resource> resourceStack;
	//private String delayedTag = null;

	private final Property property( String local ) {
		return ResourceFactory.createProperty( outputUri, local );
	}

	/* (non-Javadoc)
	 * @see org.swows.parser.Parser#startNonterminal(java.lang.String)
	 */
	@Override
	protected void startNonterminal(String tag) {
/*
		if (delayedTag != null) {
	    	Resource innerResource = outputModel.createResource();
	    	Resource outerResource = resourceStack.peek();
	    	Property property = property(delayedTag);
	    	outputModel.add(outerResource, property, innerResource);
	    	resourceStack.push(innerResource);
	    }
	    delayedTag = tag;
*/
    	Resource innerResource = outputModel.createResource();
    	Resource outerResource = resourceStack.peek();
    	Property property = property(tag);
    	outputModel.add(outerResource, property, innerResource);
    	resourceStack.push(innerResource);
	 }

	/* (non-Javadoc)
	 * @see org.swows.parser.Parser#endNonterminal(java.lang.String)
	 */
	@Override
	protected void endNonterminal(String tag) {
/*
		if (delayedTag == null)
			resourceStack.pop();
		delayedTag = null;
*/
		resourceStack.pop();
	}

	  /* (non-Javadoc)
  	 * @see org.swows.parser.Parser#characters(int, int)
  	 */
  	@Override
	protected void characters(int begin, int end) {
  		/*
  		if (end <= size) {
  			Resource outerResource = resourceStack.peek();
			if (delayedTag != null) {
				Property property = property(delayedTag);
				outputModel.add(outerResource, property, input.substring(begin, end));
				resourceStack.push(emptyResource);
			} else {
				outputModel.add(outerResource, charProperty, input.substring(begin, end));
			}
			delayedTag = null;
  		}
  		*/
	}

	private void token(String tag, int begin, int end) {
		startNonterminal(null);
	}

	/* (non-Javadoc)
  	 * @see org.swows.parser.Parser#terminal(java.lang.String, int, int)
  	 */
  	@Override
	protected void terminal(String tag, int begin, int end) {
/*
  		if (tag.charAt(0) == '\'') 
			  token(tag, begin, end);
		  else {
			  startNonterminal(tag);
			  characters(begin, end);
			  endNonterminal(tag);
		  }
*/
  		if (tag.charAt(0) != '\'') { // Not a token
  			Resource outerResource = resourceStack.peek();
  			Property property = property(tag);
  			outputModel.add(outerResource, property, input.substring(begin, end));
  		}
	  }

	  /**
  	 * Gets the model.
  	 *
  	 * @return the model
  	 */
  	public Model getModel() {
		  return outputModel;
	  }

}

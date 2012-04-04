/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open Web Server (SWOWS).

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
import java.io.Writer;
import java.util.Stack;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * The Class ModelGenerator parses text files with a format derived
 * from SPARQL 1.1 text format and allowing full dataflow
 * specification.
 * Upon parsing, generates a new model, corresponding to the dataflow graph
 */
public class XMLGenerator extends Parser {

	private static String read(String input) throws Exception {
	    if (input.startsWith("{") && input.endsWith("}")) {
	    	return input.substring(1, input.length() - 1);
	    } else {
	    	byte buffer[] = new byte[(int) new java.io.File(input).length()];
	    	new java.io.FileInputStream(input).read(buffer);
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
	    		Writer fw = new FileWriter("/home/miguel/sax_out.xml");
	    		OutputFormat outputFormat =
	    				new OutputFormat("XML", null, true);
	    		ContentHandler contHandl = new XMLSerializer(fw, outputFormat); 
	    		//StreamResult strRes = new StreamResult(fw);
	    		XMLGenerator parser = new XMLGenerator(read(arg),uri,prefix,contHandl);
	    		try {
	    			parser.writeTrace("<?xml version=\"1.0\" encoding=\"UTF-8\"?" + ">\n<trace>\n");
	          //parser.writeOutput("<?xml version=\"1.0\" encoding=\"UTF-8\"?" + ">");
	    			parser.parse();
	    			parser.writeTrace("</trace>\n");
	    			parser.flushTrace();
	    		} catch (ParseException pe) {
	    			throw new RuntimeException(parser.getErrorMessage(pe));
	    		}
	    		//Model resultModel = parser.getModel();
	    		//resultModel.write(fw,"N3");
	    	}
	    }
	}

	/* TODO: set the correct URI of namespace */
	private static String charUri = "http://www.w3.org/XML#";
	private static String uri = "http://www.swows.org/sparqlx#";
	private static String prefix = "sparqlx";

	private static Property charProperty = ResourceFactory.createProperty( charUri, "character" );
	private static Resource emptyResource = ResourceFactory.createResource();

	private char charsInput[];
	private String outputUri;
	private String outputPrefix;
	private ContentHandler saxContHandler;
	private Stack<String> elemNameStack;
	
	/**
	 * Instantiates a new model generator.
	 *
	 * @param string the input string
	 * @param outputUri the uri used as namespace to build the model
	 */
	public XMLGenerator(String string, String outputUri, String outputPrefix, ContentHandler saxContHandler) {
		super(string);
		charsInput = new char[string.length()];
		string.getChars(0, string.length()-1, charsInput, 0);
	    this.outputUri = outputUri;
	    this.outputPrefix = outputPrefix;
	    this.saxContHandler = saxContHandler;
	    elemNameStack = new Stack<String>();
	}
	
	protected void parse() throws SAXException {
		saxContHandler.startDocument();
		saxContHandler.startPrefixMapping(outputPrefix, outputUri);
		try {
			parse_GraphDefinitionUnit();
		} catch(RuntimeException e) {
			if (e.getCause() instanceof SAXException)
				throw (SAXException) e.getCause();
			throw e;
		}
		saxContHandler.endPrefixMapping(outputPrefix);
		saxContHandler.endDocument();
	}

	Attributes emptyAttrList = new AttributesImpl();
	
	/* (non-Javadoc)
	 * @see org.swows.parser.Parser#startNonterminal(java.lang.String)
	 */
	@Override
	protected void startNonterminal(String tag) {
		try {
			saxContHandler.startElement(outputUri, tag, outputPrefix + ":" + tag, emptyAttrList);
		} catch(SAXException e) {
			throw new RuntimeException(e);
		}
		elemNameStack.push(tag);
	 }

	/* (non-Javadoc)
	 * @see org.swows.parser.Parser#endNonterminal(java.lang.String)
	 */
	@Override
	protected void endNonterminal(String tag) {
		try {
			saxContHandler.endElement(outputUri, tag, outputPrefix + ":" + tag);
		} catch(SAXException e) {
			throw new RuntimeException(e);
		}
		elemNameStack.pop();
	}

	  /* (non-Javadoc)
  	 * @see org.swows.parser.Parser#characters(int, int)
  	 */
  	@Override
	protected void characters(int begin, int end) {
  		if (end <= size) {
  			try {
  				saxContHandler.characters(charsInput, begin, end-begin);
  			} catch(SAXException e) {
  				throw new RuntimeException(e);
  			}
  		}
	}

	/* (non-Javadoc)
  	 * @see org.swows.parser.Parser#terminal(java.lang.String, int, int)
  	 */
  	@Override
	protected void terminal(String tag, int begin, int end) {
  		if (tag.charAt(0) == '\'') { 
  			startNonterminal("token");
  			characters(begin, end);
  			endNonterminal("token");
  		} else {
  			startNonterminal(tag);
  			characters(begin, end);
  			endNonterminal(tag);
		}
  	}

}

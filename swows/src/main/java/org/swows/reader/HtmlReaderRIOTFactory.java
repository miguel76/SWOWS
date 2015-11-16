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
package org.swows.reader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.LangBuilder;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.util.Context;
import org.swows.xmlinrdf.DomEncoder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class HtmlReaderRIOTFactory implements ReaderRIOTFactory {
	
//	public static final String XML_SYNTAX_URI = "http://www.swows.org/syntaxes/XML";

//	static boolean initialized = false;
	
	static {

		Lang lang =
        		LangBuilder
        		.create("HTML", "text/html")
        		.addFileExtensions("htm","html").build() ;
        // This just registers the name, not the parser.
        RDFLanguages.register(lang) ;
        
        // Register the parser factory.
        ReaderRIOTFactory factory = new HtmlReaderRIOTFactory() ;
        RDFParserRegistry.registerLangTriples(lang, factory) ;
		
	}
	
	protected static void initialize() {
//		RDFReaderFImpl.setBaseReaderClassName(XML_SYNTAX_URI, HtmlReaderRIOTFactory.class.getCanonicalName());

//        Lang lang =
//        		LangBuilder
//        		.create("HTML", "text/html")
//        		.addFileExtensions("htm","html").build() ;
//        // This just registers the name, not the parser.
//        RDFLanguages.register(lang) ;
//        
//        // Register the parser factory.
//        ReaderRIOTFactory factory = new HtmlReaderRIOTFactory() ;
//        RDFParserRegistry.registerLangTriples(lang, factory) ;
        
//        // Optional extra:
//        // If needed to set or override the syntax, register the name explicitly ...
//        System.out.println("## --") ;
//        IO_Jena.registerForModelRead("SSE", RDFReaderSSE.class) ;
//        // and use read( , "SSE")
//        Model model2 = ModelFactory.createDefaultModel().read(filename, "SSE") ;
//        model2.write(System.out, "TTL") ;
	}

	public ReaderRIOT create(Lang language) {
		return new ReaderRIOT() {
			
			public void read(
					InputStream in,
					String baseURI,
					ContentType ct,
					StreamRDF output,
					Context context) {
				
				InputSource xmlInputSource = new InputSource(in);
				xmlInputSource.setSystemId(baseURI);
				output.start();
				output.base(baseURI);
				try {
//					Document document = Jsoup.parse(in, ct.getCharset(), baseURI);
//					DOM2SAX dom2sax = new DOM2SAX(document);
//					dom2sax.setContentHandler( DomEncoder2.encode(baseURI, output) );
//					dom2sax.parse();
					XMLReader xmlReader = XMLReaderFactory.createXMLReader ("org.ccil.cowan.tagsoup.Parser");
					xmlReader.setContentHandler( DomEncoder.encode(baseURI, output) );
//					//xmlReader.setFeature("XML 2.0", true);
					xmlReader.parse(xmlInputSource);
				} catch(SAXException e) {
					throw (RuntimeException) new RuntimeException(e).fillInStackTrace();
				} catch (IOException e) {
					throw (RuntimeException) new RuntimeException(e).fillInStackTrace();
				}
				output.finish();
				// TODO: change with an HTML parser
			}
		};
	}

}

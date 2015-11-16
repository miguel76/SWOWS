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
package org.swows.reader.query;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.LangBuilder;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.system.ErrorHandler;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.util.Context;
import org.swows.spinx.SpinxFactory;

public class QueryReaderRIOTFactory implements ReaderRIOTFactory {
		
//		public static final String XML_SYNTAX_URI = "http://www.swows.org/syntaxes/XML";

//		static boolean initialized = false;
		
	static {

		Lang lang =
        		LangBuilder
        		.create("SPARQL", "application/sparql-query")
        		.addFileExtensions("rq","sparql").build() ;
        // This just registers the name, not the parser.
        RDFLanguages.register(lang) ;
        
        // Register the parser factory.
        ReaderRIOTFactory factory = new QueryReaderRIOTFactory() ;
        RDFParserRegistry.registerLangTriples(lang, factory) ;
		
	}

	public static void initialize() {}

	public ReaderRIOT create(Lang language) {
		return new ReaderRIOT() {
			
			public void read(
					InputStream in,
					String baseURI,
					ContentType ct,
					StreamRDF output,
					Context context) {
				
				StringWriter sw = new StringWriter();
				Reader reader = null;
				try {
					reader = (ct.getCharset() == null) ? new InputStreamReader(in) : new InputStreamReader(in, ct.getCharset());
					for( int currChar = reader.read(); currChar > 0; currChar = reader.read() )
						sw.write(currChar);
					reader.close();
					sw.flush();
					sw.close();
				} catch(IOException e) {
					throw new RiotException(e);
				} finally {
					if (reader != null)
						try {reader.close();} catch(IOException e) {}
				}

				org.apache.jena.query.Query query = QueryFactory.create(sw.toString(),baseURI);
				SpinxFactory.fromQuery(query, output, NodeFactory.createURI(baseURI)/*SWI.GraphRoot.asNode()*/);
			}

			@Override
			public void read(Reader reader, String baseURI, ContentType ct, StreamRDF output, Context context) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public ErrorHandler getErrorHandler() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setErrorHandler(ErrorHandler errorHandler) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public ParserProfile getParserProfile() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setParserProfile(ParserProfile profile) {
				// TODO Auto-generated method stub
				
			}
		};
	}

}

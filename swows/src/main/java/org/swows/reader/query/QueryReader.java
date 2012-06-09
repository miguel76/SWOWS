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

import java.io.Reader;
import java.io.StringWriter;
import java.util.Iterator;

import org.swows.spinx.SpinxFactory;
import org.swows.vocabulary.SWI;

import com.hp.hpl.jena.n3.JenaReaderBase;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.impl.RDFReaderFImpl;

public class QueryReader extends JenaReaderBase {

	private static final String PACKAGE_NAME = "org.swows.reader.query";
	private static final String CLASS_NAME_SUFF = "_QueryReader";

	private Syntax querySyntax;
	
	public QueryReader(Syntax querySyntax) {
		this.querySyntax = querySyntax;
	}
	
	public QueryReader(String querySyntaxId) {
		this(Syntax.querySyntaxNames.lookup(querySyntaxId));
	}
	
	public static void initialize() {
		Iterator<String> syntaxNamesIter = Syntax.querySyntaxNames.keys();
		while (syntaxNamesIter.hasNext()) {
//			addSyntaxWorker( syntaxNamesIter.next() );
			String queryId = syntaxNamesIter.next();
			Syntax querySyntax = Syntax.querySyntaxNames.lookup(queryId);
			String queryUri = querySyntax.getSymbol();
//			System.out.println("New query syntax: " + queryUri);
			RDFReaderFImpl.setBaseReaderClassName(
					queryUri,
					PACKAGE_NAME + "." + queryId.toUpperCase() + CLASS_NAME_SUFF);
		}
	}

	@Override
	protected void readWorker(Model model, Reader reader, String base)
			throws Exception {
		StringWriter sw = new StringWriter();
		for( int currChar = reader.read(); currChar > 0; currChar = reader.read() )
			sw.write(currChar);
		sw.flush();
		sw.close();
		com.hp.hpl.jena.query.Query query = QueryFactory.create(sw.toString(), querySyntax);
		SpinxFactory.fromQuery(query, model.getGraph(), SWI.GraphRoot.asNode());
	}

}

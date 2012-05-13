package org.swows.reader.query;

import java.io.Reader;
import java.io.StringWriter;
import java.util.Iterator;

import org.swows.spinx.SpinxFactory;
import org.swows.vocabulary.Instance;

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
		SpinxFactory.fromQuery(query, model.getGraph(), Instance.GraphRoot.asNode());
	}

}

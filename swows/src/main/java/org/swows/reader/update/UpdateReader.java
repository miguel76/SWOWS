package org.swows.reader.update;

import java.io.Reader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.swows.spinx.SpinxFactory;
import org.swows.vocabulary.Instance;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.n3.JenaReaderBase;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.impl.RDFReaderFImpl;
import com.hp.hpl.jena.update.Update;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

public class UpdateReader extends JenaReaderBase {

	private static final String PACKAGE_NAME = "org.swows.reader.update";
	private static final String CLASS_NAME_SUFF = "_UpdateReader";

    /** The syntax that the SPARQL working group has defined */
    public static final Syntax syntaxSPARQL_11 =
    		new Syntax("http://jena.hpl.hp.com/2003/07/query/SPARQL_11") {};
    
    /** The query syntax for extended SPARQL */ 
    public static final Syntax syntaxARQ =
    		new Syntax("http://jena.hpl.hp.com/2003/07/query/ARQ") {};

    /** The query syntax currently that is standardized, published, SPARQL - the "default SPARQL Query" */ 
    public static final Syntax syntaxSPARQL = syntaxSPARQL_11 ;

	private Syntax updateSyntax;
	
	public UpdateReader(Syntax updateSyntax) {
		this.updateSyntax = updateSyntax;
	}
	
	public UpdateReader(String updateSyntaxId) {
		this(Syntax.querySyntaxNames.lookup(updateSyntaxId));
	}
	
	public static void initialize() {
		Syntax.updateSyntaxNames.put("sparql",      syntaxSPARQL) ;
		Syntax.updateSyntaxNames.put("sparql_11",   syntaxSPARQL_11) ;
		Syntax.updateSyntaxNames.put("arq",         syntaxARQ) ;
		Iterator<String> syntaxNamesIter = Syntax.updateSyntaxNames.keys();
		while (syntaxNamesIter.hasNext()) {
//			addSyntaxWorker( syntaxNamesIter.next() );
			String updateId = syntaxNamesIter.next();
			Syntax updateSyntax = Syntax.updateSyntaxNames.lookup(updateId);
			String updateUri = updateSyntax.getSymbol() + "/Update";
			RDFReaderFImpl.setBaseReaderClassName(
					updateUri,
					PACKAGE_NAME + "." + updateId.toUpperCase() + CLASS_NAME_SUFF);
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
		Graph graph = model.getGraph();
		SpinxFactory.fromUpdateRequest( UpdateFactory.create(sw.toString(), updateSyntax), graph );
	}

}

package org.swows.reader;

import com.hp.hpl.jena.rdf.model.impl.RDFReaderFImpl;

public class RdfReaderFactory extends RDFReaderFImpl {

	private static final String RDF_SYNTAXES_BASE = "http://www.swows.org/syntaxes/RDF/";

	protected static void initialize() {
		for (String lang : LANGS)
			setBaseReaderClassName(RDF_SYNTAXES_BASE + lang, langToClassName.getProperty(lang) );
	}
}

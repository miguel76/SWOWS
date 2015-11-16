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

import org.apache.jena.rdf.model.impl.RDFReaderFImpl;

public class RdfReaderFactory extends RDFReaderFImpl {

	private static final String RDF_SYNTAXES_BASE = "http://www.swows.org/syntaxes/RDF/";

	protected static void initialize() {
		for (String lang : LANGS)
			setBaseReaderClassName(RDF_SYNTAXES_BASE + lang, langToClassName.getProperty(lang) );
	}
}

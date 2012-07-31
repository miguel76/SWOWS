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

import java.io.Reader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.swows.vocabulary.SWI;
import org.swows.xmlinrdf.DomEncoder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.n3.JenaReaderBase;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.impl.RDFReaderFImpl;

public class XmlReader extends JenaReaderBase {
	
	private static final String XML_SYNTAX_URI = "http://www.swows.org/syntaxes/XML";

	protected static void initialize() {
		RDFReaderFImpl.setBaseReaderClassName(XML_SYNTAX_URI, XmlReader.class.getCanonicalName());
	}

	@Override
	protected void readWorker(Model model, Reader reader, String base)
			throws Exception {
		readWorker(model.getGraph(), reader, base);
	}
	
	protected void readWorker(Graph graph, Reader reader, String base)
			throws Exception {
		InputSource xmlInputSource = new InputSource(reader);
		
		Document newDoc;
//		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setCoalescing(true);
			//docBuilderFactory.setFeature("XML 2.0", true);
			docBuilderFactory.setNamespaceAware(true);
			newDoc = docBuilderFactory.newDocumentBuilder().parse(xmlInputSource);
			newDoc.setDocumentURI(base);
			newDoc.getDocumentElement().setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:base", base);
			Graph newGraph = DomEncoder.encode(newDoc, SWI.GraphRoot.getURI());
			graph.getBulkUpdateHandler().add(newGraph);
//		} catch (SAXException e) {
//		} catch (IOException e) {
//		} catch (ParserConfigurationException e) {
//		}

	}

}

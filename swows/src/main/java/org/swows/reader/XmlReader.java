package org.swows.reader;

import java.io.Reader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.swows.vocabulary.Instance;
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
			newDoc.getDocumentElement().setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:base", base);
			Graph newGraph = DomEncoder.encode(newDoc, Instance.GraphRoot.getURI());
			graph.getBulkUpdateHandler().add(newGraph);
//		} catch (SAXException e) {
//		} catch (IOException e) {
//		} catch (ParserConfigurationException e) {
//		}

	}

}

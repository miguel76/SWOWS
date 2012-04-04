package org.swows.test;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.swows.xmlinrdf.DomEncoder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class XmlInRdfTest {

	public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
		String xmlUri = "/home/miguel/Scaricati/there.is.only.xul";
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(xmlUri);
		
		System.out.println("*** Input XML ***");
		
		XMLSerializer serializer = new XMLSerializer(System.out,new OutputFormat());
		// As a DOM Serializer
		serializer.asDOMSerializer();
		serializer.serialize( document );
		
		System.out.println("");
		System.out.println("*** Output RDF ***");
		
		Graph rdfGraph = DomEncoder.encode(document);
		Model model = ModelFactory.createModelForGraph(rdfGraph);
		model.write(System.out, "N3");
		//model.write(System.out);
	}

}

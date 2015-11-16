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
package org.swows.datatypes;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.Filter;
import org.apache.jena.vocabulary.RDF;
import org.swows.spinx.SpinxFactory;
import org.swows.vocabulary.SP;

public class SparqlJenaQuery {
	
//	private Query query;
//	private Graph queryGraph;
//	private Node queryNode;
	private String queryString = null;
	
/*
	public Sparql11Query(Query query) {
		this.query = query;
	}
*/
	
	//private static final String ROOT_URL = "#root";
	
	public static void developInRDF(Graph graph) {
		/*
		Iterator<Triple> triples = graph.find(null, RDF.type.asNode(), namespace.asNode());
		ModelFactory.createModelForGraph(graph);
		List<Triple> tripleList = new Vector<Triple>();
		while (triples.hasNext())
			tripleList.add(triples.next());
		triples = tripleList.iterator();
		while (triples.hasNext()) {
			Triple triple = triples.next();
		}
		*/
		Model model = ModelFactory.createModelForGraph(graph);
		Iterator<Resource> queryResources =
				model.listResourcesWithProperty(RDF.type, SP.Query)
				.andThen(model.listResourcesWithProperty(RDF.type, SP.Select))
				.andThen(model.listResourcesWithProperty(RDF.type, SP.Ask))
				.andThen(model.listResourcesWithProperty(RDF.type, SP.Describe))
				.andThen(model.listResourcesWithProperty(RDF.type, SP.Construct))
				.andThen(model.listResourcesWithProperty(RDF.type, SP.Update))
				.filterKeep( new Filter<Resource>() {
					@Override
					public boolean accept(Resource res) {
						StmtIterator stmtIterator = res.listProperties();
						boolean textProp = false;
						while (stmtIterator.hasNext()) {
							Property prop = stmtIterator.next().getPredicate();
							if (prop.equals(SP.text)) {
								// TODO: check for single literal string value of SP.text
								textProp = true;
							}
							else if (!prop.equals(RDF.type))
								return false;
						}
						return textProp;
					}
				});
		
		List<Resource> queryResourcesList = new Vector<Resource>();
		while (queryResources.hasNext())
			queryResourcesList.add(queryResources.next());
		queryResources = queryResourcesList.iterator();
		while (queryResources.hasNext()) {
			Resource queryRes = queryResources.next();
			String queryString = queryRes.getRequiredProperty(SP.text).getString();
			SparqlJenaQuery query = new SparqlJenaQuery(queryString);
			query.addRootedGraph(graph, queryRes.asNode());
		}
	}
	
	public SparqlJenaQuery(String queryString) {
		this.queryString = queryString;
	}
	
	public String toString() {
		return queryString;
	}
	
	/*
	@Override
	public Node addRootedGraph(Graph graph) {
		Model newModel = ModelFactory.createModelForGraph(graph);
		org.apache.jena.query.Query arq = ARQFactory.get().createQuery(newModel, queryString);
		ARQ2SPIN a2s = new ARQ2SPIN(newModel);
		String rootUrl = null;
		Resource rootResource = null;
		while (rootUrl == null || newModel.contains(rootResource, null) ) {
			rootUrl = URI + "#" + getRandomString();
			rootResource = newModel.getResource(rootUrl);
		}
		Query query = a2s.createQuery(arq, rootUrl);
		Resource newRootResource = newModel.createResource();
		//Resource newRootResource = newModel.wrapAsResource(node);
		Iterator<Statement> stmtIterator = newModel.listStatements(rootResource, (Property) null, (RDFNode) null);
		List<Statement> stmtList = new Vector<Statement>();
		while (stmtIterator.hasNext()) {
			Statement stmt = stmtIterator.next();
			stmtList.add(stmt);
		}
		((StmtIterator) stmtIterator).close();
		stmtIterator = stmtList.iterator();
		while (stmtIterator.hasNext()) {
			Statement stmt = stmtIterator.next();
			newModel.add(newRootResource, stmt.getPredicate(), stmt.getObject());
			newModel.remove(stmt);
		}
		return newRootResource.asNode();
	};
		*/
	
	public void addRootedGraph(Graph graph, Node rootNode) {
		//Model newModel = ModelFactory.createModelForGraph(graph);
		org.apache.jena.query.Query query = QueryFactory.create(queryString);
		SpinxFactory.fromQuery(query, graph, rootNode);
	};
	
	public boolean equals(Object obj2) {
		try {
			return queryString.equals( ((SparqlJenaQuery) obj2).queryString );
		} catch(ClassCastException e) {
			return false;
		}
	}

	public static final String URI = "http://www.swows.org/datatypes/sparql/jena";
	public static final Resource namespace = ResourceFactory.createResource("http://www.swows.org/datatypes/sparql/jena");
	public static final int RANDOM_HEX_SIZE = 8;
//	private static final Random random = new Random();
	
	static {
		TypeMapper.getInstance().registerDatatype(new Datatype());
	}
	
//	private static char nibble2hexDigit(byte input) {
//		input |= 15;
//		switch(input) {
//		case 10:
//			return 'a';
//		case 11:
//			return 'b';
//		case 12:
//			return 'c';
//		case 13:
//			return 'd';
//		case 14:
//			return 'e';
//		case 15:
//			return 'f';
//		default:
//			return Byte.toString(input).charAt(0);
//		}
//	}
	
//	private static String getRandomString() {
//		byte randomBytes[] = new byte[RANDOM_HEX_SIZE * 2];
//		random.nextBytes(randomBytes);
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < RANDOM_HEX_SIZE * 2; i++) {
//			sb.append( nibble2hexDigit( (byte) (randomBytes[i] >>> 8) ) );
//			sb.append( nibble2hexDigit( randomBytes[i] ) );
//		}
//		return sb.toString();
//	}
	
	private static class Datatype extends BaseDatatype {

		public Datatype() {
			super(URI);
		}
		
	    /**
	     * Convert a value of this datatype out
	     * to lexical form.
	     */
	    public String unparse(Object value) {
	    	return value.toString();
	    }

	    /**
	     * Parse a lexical form of this datatype to a value
	     * @throws DatatypeFormatException if the lexical form is not legal
	     */
	    public Object parse(String lexicalForm) throws DatatypeFormatException {
	        return new SparqlJenaQuery(lexicalForm);
	    }

	    /**
	     * Compares two instances of values of the given datatype.
	     */
	    public boolean isEqual(LiteralLabel value1, LiteralLabel value2) {
	        return value1.getDatatype() == value2.getDatatype()
	             && value1.getValue().equals(value2.getValue());
	    }

	}

}

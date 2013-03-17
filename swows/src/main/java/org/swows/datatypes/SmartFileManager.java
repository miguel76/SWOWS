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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.swows.graph.LoadGraph;
import org.swows.reader.ReaderFactory;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;
import org.swows.vocabulary.SPINX;
import org.swows.xmlinrdf.DomEncoder;

public class SmartFileManager {
	
//	private static FileManager globalFM = null;
//    private static SmartFileManager instanceFromGlobal = null;
//    
//    public static SmartFileManager get() {
//		ReaderFactory.initialize();
//    	if (globalFM == null || FileManager.get() != globalFM) {
//    		globalFM = FileManager.get();
//    		instanceFromGlobal = new SmartFileManager(globalFM);
//    	}
//    	return instanceFromGlobal;
//    }
//    
//    private static void smartClosure(Model model) {
//    	Graph newGraph = model.getGraph();
//    	SparqlJenaQuery.developInRDF(newGraph);
//    	DomEncoder.developInRDF(newGraph);
//    }
    
	private static class IncludedGraphData {
		URI node;
		String url, baseUri, syntax;
		public IncludedGraphData(URI Node, URI Url, URI baseUri, URI syntax) {
			this.node = Node;
			this.url = Url.stringValue();
			this.baseUri = (baseUri != null) ? baseUri.stringValue() : null;
			this.syntax = (syntax != null) ? syntax.stringValue() : LoadGraph.guessLang(this.url);
		}
	}
	
    public static void includeAllInAGraph(final Repository repository, final String contextUri) {
    	
		try {
			final List<IncludedGraphData> includedGraphsData = new Vector<IncludedGraphData>();
			RepositoryConnection con = repository.getConnection();
			try {
				TupleQuery tupleQuery =  con.prepareTupleQuery(
						QueryLanguage.SPARQL,
						"SELECT ?includedGraph ?url ?baseUri ?syntax FROM <" + contextUri + "> "
						+ "WHERE { ?includedGraph a <http://www.swows.org/dataflow#IncludedGraph>; df:url ?url. OPTIONAL{?includedGraph df:baseUri ?baseUri}. OPTIONAL{?includedGraph df:syntax ?syntax} }");
				tupleQuery.evaluate(new TupleQueryResultHandler() {
					@Override
					public void startQueryResult(List<String> arg0)
							throws TupleQueryResultHandlerException {}
					@Override
					public void handleSolution(BindingSet bindingSet)
							throws TupleQueryResultHandlerException {
						Value includedGraphValue = bindingSet.getValue("includedGraph");
						Value urlValue = bindingSet.getValue("url");
						Value baseUriValue = bindingSet.getValue("baseUri");
						Value syntaxValue = bindingSet.getValue("syntax");
//						if (includedGraphValue instanceof URI
//								&& urlValue instanceof URI
//								)
						includedGraphsData.add(new IncludedGraphData((URI) includedGraphValue, (URI) urlValue, (URI) baseUriValue, (URI) syntaxValue));
					}
					@Override
					public void endQueryResult() throws TupleQueryResultHandlerException {}
				});
			} finally {
				con.close();
			}
			for (IncludedGraphData includedGraphData : includedGraphsData) includeGraph(includedGraphData,repository,contextUri);
		} catch (OpenRDFException e) {
			throw new RuntimeException(e);
//		} catch (java.io.IOException e) {
//			throw new RuntimeException(e);
		}
    }

    private static void includeGraph(IncludedGraphData includedGraphData, Repository repository, final String parentContextUri) {
		try {
			Resource context = repository.getValueFactory().createURI(includedGraphData.url);
			RepositoryConnection con = repository.getConnection();
			try {
				con.add(new FileInputStream(includedGraphData.url), includedGraphData.baseUri, RDFFormat.N3, context);
				con.prepareUpdate(
						QueryLanguage.SPARQL, 
						"WITH <" + parentContextUri + "> "
								+ "DELETE {<" + includedGraphData.node + "> ?p ?o} "
								+ "INSERT {<" + includedGraphData.node + "> a <http://www.swows.org/dataflow#ImportedGraph>. "
								+ "<" + includedGraphData.node + "> <http://www.swows.org/dataflow#uri> <" + includedGraphData.url + ">.} "
								+ "WHERE { <" + includedGraphData.node + "> ?p ?o}");
			} finally {
				con.close();
			}
			includeAllInAGraph(repository, includedGraphData.url);
		} catch (OpenRDFException e) {
			throw new RuntimeException(e);
		} catch (java.io.IOException e) {
			throw new RuntimeException(e);
		}
    }

//    private FileManager baseFileManager;
//
//	public SmartFileManager(FileManager baseFileManager) {
//		this.baseFileManager = baseFileManager;
//	}
//	
//    /** @deprecated Use setLocationMapper */
//    @Deprecated
//    public void setMapper(LocationMapper _mapper) {
//    	baseFileManager.setMapper(_mapper);
//    }
//    
//    
//    /** Set the location mapping */
//    public void setLocationMapper(LocationMapper _mapper) {
//    	baseFileManager.setLocationMapper(_mapper);
//    }
//    
//    /** Get the location mapping */
//    public LocationMapper getLocationMapper() {
//    	return baseFileManager.getLocationMapper();
//    }
//    
//    /** Return an iterator over all the handlers */
//    public Iterator<Locator> locators() {
//    	return baseFileManager.locators() ;
//    }
//
//    /** Add a locator to the end of the locators list */ 
//    public void addLocator(Locator loc) {
//        baseFileManager.addLocator(loc);
//    }
//
//    /** Add a file locator */ 
//    public void addLocatorFile() {
//    	baseFileManager.addLocatorFile();
//    } 
//
//    /** Add a file locator which uses dir as its working directory */ 
//    public void addLocatorFile(String dir) {
//        baseFileManager.addLocatorFile(dir);
//    }
//    
//    /** Add a class loader locator */ 
//    public void addLocatorClassLoader(ClassLoader cLoad) {
//        baseFileManager.addLocatorClassLoader(cLoad);
//    }
//
//    /** Add a URL locator */
//    public void addLocatorURL() {
//    	baseFileManager.addLocatorURL();
//    }
//
//    /** Add a zip file locator */
//    public void addLocatorZip(String zfn) {
//    	baseFileManager.addLocatorZip(zfn);
//    }
//    
//    /** Remove a locator */ 
//    public void remove(Locator loc) {
//    	baseFileManager.remove(loc) ;
//    }
//
//    // -------- Cache operations
//    
//    /** Reset the model cache */
//    public void resetCache() {
//    	baseFileManager.resetCache();
//    }
//    
//    /** Change the state of model cache : does not clear the cache */ 
//    
//    public void setModelCaching(boolean state) {
//    	baseFileManager.setModelCaching(state);
//    }
//    
//    /** return whether caching is on of off */
//    public boolean getCachingModels() {
//    	return baseFileManager.getCachingModels();
//    }
//    
//    /** Read out of the cache - return null if not in the cache */ 
//    public Model getFromCache(String filenameOrURI) { 
//        return baseFileManager.getFromCache(filenameOrURI);
//    }
//    
//    public boolean hasCachedModel(String filenameOrURI) { 
//        return baseFileManager.hasCachedModel(filenameOrURI);
//    }
//    
//    public void addCacheModel(String uri, Model m) { 
//    	baseFileManager.addCacheModel(uri, m);
//    }
//
//    public void removeCacheModel(String uri) { 
//    	baseFileManager.removeCacheModel(uri);
//    }
//
//    // -------- Cache operations (end)
//
//    /** Load a model from a file (local or remote).
//     *  Guesses the syntax of the file based on filename extension, 
//     *  defaulting to RDF/XML.
//     *  @param filenameOrURI The filename or a URI (file:, http:)
//     *  @return a new model
//     *  @exception JenaException if there is syntax error in file.
//     */
//
//    public Model loadModel(String filenameOrURI) { 
//        return loadModel(filenameOrURI, null, null) ;
//    }
//
//
//    /** Load a model from a file (local or remote).
//     *  URI is the base for reading the model.
//     * 
//     *  @param filenameOrURI The filename or a URI (file:, http:)
//     *  @param rdfSyntax  RDF Serialization syntax. 
//     *  @return a new model
//     *  @exception JenaException if there is syntax error in file.
//     */
//
//    public Model loadModel(String filenameOrURI, String rdfSyntax) {
//        return loadModel(filenameOrURI, null, rdfSyntax) ;
//    }
//    
//    /** Load a model from a file (local or remote).
//     * 
//     *  @param filenameOrURI The filename or a URI (file:, http:)
//     *  @param baseURI  Base URI for loading the RDF model.
//     *  @param rdfSyntax  RDF Serialization syntax. 
//     *  @return a new model
//     *  @exception JenaException if there is syntax error in file.
//    */
//
//
//    public Model loadModel(String filenameOrURI, String baseURI, String rdfSyntax)
//    {
//        Model newModel = baseFileManager.loadModel(filenameOrURI, baseURI, rdfSyntax);
//    	smartClosure(newModel);
//        return newModel;
//    }
//    
//    /**
//     * Read a file of RDF into a model.  Guesses the syntax of the file based on filename extension, 
//     *  defaulting to RDF/XML.
//     * @param model
//     * @param filenameOrURI
//     * @return The model or null, if there was an error.
//     *  @exception JenaException if there is syntax error in file.
//     */    
//
//    public Model readModel(Model model, String filenameOrURI) {
//        return readModel(model, filenameOrURI, null);
//    }
//    
//    /**
//     * Read a file of RDF into a model.
//     * @param model
//     * @param filenameOrURI
//     * @param rdfSyntax RDF Serialization syntax.
//     * @return The model or null, if there was an error.
//     *  @exception JenaException if there is syntax error in file.
//     */    
//
//    public Model readModel(Model model, String filenameOrURI, String rdfSyntax)
//    {
//        return readModel(model, filenameOrURI, null, rdfSyntax);
//    }
//
//    /**
//     * Read a file of RDF into a model.
//     * @param model
//     * @param filenameOrURI
//     * @param baseURI
//     * @param syntax
//     * @return The model
//     *  @exception JenaException if there is syntax error in file.
//     */    
//
//    public Model readModel(Model model, String filenameOrURI, String baseURI, String syntax) {
//        Model newModel = baseFileManager.readModel(model, filenameOrURI, baseURI, syntax);
//    	smartClosure(newModel);
//        return newModel;
//    }
//    
//    /** Open a file using the locators of this FileManager */
//    public InputStream open(String filenameOrURI) {
//        return baseFileManager.open(filenameOrURI);
//    }
//
//
//    /** @deprecated Use mapURI */
//    @Deprecated
//    public String remap(String filenameOrURI) {
//    	return baseFileManager.remap(filenameOrURI);
//    }
//    
//    /** Apply the mapping of a filename or URI */
//    public String mapURI(String filenameOrURI) {
//        return baseFileManager.mapURI(filenameOrURI);
//    }
//    
//    /** Slurp up a whole file */
//    public String readWholeFileAsUTF8(InputStream in) {
//        return baseFileManager.readWholeFileAsUTF8(in);
//    }
//    
//    /** Slurp up a whole file: map filename as necessary */
//    public String readWholeFileAsUTF8(String filename) {
//        return baseFileManager.readWholeFileAsUTF8(filename);
//    }
//        
//    /** Open a file using the locators of this FileManager 
//     *  but without location mapping */ 
//    public InputStream openNoMap(String filenameOrURI) {
//        return baseFileManager.openNoMap(filenameOrURI);
//    }
//    
//    /** Open a file using the locators of this FileManager 
//     *  but without location mapping.
//     *  Return null if not found
//     */ 
//    public TypedStream openNoMapOrNull(String filenameOrURI) {
//        return baseFileManager.openNoMapOrNull(filenameOrURI); 
//    }

}

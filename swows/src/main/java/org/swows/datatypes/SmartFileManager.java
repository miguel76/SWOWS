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

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.shared.JenaException;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.LocationMapper;
import org.apache.jena.util.Locator;
import org.apache.jena.util.TypedStream;
import org.apache.jena.vocabulary.RDF;
import org.swows.graph.LoadGraph;
import org.swows.node.Skolemizer;
import org.swows.reader.ReaderFactory;
import org.swows.spinx.SpinxFactory;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;

public class SmartFileManager extends FileManager {
	
	private static FileManager globalFM = null;
    private static SmartFileManager instanceFromGlobal = null;
    
    public static SmartFileManager get() {
		ReaderFactory.initialize();
    	if (globalFM == null || FileManager.get() != globalFM) {
    		globalFM = FileManager.get();
    		instanceFromGlobal = new SmartFileManager(globalFM);
    	}
    	return instanceFromGlobal;
    }
    
    private static void smartClosure(Model model) {
    	Graph newGraph = model.getGraph();
    	SparqlJenaQuery.developInRDF(newGraph);
//    	DomEncoder.developInRDF(newGraph);
    }
    
    public static void includeAllInAGraph(final Graph graph, final DatasetGraph ds) {
    	List<Node> includedGraphNodes =
    			graph.find(
    					Node.ANY,
    					RDF.type.asNode(),
    					DF.IncludedGraph.asNode())
    				.mapWith(t -> t.getSubject())
    				.toList();
    	for (Node graphNode : includedGraphNodes) includeGraph(graph,graphNode,ds);
    }

    private static void includeGraph(final Graph graph, Node graphNode, DatasetGraph ds) {
		Node urlNode = GraphUtils.getSingleValueProperty( graph, graphNode, DF.url.asNode() );
		String filenameOrURI = null, baseURI = null, rdfSyntax = null;
		if (urlNode != null)
			filenameOrURI = urlNode.getURI();
		Node baseURINode = GraphUtils.getSingleValueOptProperty( graph, graphNode, DF.baseUri.asNode() );
		if (baseURINode != null)
			baseURI = baseURINode.getURI();
		Node syntaxNode = GraphUtils.getSingleValueOptProperty( graph, graphNode, DF.syntax.asNode() );
		if (syntaxNode != null)
			rdfSyntax = syntaxNode.getURI();
		else
			rdfSyntax = LoadGraph.guessLang(filenameOrURI);
		Graph loadedGraph =
				Skolemizer.getInstance().skolemize(
						SmartFileManager.get().loadModel(filenameOrURI,baseURI,rdfSyntax).getGraph());
		includeAllInAGraph(loadedGraph, ds);
		ds.addGraph(urlNode, loadedGraph);
    	List<Triple> triplesToDelete =
    			graph.find(graphNode, Node.ANY, Node.ANY).toList();
    	for (Triple t : triplesToDelete) graph.delete(t);
		graph.add(new Triple(graphNode, RDF.type.asNode(), DF.ImportedGraph.asNode()));
		graph.add(new Triple(graphNode, DF.uri.asNode(), urlNode));
    }
    
    private static void addReifiedGraph(
    		final Graph graph, final Node graphNode,
    		Graph includedGraph) {
    	graph.add(new Triple (graphNode, RDF.type.asNode(), DF.InlineGraph.asNode()));
    	Iterator<Node> iter = includedGraph.find(Node.ANY, Node.ANY, Node.ANY).mapWith(
    			new Function<Triple, Node>() {
					public Node apply(Triple t) {
						Node tripleNode = Skolemizer.getInstance().getNode();
				    	graph.add(new Triple (graphNode, DF.triple.asNode(), tripleNode));
				    	graph.add(new Triple (tripleNode, DF.subject.asNode(), t.getSubject()));
				    	graph.add(new Triple (tripleNode, DF.predicate.asNode(), t.getPredicate()));
				    	graph.add(new Triple (tripleNode, DF.object.asNode(), t.getObject()));
						return tripleNode;
					}
    			});
    	while (iter.hasNext());
//    	List<Node> nodesConfiguredBy =
//    			graph.find(Node.ANY, DF.config.asNode(), graphNode)
//    			.mapWith(new Map1<Triple, Node>() {
//					@Override
//					public Node map1(Triple t) { return t.getSubject(); }
//				}).toList();
//    	for (Node opNode : nodesConfiguredBy) {
//        	Node inputDataset =
//        			GraphUtils.getSingleValueOptProperty(graph, opNode, DF.input.asNode());
//    		if (graph.contains(opNode, RDF.type.asNode(), DF.ConstructGraph.asNode())) {
//    			// TODO: load query
//    		} else if (graph.contains(opNode, RDF.type.asNode(), DF.UpdatableGraph.asNode())) {
//    			// TODO: load update request
//    			
//    		} else if (graph.contains(opNode, RDF.type.asNode(), DF.DataflowGraph.asNode())) {
//    			// TODO: load dataflow
//    			
//    		}
//    	}
//    	if (graph.contains(Node.ANY, DF.input.asNode(), graphNode)) {
//    		// TODO: load reified
//    	}
    }

    public static void includeAllInAGraphSingle(final Graph graph) {
    	final Set<Triple> triplesToDelete = new HashSet<Triple>();
    	List<Node> includedGraphNodes =
    			graph.find(
    					Node.ANY,
    					RDF.type.asNode(),
    					DF.IncludedGraph.asNode()).mapWith(
    							new Function<Triple, Node>() {
    								public Node apply(Triple t) {
    									triplesToDelete.add(t);
    									Node graphNode = t.getSubject();
    									return graphNode;
    								}
    							}).toList();
    	for (Node graphNode : includedGraphNodes) includeGraphSingle(graph,graphNode);
    	for (Triple t : triplesToDelete) graph.delete(t);
    }

    private static void includeGraphSingle(final Graph graph, Node graphNode) {
    	final Set<Triple> triplesToDelete = new HashSet<Triple>();
		Node urlNode = GraphUtils.getSingleValueProperty( graph, graphNode, DF.url.asNode(), triplesToDelete );
		String filenameOrURI = null, baseURI = null, syntaxURI = null;
		if (urlNode != null)
			filenameOrURI = urlNode.getURI();
		Node baseURINode = GraphUtils.getSingleValueOptProperty( graph, graphNode, DF.baseUri.asNode(), triplesToDelete );
		if (baseURINode != null)
			baseURI = baseURINode.getURI();
		Node syntaxNode = GraphUtils.getSingleValueOptProperty( graph, graphNode, DF.syntax.asNode(), triplesToDelete );
		
		if (syntaxNode != null)
			syntaxURI = syntaxNode.getURI();
		else
			syntaxURI = LoadGraph.guessLang(filenameOrURI);
		
    	List<Node> nodesConfiguredBy =
    			graph.find(Node.ANY, DF.config.asNode(), graphNode)
    			.mapWith(new Function<Triple, Node>() {
					public Node apply(Triple t) {
						triplesToDelete.add(t);
						return t.getSubject();
					}
				}).toList();
    	for (Node opNode : nodesConfiguredBy) {
    		
        	Node inputDataset =
        			GraphUtils.getSingleValueOptProperty(graph, opNode, DF.input.asNode(), triplesToDelete );
        	Node defaultGraphNode = null;
        	Map<Node,Node> namedGraphs = null;
        	if (inputDataset != null) {
            	defaultGraphNode =
            			GraphUtils.getSingleValueOptProperty(graph, inputDataset, DF.input.asNode(), triplesToDelete);
            	Iterator<Node> namedInputNodes = GraphUtils.getPropertyValues(graph, inputDataset, DF.namedInput.asNode(), triplesToDelete);
            	namedGraphs = new HashMap<Node, Node>();
            	while (namedInputNodes.hasNext()) {
            		Node namedInputNode = namedInputNodes.next();
                	Node namedGraphNode =
                			GraphUtils.getSingleValueOptProperty(graph, namedInputNode, DF.input.asNode(), triplesToDelete);
//                	String graphName =
//                			GraphUtils.getSingleValueOptProperty(graph, namedInputNode, DF.id.asNode()).getURI();
                	Node graphNameNode =
                			GraphUtils.getSingleValueOptProperty(graph, namedInputNode, DF.id.asNode(), triplesToDelete);
                	namedGraphs.put(graphNameNode, namedGraphNode);
            	}
        	}
        	
        	if (graph.contains(opNode, RDF.type.asNode(), DF.ConstructGraph.asNode())) {
        		Iterator<Syntax> syntaxesIter = Syntax.querySyntaxNames.values();
        		Syntax syntax = null; 
        		System.out.println("syntaxURI: " + syntaxURI);
        		while (syntaxesIter.hasNext()) {
        			Syntax currSyntax = syntaxesIter.next();
        			System.out.println("currSyntax: " + currSyntax + " (" + currSyntax.getSymbol() + ")");
        			if (syntaxURI.equals(currSyntax.getSymbol())) {
        				syntax = currSyntax;
        				break;
        			}
        		}
        		if (syntax == null) throw new RuntimeException("Unexpected Null Syntax!");
    			org.apache.jena.query.Query query = QueryFactory.read(filenameOrURI, baseURI, syntax);
    			SpinxFactory.fromQuery(query, graph, opNode, defaultGraphNode, namedGraphs);
    	    	for (Triple t : triplesToDelete) graph.delete(t);
    		} else if (graph.contains(opNode, RDF.type.asNode(), DF.UpdatableGraph.asNode())) {
    			Iterator<Syntax> syntaxesIter = Syntax.querySyntaxNames.values();
    			Syntax syntax = null; 
    			System.out.println("syntaxURI: " + syntaxURI);
    			while (syntaxesIter.hasNext()) {
    				Syntax currSyntax = syntaxesIter.next();
    				System.out.println("currSyntax: " + currSyntax + " (" + currSyntax.getSymbol() + ")");
    				if (syntaxURI.equals(currSyntax.getSymbol()) || syntaxURI.equals(currSyntax.getSymbol() + "/Update")) {
    					syntax = currSyntax;
    					break;
    				}
    			}
    			if (syntax == null) throw new RuntimeException("Unexpected Null Syntax!");
    			UpdateRequest updateRequest = UpdateFactory.read(filenameOrURI, baseURI, syntax);
    			SpinxFactory.fromUpdateRequest(updateRequest, graph, opNode, defaultGraphNode, namedGraphs);
    	    	for (Triple t : triplesToDelete) graph.delete(t);
    		} else if (graph.contains(opNode, RDF.type.asNode(), DF.DataflowGraph.asNode())) {
    			// TODO: load dataflow
//    			Graph loadedGraph =
//    					Skolemizer.getInstance().skolemize(
//    							SmartFileManager.get().loadModel(filenameOrURI,baseURI,syntaxURI).getGraph());
//    			includeAllInAGraphSingle(loadedGraph);
        		Graph loadedGraph =
        				Skolemizer.getInstance().skolemize(
        						SmartFileManager.get().loadModel(filenameOrURI,baseURI,syntaxURI).getGraph());
        		includeAllInAGraphSingle(loadedGraph);
        		addReifiedGraph(graph, graphNode, loadedGraph);
    			
    		}
    	}
    	if (graph.contains(Node.ANY, DF.input.asNode(), graphNode)) {
    		Graph loadedGraph =
    				Skolemizer.getInstance().skolemize(
    						SmartFileManager.get().loadModel(filenameOrURI,baseURI,syntaxURI).getGraph());
    		//includeAllInAGraphSingle(loadedGraph);
    		addReifiedGraph(graph, graphNode, loadedGraph);
    	}
		
//    	List<Triple> triplesToDelete =
//    			graph.find(graphNode, Node.ANY, Node.ANY).toList();
    	
    }

    private FileManager baseFileManager;

	public SmartFileManager(FileManager baseFileManager) {
		this.baseFileManager = baseFileManager;
	}
	    
    /** Set the location mapping */
    public void setLocationMapper(LocationMapper _mapper) {
    	baseFileManager.setLocationMapper(_mapper);
    }
    
    /** Get the location mapping */
    public LocationMapper getLocationMapper() {
    	return baseFileManager.getLocationMapper();
    }
    
    /** Return an iterator over all the handlers */
    public Iterator<Locator> locators() {
    	return baseFileManager.locators() ;
    }

    /** Add a locator to the end of the locators list */ 
    public void addLocator(Locator loc) {
        baseFileManager.addLocator(loc);
    }

    /** Add a file locator */ 
    public void addLocatorFile() {
    	baseFileManager.addLocatorFile();
    } 

    /** Add a file locator which uses dir as its working directory */ 
    public void addLocatorFile(String dir) {
        baseFileManager.addLocatorFile(dir);
    }
    
    /** Add a class loader locator */ 
    public void addLocatorClassLoader(ClassLoader cLoad) {
        baseFileManager.addLocatorClassLoader(cLoad);
    }

    /** Add a URL locator */
    public void addLocatorURL() {
    	baseFileManager.addLocatorURL();
    }

    /** Add a zip file locator */
    public void addLocatorZip(String zfn) {
    	baseFileManager.addLocatorZip(zfn);
    }
    
    /** Remove a locator */ 
    public void remove(Locator loc) {
    	baseFileManager.remove(loc) ;
    }

    // -------- Cache operations
    
    /** Reset the model cache */
    public void resetCache() {
    	baseFileManager.resetCache();
    }
    
    /** Change the state of model cache : does not clear the cache */ 
    
    public void setModelCaching(boolean state) {
    	baseFileManager.setModelCaching(state);
    }
    
    /** Read out of the cache - return null if not in the cache */ 
    public Model getFromCache(String filenameOrURI) { 
        return baseFileManager.getFromCache(filenameOrURI);
    }
    
    public boolean hasCachedModel(String filenameOrURI) { 
        return baseFileManager.hasCachedModel(filenameOrURI);
    }
    
    public void addCacheModel(String uri, Model m) { 
    	baseFileManager.addCacheModel(uri, m);
    }

    public void removeCacheModel(String uri) { 
    	baseFileManager.removeCacheModel(uri);
    }

    // -------- Cache operations (end)

    /** Load a model from a file (local or remote).
     *  Guesses the syntax of the file based on filename extension, 
     *  defaulting to RDF/XML.
     *  @param filenameOrURI The filename or a URI (file:, http:)
     *  @return a new model
     *  @exception JenaException if there is syntax error in file.
     */

    public Model loadModel(String filenameOrURI) { 
        return loadModel(filenameOrURI, null, null) ;
    }


    /** Load a model from a file (local or remote).
     *  URI is the base for reading the model.
     * 
     *  @param filenameOrURI The filename or a URI (file:, http:)
     *  @param rdfSyntax  RDF Serialization syntax. 
     *  @return a new model
     *  @exception JenaException if there is syntax error in file.
     */

    public Model loadModel(String filenameOrURI, String rdfSyntax) {
        return loadModel(filenameOrURI, null, rdfSyntax) ;
    }
    
    /** Load a model from a file (local or remote).
     * 
     *  @param filenameOrURI The filename or a URI (file:, http:)
     *  @param baseURI  Base URI for loading the RDF model.
     *  @param rdfSyntax  RDF Serialization syntax. 
     *  @return a new model
     *  @exception JenaException if there is syntax error in file.
    */


    public Model loadModel(String filenameOrURI, String baseURI, String rdfSyntax)
    {
        Model newModel = baseFileManager.loadModel(filenameOrURI, baseURI, rdfSyntax);
    	smartClosure(newModel);
        return newModel;
    }
    
    /**
     * Read a file of RDF into a model.  Guesses the syntax of the file based on filename extension, 
     *  defaulting to RDF/XML.
     * @param model
     * @param filenameOrURI
     * @return The model or null, if there was an error.
     *  @exception JenaException if there is syntax error in file.
     */    

    public Model readModel(Model model, String filenameOrURI) {
        return readModel(model, filenameOrURI, null);
    }
    
    /**
     * Read a file of RDF into a model.
     * @param model
     * @param filenameOrURI
     * @param rdfSyntax RDF Serialization syntax.
     * @return The model or null, if there was an error.
     *  @exception JenaException if there is syntax error in file.
     */    

    public Model readModel(Model model, String filenameOrURI, String rdfSyntax)
    {
        return readModel(model, filenameOrURI, null, rdfSyntax);
    }

    /**
     * Read a file of RDF into a model.
     * @param model
     * @param filenameOrURI
     * @param baseURI
     * @param syntax
     * @return The model
     *  @exception JenaException if there is syntax error in file.
     */    

    public Model readModel(Model model, String filenameOrURI, String baseURI, String syntax) {
        Model newModel = baseFileManager.readModel(model, filenameOrURI, baseURI, syntax);
    	smartClosure(newModel);
        return newModel;
    }
    
    /** Open a file using the locators of this FileManager */
    public InputStream open(String filenameOrURI) {
        return baseFileManager.open(filenameOrURI);
    }


    /** Apply the mapping of a filename or URI */
    public String mapURI(String filenameOrURI) {
        return baseFileManager.mapURI(filenameOrURI);
    }
    
    /** Slurp up a whole file */
    public String readWholeFileAsUTF8(InputStream in) {
        return baseFileManager.readWholeFileAsUTF8(in);
    }
    
    /** Slurp up a whole file: map filename as necessary */
    public String readWholeFileAsUTF8(String filename) {
        return baseFileManager.readWholeFileAsUTF8(filename);
    }
        
    /** Open a file using the locators of this FileManager 
     *  but without location mapping */ 
    public InputStream openNoMap(String filenameOrURI) {
        return baseFileManager.openNoMap(filenameOrURI);
    }
    
    /** Open a file using the locators of this FileManager 
     *  but without location mapping.
     *  Return null if not found
     */ 
    public TypedStream openNoMapOrNull(String filenameOrURI) {
        return baseFileManager.openNoMapOrNull(filenameOrURI); 
    }

}

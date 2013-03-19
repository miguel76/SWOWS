package org.swows.rio;

/* 
 * Licensed to Aduna under one or more contributor license agreements.  
 * See the NOTICE.txt file distributed with this work for additional 
 * information regarding copyright ownership. 
 *
 * Aduna licenses this file to you under the terms of the Aduna BSD 
 * License (the "License"); you may not use this file except in compliance 
 * with the License. See the LICENSE.txt file distributed with this work 
 * for the full License.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.openrdf.model.BNode;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.util.GraphUtil;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerWrapper;

/**
 * An {@link RDFHandlerWrapper} that buffers statements internally and passes
 * them to underlying handlers grouped by context, then subject, then predicate.
 * 
 * @author Jeen Broekstra
 */
public class BufferedGroupStripingRDFHandler extends RDFHandlerWrapper {

	private Graph bufferedGraph;
	private Set<Resource> bufferedContexts;
//	private Set<BNode> bNodesA, bNodesB;

	private final Object bufferLock = new Object();
	
	/**
	 * Creates a new BufferedGroupedWriter that wraps the supplied handlers,
	 * using the supplied buffer size.
	 * 
	 * @param bufferSize
	 *        size of the buffer expressed in number of RDF statements
	 * @param handlers
	 *        one or more wrapped RDFHandlers
	 */
	public BufferedGroupStripingRDFHandler(RDFHandler... handlers) {
		super(handlers);
		this.bufferedGraph = new GraphImpl();
		this.bufferedContexts = new HashSet<Resource>();
//		this.bNodesA = new HashSet<BNode>();
//		this.bNodesB = new HashSet<BNode>();
	}

	@Override
	public void handleStatement(Statement st)
		throws RDFHandlerException
	{
		synchronized (bufferLock) {
			bufferedGraph.add(st);
			bufferedContexts.add(st.getContext());
		}
	}

	/*
	 * not synchronized, assumes calling method has obtained a lock on bufferLock
	 */
	private void processBuffer()
		throws RDFHandlerException	{
//		System.out.println("processBuffer() BEGIN");
			new Object() {
				{
//					System.out.println("Inner Class Constructor BEGIN");
					for (Resource context : bufferedContexts) {
						processContext(context);
					}
//					System.out.println("Inner Class Constructor END");
				}
				private Set<URI> getPredicates(Graph graph, Resource subject, Resource context) {
//					System.out.println("getPredicates(" + context + "," + subject + ") BEGIN");
					Iterator<Statement> statements = graph.match(subject, null, null, context);
					Set<URI> result = new HashSet<URI>();
					while (statements.hasNext()) {
						result.add(statements.next().getPredicate());
					}
//					System.out.println("getPredicates(" + context + "," + subject + ") END");
					return result;
				}
				private int inDegree(Resource context, Resource node) {
//					System.out.println("inDegree(" + context + "," + node + ") BEGIN");
					int count = 0;
//					System.out.println("going for match...");
					Iterator<Statement> stats = bufferedGraph.match(null, null, node, context);
//					System.out.println("matched!");
					while (stats.hasNext()) {
						stats.next();
						count++;
					}
//					System.out.println("Indegree " + count + " for node " + node);
//					System.out.println("inDegree(" + context + "," + node + ") END");
					return count;
				}
				private void processContext(Resource context) throws RDFHandlerException {
//					System.out.println("processContext(" + context + ") BEGIN");
					Set<Resource> subjects = GraphUtil.getSubjects(bufferedGraph, null, null, context);
//					System.out.println("Subjects loaded");
					for (Resource subject : subjects) {
//						System.out.println("Checking subject " + subject + "...");
						if (!(subject instanceof BNode) || !(inDegree(context, subject) == 1)) {
//							System.out.println("Checking passed! Processing...");
							processSubject(context,subject);
						}
					}
//					System.out.println("processContext(" + context + ") END");
				}
				
				private void processSubject(Resource context, Resource subject) throws RDFHandlerException {
//					System.out.println("processSubject(" + context + "," + subject + ") BEGIN");
					Set<URI> predicates = getPredicates(bufferedGraph, subject, context);
					if (predicates.contains(RDF.TYPE)) {
						processPredicate(context, subject, RDF.TYPE);
						predicates.remove(RDF.TYPE);
					}
					for (URI predicate : predicates) {
						processPredicate(context, subject, predicate);
					}
//					System.out.println("processSubject(" + context + "," + subject + ") END");
				}

				private void processPredicate(Resource context, Resource subject, URI predicate) throws RDFHandlerException {
					Iterator<Statement> statements = bufferedGraph.match(subject, predicate, null, context);
					while (statements.hasNext()) {
						Statement statement = statements.next();
						processStatement(context,statement);
					}
				}
				
				private void processStatement(Resource context, Statement statement) throws RDFHandlerException {
					BufferedGroupStripingRDFHandler.super.handleStatement(statement);
					Value object = statement.getObject();
					if (object instanceof BNode) {
						BNode bNode = (BNode) object;
						if (inDegree(context, bNode) == 1) {
							processSubject(context, bNode);
						}
					}
				}
				
			};
//			System.out.println("processBuffer() END");

	}
	
	@Override
	public void endRDF()
		throws RDFHandlerException
	{
//		System.out.println("endRDF() BEGIN");
		synchronized (bufferLock) {
//			System.out.println("endRDF() sync");
			processBuffer();
		}
		super.endRDF();
//		System.out.println("endRDF() END");
	}
}

/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open Web Server (SWOWS).

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
package org.swows.graph;

import java.util.Iterator;
import java.util.List;

import org.openjena.atlas.io.IndentedLineBuffer;
import org.openjena.atlas.iterator.Iter;
import org.openjena.atlas.iterator.Transform;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.shared.LockMRSW;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.sse.writers.WriterGraph;
import com.hp.hpl.jena.sparql.util.Context;

public abstract class DynamicDatasetCollection extends DynamicDataset {
	
    private final Lock lock = new LockMRSW() ;
    private Context context = new Context() ;

    @Override
    public void add(Node g, Node s, Node p, Node o)     { add(new Quad(g,s,p,o)) ; }  
    @Override
    public void delete(Node g, Node s, Node p, Node o)  { delete(new Quad(g,s,p,o)) ; }
    
    @Override
    public void removeGraph(Node graphName)
    { throw new UnsupportedOperationException("DatasetGraph.removeGraph") ; }

    @Override
    public void addGraph(Node graphName, DynamicGraph graph)
    { throw new UnsupportedOperationException("DatasetGraph.addGraph") ; }

    @Override
    public void setDefaultGraph(DynamicGraph g)
    { throw new UnsupportedOperationException("DatasetGraph.setDefaultGraph") ; }
    
    @Override
    /** Simple implementation */
    public void deleteAny(Node g, Node s, Node p, Node o)
    { 
        Iterator<Quad> iter = find(g, s, p, o) ;
        List<Quad> list = Iter.toList(iter) ;
        for ( Quad q : list )
            delete(q) ;
    }
    
    @Override
    public Iterator<Quad> find()
    { return find(Node.ANY, Node.ANY, Node.ANY, Node.ANY) ; }

    
    @Override
    public Iterator<Quad> find(Quad quad)
    { 
        if ( quad == null )
            return find() ;
        return find(quad.getGraph(), quad.getSubject(), quad.getPredicate(), quad.getObject()) ; }
    
    @Override
    public boolean contains(Quad quad) { return contains(quad.getGraph(), quad.getSubject(), quad.getPredicate(), quad.getObject()) ; }

    @Override
    public boolean contains(Node g, Node s, Node p , Node o)
    {
        Iterator<Quad> iter = find(g, s, p, o) ;
        boolean b = iter.hasNext() ;
        Iter.close(iter) ;
        return b ;
    }
    
    protected static boolean isWildcard(Node g)
    {
        return g == null || g == Node.ANY ;
    }
    
    @Override
    public boolean isEmpty()
    {
        return ! contains(Node.ANY, Node.ANY, Node.ANY, Node.ANY) ;
    }

    @Override
    public long size() { return -1 ; } 
    
    @Override
    public Lock getLock()
    {
        return lock ;
    }
    
    @Override
    public Context getContext()
    {
        return context ;
    }
    
    @Override
    public void close()
    { }
    
    @Override
    public String toString()
    {
        // Using the size of the graphs would be better.
        IndentedLineBuffer out = new IndentedLineBuffer() ;
        WriterGraph.output(out, this, null) ;
        return out.asString() ;
    }

    // Helpers
    
    protected static Iterator<Quad> triples2quadsDftGraph(Iterator<Triple> iter)
    {
        //return triples2quads(Quad.defaultGraphIRI, iter) ;
        return triples2quads(Quad.defaultGraphNodeGenerated, iter) ;
        //return triples2quads(Quad.tripleInQuad, iter) ;
    }

    protected static Iter<Quad> triples2quads(final Node graphNode, Iterator<Triple> iter)
    {
        Transform<Triple, Quad> transformNamedGraph = new Transform<Triple, Quad> () {
            @Override
            public Quad convert(Triple triple)
            {
                return new Quad(graphNode, triple) ;
            }
        } ;

        return Iter.iter(iter).map(transformNamedGraph) ;
    }

    @Override
    public boolean containsGraph(Node graphNode)
    { return contains(graphNode, Node.ANY, Node.ANY, Node.ANY) ; }

    @Override
    public void add(Quad quad)
    {
        DynamicGraph g = fetchGraph(quad.getGraph()) ;
        if ( g == null )
            System.err.println("null graph") ;
        
        g.add(quad.asTriple()) ;
    }

    @Override
    public void delete(Quad quad)
    {
    	DynamicGraph g = fetchGraph(quad.getGraph()) ;
        g.delete(quad.asTriple()) ;
    }
    
    @Override
    public Iterator<Quad> find(Node g, Node s, Node p , Node o)
    {
        if ( ! isWildcard(g) )
        {
            if ( Quad.isDefaultGraph(g))
                return findInDftGraph(s,p,o) ;
            Iterator<Quad> qIter = findInSpecificNamedGraph(g, s, p, o) ;
            if ( qIter == null )
                return Iter.nullIterator() ;
            return qIter ;
        }

        return findAny(s, p, o) ;
    }
    
    @Override
    public Iterator<Quad> findNG(Node g, Node s, Node p , Node o)
    {
        Iterator<Quad> qIter ;
        if ( ! isWildcard(g) )
            qIter = findInSpecificNamedGraph(g, s, p, o) ;
        else
            qIter = findInAnyNamedGraphs(s, p, o) ;
        if ( qIter == null )
            return Iter.nullIterator() ;
        return qIter ;
    }

    protected Iterator<Quad> findAny(Node s, Node p , Node o) 
    {
        // Default graph
        Iterator<Quad> iter1 = findInDftGraph(s, p, o) ;
        Iterator<Quad> iter2 = findInAnyNamedGraphs(s, p, o) ;

        if ( iter1 == null && iter2 == null )
            return Iter.nullIterator() ;
        // Copes with null in either position.
        return Iter.append(iter1, iter2) ;
    }

    protected Iterator<Quad> findInDftGraph(Node s, Node p , Node o)
    {
        return triples2quadsDftGraph(getDefaultGraph().find(s, p, o)) ;
    }
    
    protected Iter<Quad> findInSpecificNamedGraph(Node g, Node s, Node p , Node o)
    {
        DynamicGraph graph = fetchGraph(g) ;
        if ( g == null )
            return Iter.nullIter() ;
        return triples2quads(g, graph.find(s, p, o)) ;
    }

    protected Iterator<Quad> findInAnyNamedGraphs(Node s, Node p, Node o)
    {
        Iterator<Node> gnames = listGraphNodes() ;
        Iterator<Quad> iter = null ;
        // Named graphs
        for ( ; gnames.hasNext() ; )  
        {
            Node gn = gnames.next();
            Iterator<Quad> qIter = findInSpecificNamedGraph(gn, s, p, o) ;
            if ( qIter != null )
                // copes with null for iter
                iter = Iter.append(iter, qIter) ;
        }
        return iter ;
    }
    
    @Override
    public abstract Iterator<Node> listGraphNodes() ;

    protected DynamicGraph fetchGraph(Node gn)
    {
        if ( Quad.isDefaultGraph(gn) )
            return getDefaultGraph() ;
        else
            return getGraph(gn) ;
    }
}

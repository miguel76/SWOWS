package org.swows.origin;

import java.util.Collection;
import java.util.Iterator;

import org.apache.jena.sparql.core.Quad;

public interface OriginManager {
	
	public void addOrigin(Quad newQuad, Quad originQuad);
//	public void addOrigin(Quad newQuad, Collection<Quad> originQuads);
	public void addOrigin(Quad newQuad, Iterator<Quad> originQuads);
	public void addOrigin(Iterator<Quad> newQuads, Quad originQuad);
	public void addOrigin(Iterator<Quad> newQuads, Iterator<Quad> originQuads);
	
}

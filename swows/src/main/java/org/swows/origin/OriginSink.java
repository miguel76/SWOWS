package org.swows.origin;

import java.util.Iterator;

import org.apache.jena.sparql.core.Quad;

public interface OriginSink {

	public void addOrigin(Quad newQuad, Iterator<Quad> originQuads);
	public void addOrigin(Iterator<Quad> newQuads, Iterable<Quad> originQuads);
	
}

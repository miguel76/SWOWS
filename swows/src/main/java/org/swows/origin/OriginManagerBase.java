package org.swows.origin;

import java.util.Iterator;

import org.apache.jena.sparql.core.Quad;

public abstract class OriginManagerBase implements OriginManager {

	@Override
	abstract public void addOrigin(Quad newQuad, Iterator<Quad> originQuads);

	@Override
	public void addOrigin(Iterator<Quad> newQuads, Iterable<Quad> originQuads) {
		newQuads.forEachRemaining(newQuad -> addOrigin(newQuad, originQuads.iterator()));
	}

}

package org.swows.origin;

import java.util.Iterator;

import org.apache.jena.sparql.core.Quad;

public abstract class OriginManagerBase implements OriginManager {

	@Override
	abstract public void addOrigin(Quad newQuad, Quad originQuad);

	@Override
	public void addOrigin(Quad newQuad, Iterator<Quad> originQuads) {
		originQuads.forEachRemaining(originQuad -> addOrigin(newQuad, originQuad));
	}

	@Override
	public void addOrigin(Iterator<Quad> newQuads, Quad originQuad) {
		newQuads.forEachRemaining(newQuad -> addOrigin(newQuad, originQuad));
	}

	@Override
	public void addOrigin(Iterator<Quad> newQuads, Iterator<Quad> originQuads) {
		newQuads.forEachRemaining(newQuad -> addOrigin(newQuad, originQuads));
	}

}

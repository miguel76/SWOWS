package org.swows.origin;

import java.util.Iterator;

import org.apache.jena.sparql.core.Quad;

public interface OriginSource {

	public Iterator<Iterator<Quad>> getOrigin(Quad quad);

}

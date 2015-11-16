package org.swows.transformation;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;

public interface TransformationFactory {
	
	public Transformation transformationFromGraph(
			Graph configGraph, Node configRoot);
	
}

package org.swows.transformation;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

public interface TransformationFactory {
	
	public Transformation transformationFromGraph(Graph configGraph, Node configRoot);
	
}

package org.swows.origin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;

public interface RDFTermOrigin {
	
	public static class Element implements RDFTermOrigin {
		private Node node;
		public Element(Node node) {
			this.node = node;
		}
		public Node getNode() {
			return node;
		}
		@Override
		public boolean equals(Object o) {
			return (o instanceof Element && ((Element) o).node.equals(node));
		}
	}
	
	public static class Union implements RDFTermOrigin {
		private Set<RDFTermOrigin> origins;
		public Union(Set<RDFTermOrigin> origins) {
			this.origins = origins;
		}
		public Set<RDFTermOrigin> getMembers() {
			return origins;
		}
		@Override
		public boolean equals(Object o) {
			return (o instanceof Union && ((Union) o).origins.equals(origins));
		}
	}
	
	public static class Intersection implements RDFTermOrigin {
		private Set<RDFTermOrigin> origins;
		public Intersection(Set<RDFTermOrigin> origins) {
			this.origins = origins;			
		}
		public Set<RDFTermOrigin> getMembers() {
			return origins;
		}
	}
	
	public static RDFTermOrigin union(RDFTermOrigin[] origins) {
		Set<RDFTermOrigin> newOriginsSet = new HashSet<RDFTermOrigin>();
		for (RDFTermOrigin origin: origins) {
			if (origin != null) {
				if (origin instanceof Union) {
					newOriginsSet.addAll(((Union) origin).getMembers());
				} else if (origin instanceof Intersection) {
					
				}
			}
		}
		int size = newOriginsSet.size();
		switch (size) {
		case 0:
			return null;
		case 1:
			return newOriginsSet.iterator().next();
		default:
			return new Union(newOriginsSet);				
		}
	}

}

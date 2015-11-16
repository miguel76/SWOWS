package org.swows.graph.algebra;

import org.apache.jena.graph.Capabilities;

public class ReadOnlyStreamingCapabilities implements Capabilities {

	private static final Capabilities instance =
			new ReadOnlyStreamingCapabilities();
	
	public static Capabilities getInstance() {
		return instance;
	}
	
	private ReadOnlyStreamingCapabilities() {
		
	}
	
	public boolean sizeAccurate() {
		return false;
	}

	public boolean addAllowed() {
		return false;
	}

	public boolean addAllowed(boolean everyTriple) {
		return false;
	}

	public boolean deleteAllowed() {
		return false;
	}

	public boolean deleteAllowed(boolean everyTriple) {
		return false;
	}

	public boolean iteratorRemoveAllowed() {
		return false;
	}

	public boolean canBeEmpty() {
		return true;
	}

	public boolean findContractSafe() {
		return true;
	}

	public boolean handlesLiteralTyping() {
		return true;
	}

}

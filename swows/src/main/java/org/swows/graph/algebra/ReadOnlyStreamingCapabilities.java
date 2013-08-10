package org.swows.graph.algebra;

import com.hp.hpl.jena.graph.Capabilities;

public class ReadOnlyStreamingCapabilities implements Capabilities {

	private static final Capabilities instance =
			new ReadOnlyStreamingCapabilities();
	
	public static Capabilities getInstance() {
		return instance;
	}
	
	private ReadOnlyStreamingCapabilities() {
		
	}
	
	@Override
	public boolean sizeAccurate() {
		return false;
	}

	@Override
	public boolean addAllowed() {
		return false;
	}

	@Override
	public boolean addAllowed(boolean everyTriple) {
		return false;
	}

	@Override
	public boolean deleteAllowed() {
		return false;
	}

	@Override
	public boolean deleteAllowed(boolean everyTriple) {
		return false;
	}

	@Override
	public boolean iteratorRemoveAllowed() {
		return false;
	}

	@Override
	public boolean canBeEmpty() {
		return true;
	}

	@Override
	public boolean findContractSafe() {
		return true;
	}

	@Override
	public boolean handlesLiteralTyping() {
		return true;
	}

}

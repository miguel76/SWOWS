package org.swows.processor;

public interface Processor {
	
	public void addInput(String URI, InputChannel channel );
	public void addOutput(String URI, OutputChannel channel );
	public void addInputOutput(String URI, InputOutputChannel channel );
	
	public void deleteInput(String URI, InputChannel channel );
	public void deleteOutput(String URI, OutputChannel channel );
	public void deleteInputOutput(String URI, InputOutputChannel channel );

	public void writeOutput(String URI);
	
//	public void addInput(String URI);
//	public void addOutput(String URI);
//	public void addInputOutput(String URI);
//	
//	public void deleteInput(String URI);
//	public void deleteOutput(String URI);
//	public void deleteInputOutput(String URI);

}

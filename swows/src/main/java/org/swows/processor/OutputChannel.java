package org.swows.processor;

public interface OutputChannel {
	
	public void resetData(ChannelData data);
	public void updateData(ChannelDataUpdate update);

}

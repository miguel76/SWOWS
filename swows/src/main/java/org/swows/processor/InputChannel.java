package org.swows.processor;

public interface InputChannel {
	
	public ChannelData getData(int transactionId);
	public ChannelDataUpdate getDataUpdate(int transactionId);
	public void registerListener(InputChannelListener listener);
	public void unregisterListener(InputChannelListener listener);

}

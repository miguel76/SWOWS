package org.swows.processor;

public interface OutputChannel {
	
	public void notifyTransactionStart(int transactionId);
	public void resetData(int transactionId, ChannelData data);
	public void updateData(int transactionId, ChannelDataUpdate update);
	public void notifyTransactionEnd(int transactionId);

}

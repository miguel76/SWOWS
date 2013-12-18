package org.swows.processor;

public interface InputChannelListener {
	
	public void notifyTransactionStart(int transactionId);
	public void notifyDataUpdate(int transactionId);
	public void notifyTransactionEnd(int transactionId);

}

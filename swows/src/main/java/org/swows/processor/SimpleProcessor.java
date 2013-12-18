package org.swows.processor;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class SimpleProcessor implements Processor {
	
//	private Map<Integer, Map<String, InputChannel>> inputChannels =
//			new HashMap<>();
//	private Map<Integer, Map<String, OutputChannel>> outputChannels =
//			new HashMap<>();

	private Map<String, InputChannel> inputChannels = new HashMap<>();
	private Map<String, OutputChannel> outputChannels =	new HashMap<>();
			
	private Queue<Integer> transactionQueue = new ArrayDeque<>();
	private Set<Integer> closedTransactions = new HashSet<>();
	private Set<Integer> executableTransactions = new HashSet<>();
	private Map<Integer,Set<InputChannel>> transactionsDataMissing = new HashMap<>();
	
	private void notifyTransactionStartWorker(int transactionId) {
		for (OutputChannel channel : outputChannels.values() ) {
			channel.notifyTransactionStart(transactionId);
		}
	}

	private void notifyTransactionEndWorker(int transactionId) {
		for (OutputChannel channel : outputChannels.values() ) {
			channel.notifyTransactionEnd(transactionId);
		}
	}

	private void addInputWorker(String URI, final InputChannel channel) {
		inputChannels.put(URI, channel);
		channel.registerListener(new InputChannelListener() {
			
			@Override
			public synchronized void notifyTransactionStart(int transactionId) {
				Set<InputChannel> dataMissing = null;
				if (transactionQueue.isEmpty()
						|| !transactionQueue.contains(transactionId)) {
					transactionQueue.add(transactionId);
					dataMissing = new HashSet<InputChannel>();
					transactionsDataMissing.put(transactionId, dataMissing);
					notifyTransactionStartWorker(transactionId);
				} else {
					dataMissing = transactionsDataMissing.get(transactionId);
				}
				dataMissing.add(channel);
			}
			
			@Override
			public void notifyTransactionEnd(int transactionId) {
				if (!transactionQueue.isEmpty()) {
					if (transactionQueue.peek() == transactionId) {
						transactionQueue.poll();
						notifyTransactionEndWorker(transactionId);
					} else if (transactionQueue.contains(transactionId)) {
						closedTransactions.add(transactionId);
					}
				}
			}
			
			@Override
			public void notifyDataUpdate(int transactionId) {
				if (!transactionQueue.isEmpty()
						&& transactionQueue.contains(transactionId)) {
					Set<InputChannel> dataMissing = transactionsDataMissing.get(transactionId);
					dataMissing.remove(channel);
					if (dataMissing.isEmpty()) {
						update();
					}
				}
			}
		});
//		Map<String, InputChannel> transInputChannels = inputChannels.get(transactionId);
//		if (transInputChannels == null) {
//			transInputChannels = new HashMap<>();
//			inputChannels.put(transInputChannels);
//		} else {
//			
//		}
	}

	private void addOutputWorker(String URI, OutputChannel channel) {
		outputChannels.put(URI, channel);
	}

	@Override
	public synchronized void addInput(String URI, InputChannel channel) {
		addInputWorker(URI, channel);
	}

	@Override
	public synchronized void addOutput(String URI, OutputChannel channel) {
		addOutputWorker(URI, channel);
	}

	@Override
	public synchronized void addInputOutput(String URI, InputOutputChannel channel) {
		addInputWorker(URI, channel);
		addOutputWorker(URI, channel);
	}

	@Override
	public synchronized void deleteInput(String URI) {
		inputChannels.remove(URI);
	}

	@Override
	public synchronized void deleteOutput(String URI) {
		outputChannels.remove(URI);
	}

	@Override
	public synchronized void deleteInputOutput(String URI) {
		inputChannels.remove(URI);
		outputChannels.remove(URI);
	}

}

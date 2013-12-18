package org.swows.graph.events;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.hp.hpl.jena.graph.Graph;

public abstract class DynamicGraphBase implements DynamicGraph {

	private Queue<Transaction> transactionQueue =
			new ArrayDeque<Transaction>();
	private Set<Transaction> closedTransactions =
			new HashSet<Transaction>();
	private Map<Transaction,Queue<GraphUpdate>> updates =
			new HashMap<Transaction,Queue<GraphUpdate>>();
	
	protected synchronized void addTransaction(Transaction transaction) {
		transactionQueue.add(transaction);
		updates.put(transaction, new ArrayDeque<GraphUpdate>());
	}
			
	@Override
	public Transaction getCurrentTransaction() {
		return transactionQueue.peek();
	}

	protected synchronized Transaction endCurrentTransaction() {
		Transaction transactionDeleted = transactionQueue.poll();
		updates.remove(transactionDeleted);
		closedTransactions.remove(transactionDeleted);
		return transactionDeleted;
	}

	protected void registerListener(final DynamicGraph source) {
		source.getEventManager().register(
   			 new Listener() {
   					
   					public synchronized void notifyUpdate(Transaction transaction) {
   						DynamicGraphBase.this.notifyUpdate(source, transaction);
   					}

					@Override
					public void startTransaction(Transaction transaction) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void commit(Transaction transaction) {
						// TODO Auto-generated method stub
						
					}

   				}
   			);
		
	}
	
	private synchronized void notifyUpdate(DynamicGraph source, Transaction transaction) {
		notifyUpdateWorker(source, transaction);
	}
	
	protected abstract void notifyUpdateWorker(DynamicGraph source, Transaction transaction);

//	@Override
//	public Graph getCurrentGraph() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public GraphUpdate getCurrentGraphUpdate() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public EventManager getEventManager() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}

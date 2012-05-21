package org.swows.runnable;

public class RunnableContextFactory {
	
	private static RunnableContext defaultRunnableContext = new RunnableContext() {
		@Override
		public void run(Runnable runnable) {
//			runnable.run();
		}
	};
	
//	private static RunnableContext defaultRunnableContext = null;

	public static RunnableContext getDefaultRunnableContext() {
		return defaultRunnableContext;
	}
	
	public static void setDefaultRunnableContext(RunnableContext defaultRunnableContext) {
		RunnableContextFactory.defaultRunnableContext = defaultRunnableContext;
	}

}

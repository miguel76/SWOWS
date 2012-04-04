package org.swows.runnable;

import java.util.Timer;


public class LocalTimer {
	
	private static Timer singleTimer = null;
	
	public static Timer get() {
		if (singleTimer == null)
			singleTimer = new Timer();
		return singleTimer;
	}

}

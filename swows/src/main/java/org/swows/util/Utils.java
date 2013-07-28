package org.swows.util;

public class Utils {

	public static String standardStr(Object o) {
		return o.getClass().getName() + '@' + Integer.toHexString(o.hashCode());
	}
	
}

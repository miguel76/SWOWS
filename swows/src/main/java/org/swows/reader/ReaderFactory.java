package org.swows.reader;

import org.swows.reader.query.QueryReader;

public class ReaderFactory {
	
	static boolean initialized = false;
	
	public static void initialize() {
		if (!initialized) {
			QueryReader.initialize();
			RdfReaderFactory.initialize();
			XmlReader.initialize();
			initialized = true;
		}
	}
	
}

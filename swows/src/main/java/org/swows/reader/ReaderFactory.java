package org.swows.reader;

import org.swows.reader.query.QueryReader;
import org.swows.reader.update.UpdateReader;

public class ReaderFactory {
	
	static boolean initialized = false;
	
	public static void initialize() {
		if (!initialized) {
			QueryReader.initialize();
			UpdateReader.initialize();
			RdfReaderFactory.initialize();
			XmlReader.initialize();
			initialized = true;
		}
	}
	
}

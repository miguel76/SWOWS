/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open datatafloW System (SWOWS).

 * SWOWS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * SWOWS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General
 * Public License along with SWOWS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.swows.reader;

public class ReaderFactory {
	
	static boolean initialized = false;
	
	public static void initialize() {
		if (!initialized) {
//			QueryReader.initialize();
//			UpdateReader.initialize();
//			RdfReaderFactory.initialize();
//			XmlReader.initialize();
			XmlReaderRIOTFactory.initialize();
			initialized = true;
		}
	}
	
}

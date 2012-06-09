/*
 * Copyright (c) 2011 Dario
 * 

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
package org.swows.tuio;
import org.apache.batik.dom.events.DocumentEventSupport;
import org.apache.batik.dom.util.HashTable;
import org.w3c.dom.events.Event;
/**
 *
 * @author dario
 */
public class DocumentEventSupportTuio extends DocumentEventSupport {
     
    
    public static final String TUIO_EVENT = "tuioEvent";
    
    public HashTable eventFactories = new HashTable();
    {
        eventFactories.put(TUIO_EVENT.toLowerCase(), new TuioEventFactory ());
        System.out.println("documentEventSupportTuio " + (EventFactory)eventFactories.get(TUIO_EVENT.toLowerCase()));
    }
    //
    public static class TuioEventFactory implements EventFactory {
        public Event createEvent() {
             return new TuioEvent();
         }
    }
         
     }



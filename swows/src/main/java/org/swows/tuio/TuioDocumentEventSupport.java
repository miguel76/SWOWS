/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swows.tuio;
import org.w3c.dom.events.Event;
import org.apache.batik.dom.events.DOMMouseEvent;
import org.apache.batik.dom.events.DocumentEventSupport.EventFactory;
import org.apache.batik.dom.events.DocumentEventSupport;
import org.apache.batik.dom.util.HashTable;
/**
 *
 * @author dario
 */
public class TuioDocumentEventSupport extends DocumentEventSupport {
     
    public static final String TUIO_EVENT = "tuioEvent";
    
    protected HashTable eventFactories = new HashTable();
    {
        eventFactories.put(TUIO_EVENT, new TuioDocumentEventSupport ());
    }
    
    protected static class TuioEventFactory implements EventFactory {
        public Event createEvent() {
             return new org.swows.tuio.TuioEvent();
         }
    }
         
}



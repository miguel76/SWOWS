/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.swows.test;
import org.w3c.dom.events.Event;
import org.apache.batik.dom.events.DOMMouseEvent;
import org.apache.batik.dom.events.DocumentEventSupport.EventFactory;
import org.apache.batik.dom.events.DocumentEventSupport;
import org.apache.batik.dom.util.HashTable;
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
    
    public static class TuioEventFactory implements EventFactory {
        public Event createEvent() {
             return new TuioEventTest();
         }
    }
         
     }



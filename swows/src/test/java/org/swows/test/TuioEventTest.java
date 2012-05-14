package org.swows.test;

import java.util.HashSet;
import org.apache.batik.dom.events.DOMMouseEvent;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.*;
import org.apache.batik.dom.xbl.*;
import org.apache.batik.dom.events.DOMUIEvent;

public class TuioEventTest extends DOMMouseEvent  {
   //
   
         public void initTuioClickEvent (AbstractView defaultView, int x, int y) {
        this.initMouseEvent("tuioclick",true,false,defaultView,0,0,0,x,y,false,false,false,false,(short)0/*left button*/,null);
       
    }
    
}

    
    /*  public TuioEventTest () {
        super();
    } */

 
    
    
    
    
    
    
    
  


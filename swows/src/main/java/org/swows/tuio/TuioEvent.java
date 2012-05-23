package org.swows.tuio;

import java.util.HashSet;
import org.apache.batik.dom.events.DOMMouseEvent;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.events.*;
import org.apache.batik.dom.xbl.*;
import org.apache.batik.dom.events.DOMUIEvent;

import TUIO.TuioPoint;

public class TuioEvent extends DOMMouseEvent  {
   
	private TuioPoint tuioPoint;
   
    public void initTuioClickEvent (AbstractView defaultView, int x, int y, TuioPoint tuioPoint) {
        this.initMouseEvent("tuioclick",true,false,defaultView,0,0,0,x,y,false,false,false,false,(short)0/*left button*/,null);
        this.tuioPoint = tuioPoint;
    }
    
    public TuioPoint getTuioPoint() {
    	return tuioPoint;
    }
    
}


 
    
    
    
    
    
    
    
  


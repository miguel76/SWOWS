/*
 * Copyright (c) 2011 Dario

 * This file is part of Semantic Web Open Web Server (SWOWS).

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

import org.apache.batik.dom.events.DOMMouseEvent;
import org.w3c.dom.views.AbstractView;

import TUIO.TuioPoint;

public class TuioEvent extends DOMMouseEvent  {
   
	private TuioPoint tuioPoint;
   
    public void initTuioClickEvent (AbstractView defaultView, int x, int y, TuioPoint tuioPoint) {
        this.initMouseEvent("tuioEvent",true,false,defaultView,0,0,0,x,y,false,false,false,false,(short)0/*left button*/,null);
        this.tuioPoint = tuioPoint;
                   }
    
    public TuioPoint getTuioPoint() {
    	return tuioPoint;
    }
    
}


 
    
    
    
    
    
    
    
  


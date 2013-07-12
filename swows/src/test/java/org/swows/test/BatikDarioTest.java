/*
 * Copyright (c) 2011 Dario

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
package org.swows.test;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.batik.dom.events.DOMMouseEvent;
import org.apache.batik.dom.events.DocumentEventSupport;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.swows.tuio.TuioEvent;
import org.swows.tuio.TuioEventFactory;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.views.DocumentView;

import TUIO.TuioPoint;

public class BatikDarioTest extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BatikDarioTest() {
        JFrame frame = new JFrame("Prova");
        JPanel p = new JPanel(new BorderLayout());
        JSVGCanvas svgCanvas = new JSVGCanvas(null, true, true);
        svgCanvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);

        Element element = null;

        SVGDocument doc = null;

        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {

            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory sax = new SAXSVGDocumentFactory(parser);
            String uri = "file:///home/dario/NetBeansProjects/provaTavolo/test/pampersoriginal4/dataflow/svg/138/138.svg";

            doc = sax.createSVGDocument(uri);
            svgCanvas.setDocument(doc);
            element = doc.getDocumentElement();

            element.setNodeValue("svg");

            doc.adoptNode(element.cloneNode(true));

            AbstractView defaultView = ((DocumentView) doc).getDefaultView();
            String eventType = "tuioEvent";
            TuioEvent evt = new TuioEvent();
            DocumentEventSupport docSupport = new DocumentEventSupport();
            docSupport.registerEventFactory(eventType, new TuioEventFactory());
            docSupport.createEvent(eventType);
            TuioPoint tuioPoint = null;
            evt.initTuioClickEvent(defaultView, 86, 126, tuioPoint);
            EventTarget t = (EventTarget) element;
            
            
            t.addEventListener("tuioclick", new EventListener() {

                public void handleEvent(Event evt) {
                    
                    int clientX = -1;
                    int clientY = -1;
                   
                   // if (evt.getType().equals("tuioevent")){
                    TuioEvent devt = (TuioEvent) evt;
          
                    clientX = devt.getClientX();
                    clientY = devt.getClientY();
                   // }
                    
                   /* else if (evt.getType().equals(DocumentEventSupport.MOUSE_EVENT_TYPE.toLowerCase())){ 
                    DOMMouseEvent devt = (DOMMouseEvent) evt;
                    System.out.println("MOUSE EVENT");
                    clientX = devt.getClientX();
                    clientY = devt.getClientY();    
                    }*/
                    
                    //String newXString, newYString;
                    //int newX, newY                    
                    

                    System.out.println("tuio click");
                    System.out.printf("x = %d y = %d%n", clientX, clientY);
                    //dispatchMouseEvent("click", evt, true);    

                }
            }, false);

            t.addEventListener("click", new EventListener() {

                public void handleEvent(Event evt) {
                    
                    int clientX = -1;
                    int clientY = -1;
                     
                    DOMMouseEvent devt = (DOMMouseEvent) evt;
                    clientX = devt.getClientX();
                    clientY = devt.getClientY();    
                    
                    //String newXString, newYString;
                    //int newX, newY                    
                    

                    System.out.println("mouse click");
                    System.out.printf("x = %d y = %d%n", clientX, clientY);
                    //dispatchMouseEvent("click", evt, true);    

                }
            }, false);

            p.add(svgCanvas);
            svgCanvas.setVisible(true);
            frame.add(p);
            frame.validate();

            Thread.sleep(5000);
            while (true) {
                //while (true) {
                t.dispatchEvent(evt);
                Thread.sleep(3000);
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (java.lang.InterruptedException ex) {
            ex.printStackTrace();
        }

      
    }

    public static void main(String[] args) {
        new BatikDarioTest().setVisible(true);
    }
}
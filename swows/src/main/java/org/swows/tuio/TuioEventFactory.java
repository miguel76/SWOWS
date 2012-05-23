package org.swows.tuio;

import org.apache.batik.dom.events.DocumentEventSupport.EventFactory;
import org.w3c.dom.events.Event;

public class TuioEventFactory implements EventFactory {

	@Override
	public Event createEvent() {
		return new org.swows.tuio.TuioEvent();
	}

}

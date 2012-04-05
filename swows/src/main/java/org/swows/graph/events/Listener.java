package org.swows.graph.events;

import com.hp.hpl.jena.graph.Graph;

public interface Listener {

    public void notifyUpdate( Graph source, GraphUpdate update );

}

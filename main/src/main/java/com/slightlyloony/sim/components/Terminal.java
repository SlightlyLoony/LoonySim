package com.slightlyloony.sim.components;

import com.slightlyloony.sim.Net;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface Terminal {

    Net getNet();

    void setNet( final Net _net );

    Component getComponent();
}

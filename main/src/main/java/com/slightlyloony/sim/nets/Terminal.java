package com.slightlyloony.sim.nets;

import com.slightlyloony.sim.components.Component;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface Terminal {

    Component getComponent();

    Wire getWire();

    void setWire( final Wire _wire );
}

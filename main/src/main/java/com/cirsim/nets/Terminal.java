package com.cirsim.nets;

import com.cirsim.components.Component;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface Terminal {

    Component getComponent();

    Wire getWire();

    void setWire( final Wire _wire );
}

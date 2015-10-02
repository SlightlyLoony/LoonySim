package com.slightlyloony.sim.nets;

import com.slightlyloony.sim.components.Component;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Wire {


    private final Component component;
    private final Net net;


    public Wire( final Component _component, final Net _net ) {
        component = _component;
        net = _net;
    }


    public Component getComponent() {
        return component;
    }


    public Net getNet() {
        return net;
    }
}

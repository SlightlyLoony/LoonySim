package com.slightlyloony.sim.components;

import com.slightlyloony.sim.Net;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class ATerminal implements Terminal {


    protected Net net = null;
    final protected Component component;


    public ATerminal( final Component _component ) {
        if( _component == null )
            throw new IllegalArgumentException( "component, net, or name missing from ATerminal constructor" );
        component = _component;
    }


    public Net getNet() {
        return net;
    }


    public void setNet( final Net _net ) {
        net = _net;
    }


    public Component getComponent() { return component; }

}

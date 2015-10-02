package com.slightlyloony.sim.nets;

import com.slightlyloony.sim.components.Component;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class TerminalImpl implements Terminal {


    protected Wire wire;
    final protected Component component;


    public TerminalImpl( final Component _component ) {

        if( _component == null )
            throw new IllegalArgumentException( "Component from TerminalImpl constructor" );

        component = _component;
    }


    @Override
    public Wire getWire() {
        return wire;
    }


    @Override
    public void setWire( final Wire _wire ) {
        wire = _wire;
    }


    @Override
    public Component getComponent() { return component; }

}

package com.slightlyloony.sim.components;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface Component {

    boolean isVoltageSource();


    boolean isCurrentSource();


    Terminal getTerminal( final String _name );


    String getName();
}

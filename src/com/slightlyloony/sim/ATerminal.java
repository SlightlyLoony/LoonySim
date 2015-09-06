package com.slightlyloony.sim;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class ATerminal {


    protected ATerminal connectedTo;


    abstract boolean isVoltageSource();


    abstract boolean isCurrentSource();


    public void connect( final ATerminal _terminal ) {
        connectedTo = _terminal;
    }


    public ATerminal connectedTo() {
        return connectedTo;
    }
}

package com.slightlyloony.sim;

import java.util.List;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class ATwoTerminalComponent extends AComponent {

    public ATwoTerminalComponent( final Circuit _circuit, final boolean _voltageSource, final boolean _currentSource ) {
        super( _circuit, _voltageSource, _currentSource );
    }


    public ATerminal getOtherTerminal( final ATerminal _this ) {
        List<ATerminal> myTerminals = terminals.getAll();
        if( myTerminals.size() != 2 )
            throw new IllegalStateException( "Invalid number of terminals (not 2)" );
        return myTerminals.get( (_this == myTerminals.get( 0 )) ? 1 : 0);
    }
}

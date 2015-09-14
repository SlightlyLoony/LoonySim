package com.slightlyloony.sim.components;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class ATwoTerminalComponent extends AComponent {


    protected ATwoTerminalComponent( final String _name, final String[][] _terminalsSpec ) {
        super( _name, _terminalsSpec );
    }
}

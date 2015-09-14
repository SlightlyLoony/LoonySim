package com.slightlyloony.sim.components;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class AComponent implements Component {

    protected final String name;
    protected final Map<String, Terminal> terminals = new HashMap<>();


    protected boolean evaluated;


    protected AComponent( final String _name, final String[][] _terminalsSpec ) {

        name = _name;
        for( String[] terminalsSpec : _terminalsSpec ) {
            ComponentTerminal terminal = new ComponentTerminal( this );
            for( String terminalName : terminalsSpec ) {
                terminals.put( terminalName, terminal );
            }
        }
    }


    public Terminal getTerminal( final String _name ) {
        if( !terminals.containsKey( _name ))
            throw new IllegalStateException( "Terminal does not exist: " + name + "." + _name );
        return terminals.get( _name );
    }


    protected static class ComponentTerminal extends ATerminal implements Terminal {

        public ComponentTerminal( final Component _component ) {
            super( _component );
        }
    }


    public String getName() {
        return name;
    }
}

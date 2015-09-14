package com.slightlyloony.sim.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Terminals {

    private final Map<String, Terminal> terminals = new HashMap<>();


    public void add( final String _name, final Terminal _terminal ) {
        terminals.put( _name, _terminal );
    }


    public Terminal get( final String _name ) {
        Terminal result = terminals.get( _name );
        if( result == null )
            throw new IllegalArgumentException( "Terminal name does not exist" );
        return result;
    }


    public boolean has( final String _name ) {
        return terminals.containsKey( _name );
    }


    public List<Terminal> getAll() {
        return new ArrayList<>( terminals.values() );
    }


    public boolean isEmpty() {
        return terminals.isEmpty();
    }


    public int size() {
        return terminals.size();
    }
}

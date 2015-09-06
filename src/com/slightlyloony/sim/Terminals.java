package com.slightlyloony.sim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Terminals {

    private final Map<String, ATerminal> terminals = new HashMap<>();


    public void add( final ATerminal _terminal, final String... _names ) {
        for(String name : _names ) {
            terminals.put( name, _terminal );
        }
    }


    public ATerminal get( final String _name ) {
        ATerminal result = terminals.get( _name );
        if( result == null )
            throw new IllegalArgumentException( "Terminal name does not exist" );
        return result;
    }


    public boolean has( final String _name ) {
        return terminals.containsKey( _name );
    }

    public List<ATerminal> getAll() {
        return new ArrayList<>( terminals.values() );
    }


    public boolean isEmpty() {
        return terminals.isEmpty();
    }


    public int size() {
        return terminals.size();
    }
}

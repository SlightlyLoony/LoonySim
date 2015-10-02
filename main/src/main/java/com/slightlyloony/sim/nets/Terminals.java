package com.slightlyloony.sim.nets;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slightlyloony.sim.components.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Terminals {

    private final Map<String, Terminal> terminals;


    public static Terminals getTwoTerminalInstance( final Component _component ) {

        Map<String, Terminal> terminals = Maps.newHashMap();

        Terminal terminal1 = new TerminalImpl( _component );
        Lists.newArrayList( "1", "t", "top", "l", "left", "+", "plus" ).forEach( ( name ) -> {
            terminals.put( name, terminal1 );
        } );

        Terminal terminal2 = new TerminalImpl( _component );
        Lists.newArrayList( "2", "b", "bottom", "r", "right", "-", "minus" ).forEach( ( name ) -> {
            terminals.put( name, terminal2 );
        } );

        return new Terminals( terminals );
    }


    private Terminals( final Map<String, Terminal> _terminals ) {
        terminals = _terminals;
    }


    public Terminal get( final String _name ) {
        Terminal result = terminals.get( _name );
        if( result == null )
            throw new IllegalArgumentException( "Terminal name does not exist" );
        return result;
    }


    public Wire getWire( final String _name ) {

        if( !terminals.containsKey( _name ))
            throw new IllegalArgumentException( "No terminal by this name: " + _name );

        return terminals.get( _name ).getWire();
    }


    public Net getNet( final String _name ) {

        if( !terminals.containsKey( _name ))
            throw new IllegalArgumentException( "No terminal by this name: " + _name );

        return terminals.get( _name ).getWire().getNet();
    }


    public Terminal getOtherTerminal( final Terminal _terminal ) {

        if( terminals.size() != 2 )
            throw new IllegalStateException( "getOtherTerminal called with invalid number of terminals: " + terminals.size() );

        if( !terminals.containsValue( _terminal ) )
            throw new IllegalArgumentException( "Terminal does not exist in this component" );

        Collection<Terminal> ts = terminals.values();
        for( Terminal t : ts ) {
            if( t != _terminal )
                return t;
        }

        // this should be impossible to reach...
        return null;
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

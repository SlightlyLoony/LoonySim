package com.cirsim.values;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public enum Units {

    RESISTANCE( "o", "ohm", "ohms", "Ω" ),
    CAPACITANCE( "f", "farad", "farads" ),
    INDUCTANCE( "h", "henry", "henrys", "henries" ),
    POWER( "w", "watt", "watts" ),
    VOLTAGE( "v", "volt", "volts" );


    private final Set<String> unitStrings = Sets.newHashSet();


    public boolean isUnit( final String _description ) {
        return (_description != null) && unitStrings.contains( _description.toUpperCase() );
    }


    Units( final String... _names ) {
        for( String string : _names ) {
            unitStrings.add( string.toUpperCase() );
        }
    }
}

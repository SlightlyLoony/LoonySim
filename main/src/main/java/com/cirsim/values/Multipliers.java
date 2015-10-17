package com.cirsim.values;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public final class Multipliers {


    private static final Map<String, Double> multipliers = Maps.newHashMap();


    public static boolean contains( final String _key ) {

        if( _key == null )
            throw new IllegalArgumentException( "Key was null" );

        return multipliers.containsKey( _key.toUpperCase() );
    }


    public static Double get( final String _key ) {

        if( _key == null )
            throw new IllegalArgumentException( "Key was null" );

        return multipliers.get( _key.toUpperCase() );
    }


    static {
        addMultiplier( 1E-15, "f", "femto"         );
        addMultiplier( 1E-12, "p", "pico"          );
        addMultiplier( 1E-09, "n", "nano"          );
        addMultiplier( 1E-06, "u", "µ",    "micro" );
        addMultiplier( 1E-03, "m", "milli"         );
        addMultiplier( 1E00,  ""                   );
        addMultiplier( 1E03,  "k", "kilo"          );
        addMultiplier( 1E06,  "x", "meg",  "mega"  );
        addMultiplier( 1E09,  "g", "gig",  "giga"  );
        addMultiplier( 1E12,  "t", "tera"          );
    }


    private static void addMultiplier( double _multiplier, String... _aliases ) {

        if( (_aliases == null) || (_aliases.length == 0) )
            throw new IllegalStateException( "No aliases supplied" );

        // for each alias...
        for( String alias : _aliases ) {

            // force to upper case...
            alias = alias.toUpperCase();

            // if the alias is already recorded, we've got an error...
            if( multipliers.containsKey( alias ) )
                throw new IllegalStateException( "Alias used more than once: " + alias );

            multipliers.put( alias, _multiplier );
        }
    }
}

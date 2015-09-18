package com.slightlyloony.sim.util;

/**
 * String utility methids
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class S {


    public static int count( final String _s, final String _p ) {
        int c = 0;
        int i = 0;
        while( (i = _s.indexOf( _p, i )) >= 0 ) {
            c++;
            i++;
        }
        return c;
    }


    public static int count( final String _s, final char _c ) {
        return count( _s, Character.toString( _c ));
    }
}

package com.slightlyloony.sim.util;

/**
 * String utility methids
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class S {


    /**
     * Returns a string guaranteed to end with a newline character.  If the given string already ends wiht a newline, it is simply returned.
     * Otherwise a newline is appended to the given string.
     *
     * Throws <code>IllegalArgumentException</code> if the given string is <code>null</code>.
     *
     * @param _s the string to ensure a terminal newline on
     * @return the given string with a newline appended (if necessary) to guarantee a terminal newline
     */
    public static String ensureTerminalNewline( final String _s ) {

        if( _s == null)
            throw new IllegalArgumentException( "Null argument to S.ensureTerminalNewline()" );

        if( _s.isEmpty() || (_s.charAt( _s.length() - 1 ) != '\n') )
            return _s + "\n";

        return _s;
    }


    /**
     * Count the instances of the second given string that occur in the first given string.  For example, given "Blessed is Bessy" and "ss", this
     * method will return 2.  The comparison is case-sensitive.
     *
     * @param _s the string to look in for occurrences
     * @param _p the string to look for
     * @return the count of occurrences of _p in _s
     */
    public static int count( final String _s, final String _p ) {
        int c = 0;
        int i = 0;
        while( (i = _s.indexOf( _p, i )) >= 0 ) {
            c++;
            i++;
        }
        return c;
    }


    /**
     * Count the instances of the given character that occur in the given string.  For example, given "Blessed is Bessy" and 's', this method will
     * return 5.  The comparison is case-sensitive.
     *
     * @param _s the string to look in for occurrences
     * @param _c the character to look for
     * @return the count of occurrences of _c in _s
     */
    public static int count( final String _s, final char _c ) {
        return count( _s, Character.toString( _c ));
    }
}

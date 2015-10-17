package com.cirsim.values;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Tolerance implements Value {


    private static final Pattern PATTERN
            = Pattern.compile( "(?:(\\+\\-|\\+|\\-|)(\\d+(?:\\.\\d*)?))(?:(\\+|\\-)(\\d+(?:\\.\\d*)?))?%" );


    private final double lowerPercent;
    private final double higherPercent;


    public Tolerance( final double _lowerPercent, final double _higherPercent ) {
        lowerPercent = _lowerPercent;
        higherPercent = _higherPercent;
    }


    /**
     * Creates instances from strings in any of these formats (where "n" or "m" is any well-formed number):<br>
     * n%<br>
     * +-n%<br>
     * +n-m%<br>
     * n-m%<br>
     * -n+m%<br>
     * Units are ignored.
     *
     * @param _spec
     * @param _unit
     * @return
     */
    @Override
    public Tolerance getInstance( final String _spec, final Units _unit ) {

        // no spec, no instance (we don't care about the units)...
        if( _spec == null )
            return null;

        // see if the spec meets the sniff test...
        Matcher mat = PATTERN.matcher( _spec );
        if( !mat.matches() )
            return null;

        // see what we got in our match (it will be either one or two numbers with leading +s and -s)...
        String sign1 = mat.group( 1 );
        String numb1 = mat.group( 2 );
        String sign2 = mat.group( 3 );
        String numb2 = mat.group( 4 );

        // if we got just one number...
        if( sign2 == null ) {

            // we must have either no sign or a +- sign...
            if( "".equals( sign1 ) || "+-".equals( sign1 ) ) {

                double percent = Double.parseDouble( numb1 );
                return new Tolerance( -percent, percent );
            }
            else
                return null;
        }

        // or if we got two numbers...
        else {

            // some things are not allowed...
            if( "+-".equals( sign1 ) || sign1.equals( sign2 ) )
                return null;

            double p1 = Double.parseDouble( numb1 ) * ("-".equals( sign1 ) ? -1 : 1);
            double p2 = Double.parseDouble( numb2 ) * ("-".equals( sign2 ) ? -1 : 1);
            return new Tolerance( p1 < p2 ? p1 : p2, p1 < p2 ? p2 : p1 );
        }
    }


    @Override
    public Units getUnit() {
        return null;
    }


    public double getLowerPercent() {
        return lowerPercent;
    }


    public double getHigherPercent() {
        return higherPercent;
    }
}

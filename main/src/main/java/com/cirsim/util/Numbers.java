package com.cirsim.util;

/**
 * Static container class for numbers-related utility functions.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Numbers {

    public static final long MAX_POSITIVE_LONG = 0x7FFFFFFFFFFFFFFFL;


    /**
     * Compares the two given doubles and returns true if they are equal to within the given number of ulps (the value of the LSB of the mantissa).
     * Note that the magnitude of an ulp varies with the magnitude of a double's exponent.  If the two given numbers have differing exponents, then
     * the magnitude of the ulps will be mixed for the comparison.  For the purposes of a fuzzy match, this is not generally a problem.  This method
     * handles NAN and infinities in a sensible way, and also handles both +0 and -0 correctly.  For details,
     * see <a href="http://www.cygnus-software.com/papers/comparingfloats/comparingfloats.htm">this article</a> online.
     *
     * @param _a  one number to compare
     * @param _b  the other number to compare
     * @param _ulps the number of ulps the two given numbers may differ by and still be considered equal
     * @return true if the two given numbers are equal to within the given number of ulps
     */
    public static boolean nearlyEqual( final double _a, final double _b, final int _ulps ) {

        // get the exponent and mantissa of our two arguments, but not the sign bit...
        long a = MAX_POSITIVE_LONG & Double.doubleToRawLongBits( _a );
        long b = MAX_POSITIVE_LONG & Double.doubleToRawLongBits( _b );
        return _ulps >= java.lang.Math.abs( a - b );
    }
}

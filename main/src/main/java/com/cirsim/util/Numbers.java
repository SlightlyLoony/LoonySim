package com.cirsim.util;

/**
 * Static container class for numbers-related utility functions.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Numbers {


    /**
     * Compares the two given doubles and returns true if they are equal to within the given epsilon (see explanation below).  This method should
     * <i>never</i> be used to compare the result of a subtraction to zero (see discussion below for the reasons why).  Instead, the method should be
     * used to compare the operands <i>before</i> the subtraction.  The preceding caution is a specific example of a more general issue: this method
     * will return expected results only when the significands of the given numbers overlap.  In other words, when the difference between their
     * binary exponents is less than 53.  This method will also yield quite possibly unexpected results when the given operands are NANs or
     * Infinities.
     * <p>
     * For details on the issues that arise when comparing two floating point numbers,
     * see <a href="https://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/">this article</a> online.
     *<p>
     * This method provides one way to handle an awkwardness associated with using floating point number representation.  The root cause of the
     * challenge is that floating point numbers are usually inexact approximations of the number being represented.  For example, you might quite
     * reasonably expect that 1.000001 - 0.000001 would yield a result of 1.0, but in fact it does not.  That's because both operands in that
     * calculation are inexact representations of the value shown, and the difference between them is just a tiny bit different than the 1.0 you'd
     * expect.  While this tiny error is inconvenient and occasionally ugly from a human perspective, from the software's perspective it can be
     * downright disastrous.  To make a simple example, software often wants to check to see if a result is zero.  With operands of type double, it
     * is completely unsafe to simply write <code>if( a == 0 )</code> or similar code, as the value of "a" may be really close to zero, within the
     * margin of floating point approximation error, and the comparison would fail.
     *<p>
     * The solution implemented here is a sort of "fuzzy" comparison that doesn't look for precise equality, but rather equality within a certain
     * range.  That range is not an absolute value, like 0.01 &ndash; such a range would produce very unhappy results with numbers of various sizes.
     * For operands in the 10^100 range, that's far too small an error range.  For numbers in the 10^-100 range, that's far too big.  This solution
     * provides a relative range, expressed in "ulps" (Units in the Last Place), conventionally called "epsilon".  These units are the LSBs of the
     * significand, or mantissa, of the floating point numbers.  The magnitude of that LSBs value scales right along with the exponent of the
     * floating point numbers, making it perfectly relative no matter what the operands are.
     * <p>
     * One might reasonably imagine that instead of comparing for "near equality", one might instead subtract two numbers and test for the result
     * being near zero.  It turns out that this method leads to significant errors with large numbers.  Consider this simple case, where one number
     * is 1.0E100, and the other number is computed by adding 1.0E97 to itself 1000 times.  One would naturally expect those two numbers to be the
     * same, and if you subtract one from the other, you'd get an answer that was close to zero.  Instead, you get 5.2452060090094685E85,
     * which is a long way from zero!  Yet in the context of the two operands, it's actually a very close result - the two values differ by just
     * 27 ulps.  By doing the comparison for equality on the operands <i>before</i> they're subtracted, we can take the exponents of the operands
     * into account.  Of course, these operands may have different binary exponents.  So what's the magnitude of an ulp in that case?  It's a mix of
     * the two, based on the ratio of how far each operand is from the intervening exponent boundary.  It's really not important to understand
     * exactly what that means if you're willing to accept that the result is meaningful nonetheless.
     *
     * @param _a  one number to compare
     * @param _b  the other number to compare
     * @param _epsilon the number of ulps the two given numbers may differ by and still be considered equal
     * @return true if the two given numbers are equal to within the given number of ulps
     */
    public static boolean nearlyEqual( final double _a, final double _b, final int _epsilon ) {

        long rb_a = Double.doubleToRawLongBits( _a );
        long rb_b = Double.doubleToRawLongBits( _b );

        // if the signs of the operands are the same, then do a simple comparison of binary exponent and significand...
        if( (rb_a < 0) == (rb_b < 0) ) {

            // get the exponent and mantissa of our two arguments, but not the sign bit and return the result of comparing them...
            long a = Long.MAX_VALUE & rb_a;
            long b = Long.MAX_VALUE & rb_b;
            return _epsilon >= java.lang.Math.abs( a - b );
        }

        // the signs are different, so we need to check if both operands are close to zero...
        // get the exponents of our two arguments...
        int exp_a = (int) (0x7FF & (rb_a >> 52));
        int exp_b = (int) (0x7FF & (rb_b >> 52));
        if( (exp_a == 0) && (exp_b == 0) ) {

            // return the result of comparing the significands, taking the sign into account...
            long a = (rb_a < 0) ? -(Long.MAX_VALUE & rb_a) : Long.MAX_VALUE & rb_a;
            long b = (rb_b < 0) ? -(Long.MAX_VALUE & rb_b) : Long.MAX_VALUE & rb_b;
            return _epsilon >= java.lang.Math.abs( a + b );
        }

        // otherwise the operands cannot be nearly equal...
        return false;
    }


    /**
     * Subtracts the given doubles, returning the difference.  If the result would be within the given epsilon from zero, then an exactly zero double
     * is returned.
     *
     * @param _a the subtrahend
     * @param _b the minuend
     * @param _epsilon the maximum delta between the two values, in ulps, for the result to be converted to zero
     * @return the result of adding the two operands
     */
    public static double subtractWithZeroDetection( final double _a, final double _b, final int _epsilon ) {

        return nearlyEqual( _a, _b, _epsilon ) ? 0.0d : _a - _b;
    }


    /**
     * Adds the given doubles, returning the sum.  If the result would be within the given epsilon from zero, then an exactly zero double
     * is returned.
     *
     * @param _a one addend
     * @param _b the other addend
     * @param _epsilon the maximum delta between the two values, in ulps, for the result to be converted to zero
     * @return the result of adding the two operands
     */
    public static double addWithZeroDetection( final double _a, final double _b, final int _epsilon ) {

        return nearlyEqual( _a, -_b, _epsilon ) ? 0.0d : _a + _b;
    }


    public static int hash( final int _hash, final int _n ) {
        return ((_hash << 5) - _hash) ^ _n;
    }


    /**
     * Returns the integer that is the first even power of two that is equal to or larger than the given number.
     *
     * @param _a the number to find a binary power larger than
     * @return the integer that is the first even power of two that is equal to or larger than the given number
     */
    public static int closestBinaryPower( final int _a ) {

        if( _a < 1 )
            throw new IllegalArgumentException( "Negative argument not allowed: " + _a );

        if( _a > 0x40000000 )
            throw new IllegalArgumentException( "Argument is too large: " + _a );

        int hb = Integer.highestOneBit( _a );
        return  (hb == _a) ? hb : hb << 1;
    }
}

package com.cirsim.test;

import org.junit.Test;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class NumbersTest {

    @Test
    public void testNearlyEqual() throws Exception {

        double a = 1.0E100;
        double b = 0;
        for(int i = 0; i < 1000; i++)
            b += 1.0E97;
        double c = a - b;
        int expa = (int) (0x7FF & (Double.doubleToRawLongBits( a ) >> 52));
        int expc = (int) (0x7FF & (Double.doubleToRawLongBits( c ) >> 52));

        long rba = Double.doubleToRawLongBits( a );
        long x = rba * 31;
        long y = (rba << 5) - rba;
        hashCode();
    }


    @Test
    public void testNearlyZero() throws Exception {

    }
}
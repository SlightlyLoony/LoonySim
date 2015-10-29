package com.cirsim.test;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.cirsim.util.Numbers.closestBinaryPower;
import static com.cirsim.util.Numbers.closestBinaryPowerLog;
import static junit.framework.TestCase.assertEquals;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class NumbersTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

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
    public void testClosestBinaryPower() throws Exception {

        assertEquals( 256, closestBinaryPower(255) );
        assertEquals( 1, closestBinaryPower( 1 ) );
        assertEquals( 4096, closestBinaryPower( 4095 ) );
        assertEquals( 0x4000_0000, closestBinaryPower( 0x3FFF_FFFF ) );
    }


    @Test
    public void testClosestBinaryPowerEx1() throws Exception {

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Zero or negative argument not allowed: 0");

        closestBinaryPower( 0 );
    }


    @Test
    public void testClosestBinaryPowerEx2() throws Exception {

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Argument is too large: 1073741825");

        closestBinaryPower( 0x4000_0001 );
    }


    @Test
    public void testClosestBinaryPowerLog() throws Exception {

        assertEquals( 3, closestBinaryPowerLog( 6 ) );
        assertEquals( 4, closestBinaryPowerLog( 9 ) );
        assertEquals( 3, closestBinaryPowerLog( 8 ) );
        assertEquals( 12, closestBinaryPowerLog( 4095 ) );
        assertEquals( 10, closestBinaryPowerLog( 550 ) );
    }


    @Test
    public void testNearlyZero() throws Exception {

    }
}
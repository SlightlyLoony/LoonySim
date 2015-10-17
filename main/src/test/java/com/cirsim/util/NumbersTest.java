package com.cirsim.util;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class NumbersTest {

    @Test
    public void testNearlyEqual() throws Exception {

        double a = 1.000001;
        double b = 0.000001;
        double c = a - b;
        assertTrue( Numbers.nearlyEqual( 1.0, c, 1 ) );
    }
}
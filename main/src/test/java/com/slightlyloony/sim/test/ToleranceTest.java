package com.slightlyloony.sim.test;

import com.slightlyloony.sim.values.Tolerance;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ToleranceTest {

    @Test
    public void basic() {

        // factory...
        Tolerance f = new Tolerance( 0, 0 );

        Tolerance t = f.getInstance( "10%", null );
        assertEquals( -10, t.getLowerPercent(), 1e-20 );
        assertEquals( 10, t.getHigherPercent(), 1e-20 );

        t = f.getInstance( "+-10%", null );
        assertEquals( -10, t.getLowerPercent(), 1e-20 );
        assertEquals( 10, t.getHigherPercent(), 1e-20 );

        t = f.getInstance( "+50-20%", null );
        assertEquals( -20, t.getLowerPercent(), 1e-20 );
        assertEquals( 50, t.getHigherPercent(), 1e-20 );

        t = f.getInstance( "50-20%", null );
        assertEquals( -20, t.getLowerPercent(), 1e-20 );
        assertEquals( 50, t.getHigherPercent(), 1e-20 );

        t = f.getInstance( "-20+50%", null );
        assertEquals( -20, t.getLowerPercent(), 1e-20 );
        assertEquals( 50, t.getHigherPercent(), 1e-20 );

        t = f.getInstance( "+10%", null );
        assertNull( t );

        t = f.getInstance( "-10%", null );
        assertNull( t );

        t = f.getInstance( "10", null );
        assertNull( t );

        hashCode();
    }

}

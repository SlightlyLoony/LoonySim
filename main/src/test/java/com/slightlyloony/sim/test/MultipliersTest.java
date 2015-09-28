package com.slightlyloony.sim.test;

import com.slightlyloony.sim.values.Multipliers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class MultipliersTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void Basic() {

        exception = ExpectedException.none();

        assertTrue( Multipliers.contains( "f" ) );
        assertTrue( Multipliers.contains( "p" ) );
        assertTrue( Multipliers.contains( "n" ) );
        assertTrue( Multipliers.contains( "u" ) );
        assertTrue( Multipliers.contains( "m" ) );
        assertTrue( Multipliers.contains( "" ) );
        assertTrue( Multipliers.contains( "k" ) );
        assertTrue( Multipliers.contains( "x" ) );
        assertTrue( Multipliers.contains( "g" ) );
        assertTrue( Multipliers.contains( "t" ) );
        assertTrue( Multipliers.contains( "µ" ) );

        assertTrue( Multipliers.contains( "femto" ) );
        assertTrue( Multipliers.contains( "pico" ) );
        assertTrue( Multipliers.contains( "nano" ) );
        assertTrue( Multipliers.contains( "micro" ) );
        assertTrue( Multipliers.contains( "milli" ) );
        assertTrue( Multipliers.contains( "kilo" ) );
        assertTrue( Multipliers.contains( "meg" ) );
        assertTrue( Multipliers.contains( "mega" ) );
        assertTrue( Multipliers.contains( "gig" ) );
        assertTrue( Multipliers.contains( "giga" ) );
        assertTrue( Multipliers.contains( "tera" ) );

        assertFalse( Multipliers.contains( "q" ) );


        assertEquals( 1e-15, Multipliers.get( "f" ), 1E-20 );
        assertEquals( 1e-12, Multipliers.get( "p" ), 1E-20 );
        assertEquals( 1e-9, Multipliers.get( "n" ), 1E-20 );
        assertEquals( 1e-6, Multipliers.get( "u" ), 1E-20 );
        assertEquals( 1e-3, Multipliers.get( "m" ), 1E-20 );
        assertEquals( 1, Multipliers.get( "" ), 1E-20 );
        assertEquals( 1e3, Multipliers.get( "k" ), 1E-20 );
        assertEquals( 1e6, Multipliers.get( "x" ), 1E-20 );
        assertEquals( 1e9, Multipliers.get( "g" ), 1E-20 );
        assertEquals( 1e12, Multipliers.get( "t" ), 1E-20 );
        assertEquals( 1e-6, Multipliers.get( "µ" ), 1E-20 );

        assertEquals( 1e-15d, Multipliers.get( "femto" ), 1E-20d );
        assertEquals( 1e-12d, Multipliers.get( "pico" ), 1E-20d );
        assertEquals( 1e-9d, Multipliers.get( "nano" ), 1E-20d );
        assertEquals( 1e-6, Multipliers.get( "micro" ), 1E-20 );
        assertEquals( 1e-3, Multipliers.get( "milli" ), 1E-20 );
        assertEquals( 1e3, Multipliers.get( "kilo" ), 1E-20 );
        assertEquals( 1e6, Multipliers.get( "meg" ), 1E-20 );
        assertEquals( 1e6, Multipliers.get( "mega" ), 1E-20 );
        assertEquals( 1e9, Multipliers.get( "gig" ), 1E-20 );
        assertEquals( 1e9, Multipliers.get( "giga" ), 1E-20 );
        assertEquals( 1e12, Multipliers.get( "tera" ), 1E-20 );

        assertNull( Multipliers.get( "q" ) );
    }


    @Test
    public void Null1() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Key was null");
        Multipliers.contains( null );
    }


    @Test
    public void Null2() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Key was null");
        Multipliers.get( null );
    }
}
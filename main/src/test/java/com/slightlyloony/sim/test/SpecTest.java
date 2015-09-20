package com.slightlyloony.sim.test;

import com.slightlyloony.sim.CircuitFactory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class SpecTest {


    @Test
    public void SimpleTest() {
        try {
            CircuitFactory circuitFactory = new CircuitFactory();
            circuitFactory.addDir( new File( "circuits" ) );
            circuitFactory.load( "com.slightlyloony.sim.test.TestRun1" );

            hashCode();
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }
}

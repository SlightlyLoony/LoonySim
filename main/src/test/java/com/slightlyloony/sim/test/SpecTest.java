package com.slightlyloony.sim.test;

import com.slightlyloony.sim.ParseException;
import com.slightlyloony.sim.SimCompiler;
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
            SimCompiler compiler = new SimCompiler();
            compiler.addDir( new File( "circuits" ) );
            compiler.compile( "com.slightlyloony.sim.test.TestRun1" );

            hashCode();
        } catch( IOException e ) {
            e.printStackTrace();
        } catch( ParseException e ) {
            e.printStackTrace();
        }
    }
}

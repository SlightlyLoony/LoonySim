package com.slightlyloony.sim.test;

import com.slightlyloony.sim.Circuit;
import com.slightlyloony.sim.specs.CircuitSpec;
import org.junit.Test;

import java.io.*;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class SpecTest {


    @Test
    public void SimpleTest() {
        try( Reader spec = new InputStreamReader( new FileInputStream( new File( "src/test/java/com/slightlyloony/sim/test/TestSpec1.json" ) ) ) ) {
            CircuitSpec cs = CircuitSpec.constructFromStream( spec );
            Circuit c = new Circuit( cs );
            hashCode();
        }
        catch( IOException e ) {
            e.printStackTrace();
        };
    }
}

package com.slightlyloony.sim;

import com.slightlyloony.sim.specs.CircuitSpec;

import java.io.Reader;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Simulator {

    public void compile( final Reader _spec ) {
        CircuitSpec circuitSpec = CircuitSpec.constructFromStream( _spec );
    }
}

package com.slightlyloony.sim.components;

import com.slightlyloony.sim.Circuit;
import com.slightlyloony.sim.nets.Terminals;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class AComponent implements Component {

    protected Terminals terminals;
    protected final Circuit circuit;


    protected AComponent( final Circuit _circuit ) {
        circuit = _circuit;
    }


    public Terminals getTerminals() {
        return terminals;
    }


    public Circuit getCircuit() {
        return circuit;
    }
}

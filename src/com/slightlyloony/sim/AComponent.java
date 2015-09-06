package com.slightlyloony.sim;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class AComponent {

    protected final boolean voltageSource;
    protected final boolean currentSource;
    protected final Circuit circuit;
    protected final Terminals terminals = new Terminals();


    public AComponent( final Circuit _circuit, final boolean _voltageSource, final boolean _currentSource ) {
        circuit = _circuit;
        voltageSource = _voltageSource;
        currentSource = _currentSource;
    }


    public boolean isVoltageSource() {
        return voltageSource;
    }


    public boolean isCurrentSource() {
        return currentSource;
    }


    public Circuit getCircuit() {
        return circuit;
    }


    public ATerminal getTerminal( final String _name ) {
        return terminals.get( _name );
    }
}

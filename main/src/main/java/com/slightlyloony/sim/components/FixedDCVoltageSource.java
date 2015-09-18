package com.slightlyloony.sim.components;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class FixedDCVoltageSource extends ATwoTerminalComponent implements Component {


    private double volts;
    private Tolerance tolerance;


    protected FixedDCVoltageSource( final String _name, final String[][] _terminalsSpec ) {
        super( _name, _terminalsSpec );
    }


    @Override
    public boolean isVoltageSource() {
        return false;
    }


    @Override
    public boolean isCurrentSource() {
        return false;
    }


    public Tolerance getTolerance() {
        return tolerance;
    }


    public double getVolts() {
        return volts;
    }
}

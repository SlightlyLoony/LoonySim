package com.slightlyloony.sim.components;

import com.slightlyloony.sim.Net;
import com.slightlyloony.sim.specs.FixedDCVoltageSourceSpec;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class FixedDCVoltageSource extends ATwoTerminalComponent implements Component {

    public enum Kind { carbon_film, thick_film, thin_film, metal_film, metal_oxide_film, wire_wound, foil, other }

    private double volts;
    private Tolerance tolerance;


    public FixedDCVoltageSource( final FixedDCVoltageSourceSpec _spec ) {
        super( _spec.getName(), _spec.getTerminals() );
        volts = _spec.getValue();
        if( _spec.getToleranceSpec() != null )
            tolerance = new Tolerance( _spec.getToleranceSpec() );
    }


    @Override
    public boolean isVoltageSource() {
        return false;
    }


    @Override
    public boolean isCurrentSource() {
        return false;
    }


    protected Terminal createTerminal( final Net _net, final String _name ) {
        return new ResistorTerminal( this );
    }


    private static class ResistorTerminal extends ATerminal implements Terminal {

        public ResistorTerminal( final Component _component ) {
            super( _component );
        }
    }


    public Tolerance getTolerance() {
        return tolerance;
    }


    public double getVolts() {
        return volts;
    }
}

package com.slightlyloony.sim.components;

import com.slightlyloony.sim.Net;
import com.slightlyloony.sim.specs.ResistorSpec;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Resistor extends ATwoTerminalComponent implements Component {

    public enum Kind { carbon_film, thick_film, thin_film, metal_film, metal_oxide_film, wire_wound, foil, other }

    private double ohms;
    private double powerDissipation;
    private Tolerance tolerance;
    private Kind kind;


    public Resistor( final ResistorSpec _spec ) {
        super( _spec.getName(), _spec.getTerminals() );
        ohms = _spec.getValue();
        powerDissipation = _spec.getPower_dissipation();
        kind = _spec.getKind();
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


    public double getOhms() {
        return ohms;
    }


    public double getPowerDissipation() {
        return powerDissipation;
    }


    public Tolerance getTolerance() {
        return tolerance;
    }


    public Kind getKind() {
        return kind;
    }
}

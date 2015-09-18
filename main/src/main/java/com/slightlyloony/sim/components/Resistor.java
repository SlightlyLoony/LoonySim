package com.slightlyloony.sim.components;

import com.slightlyloony.sim.Net;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Resistor extends ATwoTerminalComponent implements Component {

    protected Resistor( final String _name, final String[][] _terminalsSpec ) {
        super( _name, _terminalsSpec );
    }


    public enum Kind { carbon_film, thick_film, thin_film, metal_film, metal_oxide_film, wire_wound, foil, other }

    private double ohms;
    private double powerDissipation;
    private Tolerance tolerance;
    private Kind kind;




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

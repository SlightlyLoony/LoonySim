package com.slightlyloony.sim;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Resistor extends ATwoTerminalComponent {

    private final double ohms;
    private final double powerDissipation;
    private final double tolerance;
    private final String type;

    public Resistor( final Circuit _circuit, double _ohms, double _powerDissipation, double _tolerance, String _type ) {
        super( _circuit, false, false );
        ohms = _ohms;
        powerDissipation = _powerDissipation;
        tolerance = _tolerance;
        type = _type;

        terminals.add( new ResistorTerminal(), "a", "1" );
        terminals.add( new ResistorTerminal(), "b", "2" );
    }


    private class ResistorTerminal extends ATerminal {

        @Override
        public boolean isVoltageSource() {
            return (ohms == 0) && (getOtherTerminal( this ).isVoltageSource());
        }


        @Override
        public boolean isCurrentSource() {
            return getOtherTerminal( this ).isCurrentSource();
        }
    }


    public double getOhms() {
        return ohms;
    }


    public double getPowerDissipation() {
        return powerDissipation;
    }


    public double getTolerance() {
        return tolerance;
    }


    public String getType() {
        return type;
    }
}

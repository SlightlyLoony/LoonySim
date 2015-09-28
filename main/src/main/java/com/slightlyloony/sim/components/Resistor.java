package com.slightlyloony.sim.components;

import com.google.common.collect.Sets;
import com.slightlyloony.sim.Circuit;
import com.slightlyloony.sim.Net;
import com.slightlyloony.sim.Token;
import com.slightlyloony.sim.values.*;

import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Resistor extends ATwoTerminalComponent implements Component {


    private AScalar      resistance;
    private AScalar      powerDissipation;
    private Tolerance    tolerance;
    private ResistorType resistorType;



    public Resistor( final ListIterator<Token> _tokenIterator, final Circuit _circuit ) {
        super( _tokenIterator, _circuit );

        // get our default values, which also creates factories...
        Set<Value> defaults = Sets.newHashSet(
                new AssumedUnit( Double.POSITIVE_INFINITY, Units.RESISTANCE ),
                new RequiredUnit( 0.25, Units.POWER ),
                new Tolerance( 20, 20 ),
                new ResistorType( ResistorTechnology.CARBON_FILM ) );

        // get any specified values, along with defaults for unspecified values...
        Map<Class<? extends Value>, Value> values = ValuesFactory.getValues( defaults, _tokenIterator, _circuit );

        // store the values we got...
        resistance       = (AssumedUnit)  values.get( AssumedUnit.class  );
        powerDissipation = (RequiredUnit) values.get( RequiredUnit.class );
        tolerance        = (Tolerance)    values.get( Tolerance.class    );
        resistorType     = (ResistorType) values.get( ResistorType.class );

        hashCode();
    }


    public boolean isVoltageSource() {
        return false;
    }


    public boolean isCurrentSource() {
        return false;
    }


    protected Terminal createTerminal( final Net _net, final String _name ) {
        return new ResistorTerminal( this );
    }


    @Override
    public Units getExpectedUnit() {
        return Units.RESISTANCE;
    }


    private static class ResistorTerminal extends ATerminal implements Terminal {

        public ResistorTerminal( final Component _component ) {
            super( _component );
        }
    }


    public AScalar getResistance() {
        return resistance;
    }


    public AScalar getPowerDissipation() {
        return powerDissipation;
    }


    public Tolerance getTolerance() {
        return tolerance;
    }


    public ResistorType getResistorType() {
        return resistorType;
    }
}

package com.cirsim.components;

import com.cirsim.Circuit;
import com.cirsim.nets.Terminals;
import com.cirsim.values.*;
import com.google.common.collect.Sets;
import com.cirsim.Token;

import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Resistor extends AComponent implements Component {


    private AScalar      resistance;
    private Power        powerDissipation;
    private Tolerance tolerance;
    private ResistorType resistorType;


    public Resistor( final ListIterator<Token> _tokenIterator, final Circuit _circuit ) {
        super(_circuit );

        // get our default values, which also creates factories...
        Set<Value> defaults = Sets.newHashSet(
                new AssumedUnit( Double.POSITIVE_INFINITY, Units.RESISTANCE ),
                new Power( Double.POSITIVE_INFINITY ),
                new Tolerance( 0, 0 ),
                new ResistorType( ResistorTechnology.UNSPECIFIED ) );

        // get any specified values, along with defaults for unspecified values...
        Map<Class<? extends Value>, Value> values = ValuesFactory.getValues( defaults, _tokenIterator, _circuit );

        // store the values we got...
        resistance       = (AssumedUnit)  values.get( AssumedUnit.class  );
        powerDissipation = (Power)        values.get( Power.class        );
        tolerance        = (Tolerance)    values.get( Tolerance.class    );
        resistorType     = (ResistorType) values.get( ResistorType.class );

        // create our terminals...
        terminals = Terminals.getTwoTerminalInstance( this );
    }


    @Override
    public Units getExpectedUnit() {
        return Units.RESISTANCE;
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

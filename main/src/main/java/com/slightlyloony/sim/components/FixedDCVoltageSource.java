package com.slightlyloony.sim.components;

import com.slightlyloony.sim.Circuit;
import com.slightlyloony.sim.Token;
import com.slightlyloony.sim.values.Tolerance;
import com.slightlyloony.sim.values.Units;

import java.util.ListIterator;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class FixedDCVoltageSource extends AComponent implements Component {


    private double volts;
    private Tolerance tolerance;


    protected FixedDCVoltageSource( final ListIterator<Token> _tokenIterator, final Circuit _circuit ) {
        super( _circuit );
    }


    @Override
    public Units getExpectedUnit() {
        return Units.VOLTAGE;
    }
}

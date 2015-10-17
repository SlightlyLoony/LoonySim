package com.cirsim.components;

import com.cirsim.Circuit;
import com.cirsim.Token;
import com.cirsim.values.Tolerance;
import com.cirsim.values.Units;

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

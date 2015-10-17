package com.cirsim.expressions;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Constant extends NodeImpl implements Node {


    private final double value;


    public Constant( double _value ) {
        value = _value;
    }


    @Override
    public double getValue() {
        return value;
    }
}

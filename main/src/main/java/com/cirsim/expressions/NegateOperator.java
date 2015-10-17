package com.cirsim.expressions;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class NegateOperator extends UnaryOperator {


    @Override
    protected double operation( final double _a ) {
        return - _a;
    }
}

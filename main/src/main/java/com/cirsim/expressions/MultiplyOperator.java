package com.cirsim.expressions;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class MultiplyOperator extends NonUnaryOperator {


    @Override
    protected double operation( final double _a, final double _b ) {
        return _a * _b;
    }
}

package com.cirsim.expressions;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class PowerOperator extends NonUnaryOperator {


    @Override
    protected double operation( final double _a, final double _b ) {
        return Math.pow( _a, _b );
    }
}

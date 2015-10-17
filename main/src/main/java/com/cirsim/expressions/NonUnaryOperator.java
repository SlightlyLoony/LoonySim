package com.cirsim.expressions;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class NonUnaryOperator extends NodeImpl implements Node {


    @Override
    public double getValue() {

        if( children.size() == 0 )
            throw new IllegalStateException( "Non-unary operator value queried with no operands" );

        if( children.size() == 1 )
            throw new IllegalStateException( "Non-unary operator value queried with one operand" );

        double result = getChildValue( 0 );
        for( int i = 1; i < children.size(); i++ ) {
            result = operation( result, getChildValue( i ) );
        }

        return result;
    }


    protected abstract double operation( double _a, double _b );
}

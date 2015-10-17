package com.cirsim.expressions;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class UnaryOperator extends NodeImpl implements Node {


    @Override
    public void addChild( Node _child ) {

        if( children.size() > 1 )
            throw new IllegalStateException( "Attempt to add second operand to unary operator" );

        super.addChild( _child );
    }


    @Override
    public double getValue() {

        if( children.size() == 0 )
            throw new IllegalStateException( "Non-unary operator value queried with no operands" );

        if( children.size() > 1 )
            throw new IllegalStateException( "Non-unary operator value queried with more than one operand" );

        return operation( getChildValue( 0 ) );
    }


    protected abstract double operation( double _a );
}

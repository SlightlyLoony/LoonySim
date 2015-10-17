package com.cirsim.matrices;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ImmutableVector implements Vector {


    private Vector vector;


    public ImmutableVector( final Vector _vector ) {

        if( _vector == null )
            throw new IllegalArgumentException( "Vector is missing" );

        vector = _vector;
    }


    public Vector add( final Vector _vector ) {
        return vector.add( _vector );
    }



    public Vector subtract( final Vector _vector ) {
        return vector.subtract( _vector );
    }



    public double get( final int _index ) {
        return vector.get( _index );
    }


    public void set( final int _index, final double _value ) {
        throw new UnsupportedOperationException( "ImmutableVector does not support 'set()'" );
    }


    public void set( final double _value ) {
        throw new UnsupportedOperationException( "ImmutableVector does not support 'set()'" );
    }


    @Override
    public int getMaxEqualsUlpDiff() {
        return 0;
    }


    public int length() {
        return vector.length();
    }


    public boolean isValidIndex( final int _index ) {
        return vector.isValidIndex( _index );
    }


    public boolean isSameLength( final int _length ) {
        return vector.isSameLength( _length );
    }


    public boolean isSameLength( final Vector _vector ) {
        return vector.isSameLength( _vector );
    }


    public Vector deepCopy() {
        return vector.deepCopy();
    }


    public Vector multiply( final double _divisor ) {
        return vector.multiply( _divisor );
    }


    public Vector subVector( final int _start, final int _end ) {
        return vector.subVector( _start, _end );
    }


    public double[] toArray() {
        return vector.toArray();
    }


    public JaVector toJaVector() {
        return vector.toJaVector();
    }


    public VectorIterator iterator() {
        return vector.iterator();
    }


    @Override
    public VectorIterator sparseIterator() {
        return null;
    }


    public VectorIterator sparseIterator( final int _ulps ) {
        return vector.sparseIterator();
    }
}

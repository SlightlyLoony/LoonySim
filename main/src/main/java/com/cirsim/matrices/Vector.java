package com.cirsim.matrices;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface Vector {


    Vector add( final Vector _vector );


    Vector subtract( final Vector _vector );


    double get( final int _index );


    void set( final int _index, final double _value );


    void set( final double _value );


    int getMaxEqualsUlpDiff();


    int length();


    boolean isValidIndex( final int _index );


    boolean isSameLength( final int _length );


    boolean isSameLength( final Vector _vector );


    Vector deepCopy();


    Vector multiply( final double _multiplier );


    Vector subVector( final int _start, final int _end );


    double[] toArray();


    JaVector toJaVector();


    VectorIterator iterator();


    VectorIterator sparseIterator();

}



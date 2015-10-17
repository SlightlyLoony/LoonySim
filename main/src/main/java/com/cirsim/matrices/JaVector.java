package com.cirsim.matrices;

import com.cirsim.util.*;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.Arrays;
import java.util.Objects;

/**
 * Implements a vector of real numbers by using a standard Java array.  Instances of this class are most suitable for relatively small vectors,
 * especially for cases where the size of the vector is unchanging.
 *
 * Instances of this class are mutable and <i>not</i> threadsafe.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class JaVector implements Vector {


    private final double[] vector;

    // the maximum number of ulps two doubles may differ by and still be considered equal
    private final int maxEqualsUlpDiff;


    public JaVector( final int _length, final int _maxEqualsUlpDiff ) {

        if( _length < 1 )
            throw new IllegalArgumentException( "Invalid vector length: " + _length );

        if( _maxEqualsUlpDiff < 0 )
            throw new IllegalArgumentException( "Invalid maxEqualsUlpDiff: " + _maxEqualsUlpDiff );

        vector = new double[_length];
        maxEqualsUlpDiff = _maxEqualsUlpDiff;
    }


    public JaVector( final double[] _vector, final int _maxEqualsUlpDiff ) {

        if( (_vector == null) || (_vector.length < 1) )
            throw new IllegalArgumentException( "Vector missing or length zero" );

        if( _maxEqualsUlpDiff < 0 )
            throw new IllegalArgumentException( "Invalid maxEqualsUlpDiff: " + _maxEqualsUlpDiff );

        vector = _vector;
        maxEqualsUlpDiff = _maxEqualsUlpDiff;
    }


    public JaVector( final Vector _vector ) {

        if( _vector == null )
            throw new IllegalArgumentException( "Vector missing" );

        vector = new double[_vector.length()];
        VectorIterator vi = _vector.sparseIterator();
        while( vi.hasNext() ) {
            vector[vi.index()] = vi.next();
        }

        maxEqualsUlpDiff = _vector.getMaxEqualsUlpDiff();
    }


    /**
     * Returns the element-by-element sum of this instance and the given vector.  Throws an <code>IllegalArgumentException</code> if the given vector
     * is missing or is a different length than this instance.
     *
     * @param _vector the vector to add to this vector.
     * @return a new vector containing the element-by-element sum of this instance and the given vector.
     */
    public Vector add( final Vector _vector ) {

        if( !isSameLength( _vector ) )
            throw new IllegalArgumentException( "Vector missing or not the same length" );

        JaVector result = new JaVector( vector.length, maxEqualsUlpDiff );
        VectorIterator vi = _vector.sparseIterator();
        while( vi.hasNext() ) {
            result.vector[vi.index()] = vector[vi.index()] + vi.next();
        }

        return result;
    }


    /**
     * Returns the element-by-element difference of this instance and the given vector, this vector minus the given vector.  Throws an
     * <code>IllegalArgumentException</code> if the given vector is missing or is a different length than this instance.
     *
     * @param _vector the vector to subtract from this vector.
     * @return a new vector containing the element-by-element difference of this instance and the given vector.
     */
    public Vector subtract( final Vector _vector ) {

        if( !isSameLength( _vector ) )
            throw new IllegalArgumentException( "Vector missing or not the same length" );

        JaVector result = new JaVector( vector.length, maxEqualsUlpDiff );
        VectorIterator vi = _vector.sparseIterator();
        while( vi.hasNext() ) {
            result.vector[vi.index()] = vector[vi.index()] - vi.next();
        }

        return result;
    }


    public double get( final int _index ) {

        if( !isValidIndex( _index ) )
            throw new IndexOutOfBoundsException( "Vector index out of bounds: " + _index );

        return vector[_index];
    }


    public void set( final int _index, final double _value ) {

        if( !isValidIndex( _index ) )
            throw new IndexOutOfBoundsException( "Vector index out of bounds: " + _index );

        vector[_index] = _value;
    }


    public void set( final double _value ) {
        Arrays.fill( vector, _value );
    }


    public int getMaxEqualsUlpDiff() {
        return maxEqualsUlpDiff;
    }


    public int length() {
        return vector.length;
    }


    public boolean isValidIndex( final int _index ) {
        return (_index >= 0) && (_index < vector.length);
    }


    public boolean isSameLength( final int _length ) {
        return false;
    }


    /**
     * Returns <code>true</code> if and only if the given vector is non-null and is the same length as this instance.
     *
     * @param _vector the vector to check.
     * @return true if the given vector is non-null and is the same length as this instance.
     */
    public boolean isSameLength( final Vector _vector ) {
        return (_vector != null) && (_vector.length() == vector.length);
    }


    public Vector deepCopy() {
        return new JaVector( this );
    }


    public Vector multiply( final double _multiplier ) {

        JaVector result = new JaVector( vector.length, maxEqualsUlpDiff );

        // skip multiplications if we're multiplying by zero, for speed...
        if( !Numbers.nearlyEqual( 0.0, _multiplier, maxEqualsUlpDiff ) ) {
            VectorIterator vi = sparseIterator();
            while( vi.hasNext() ) {
                result.vector[vi.index()] = vector[vi.index()] * _multiplier;
            }
        }

        return result;
    }


    public Vector subVector( final int _start, final int _end ) {

        if( !isValidIndex( _start ) || !isValidIndex( _end ) )
            throw new IndexOutOfBoundsException( "Index out of bounds: " + _start + " or " + _end );

        return new JaVector( Arrays.copyOfRange( vector, _start, _end ), maxEqualsUlpDiff );
    }


    /**
     * Returns a copy of the internal array containing this vector.
     *
     * @return a Java array containing all the values of this vector.
     */
    public double[] toArray() {
        return Arrays.copyOf( vector, vector.length );
    }


    public JaVector toJaVector() {
        return this;
    }


    public VectorIterator iterator() {
        return new Iterator( false );
    }


    public VectorIterator sparseIterator() {
        return new Iterator( true );
    }


    //TODO: make equals and hash work over ALL Vector implementations...
    @Override
    public boolean equals( final Object o ) {
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        JaVector jaVector = (JaVector) o;
        return Objects.equals( vector, jaVector.vector );
    }


    @Override
    public int hashCode() {
        return Objects.hash( vector );
    }


    private class Iterator implements VectorIterator {


        private final boolean sparse;
        private int index;
        private boolean hasNext;


        private Iterator( final boolean _sparse ) {
            sparse = _sparse;
            index = -1;
        }


        public boolean hasNext() {

            if( sparse ) {
                while( (index + 1 < vector.length) && Numbers.nearlyEqual( vector[index + 1], 0, maxEqualsUlpDiff ) ) {
                    index++;
                }
            }

            index++;
            hasNext = index < vector.length;

            return hasNext;
        }


        public double next() {

            if( !hasNext )
                throw new InvalidStateException( "No values remaining in iterator" );

            return vector[index];
        }


        public int index() {

            if( !hasNext )
                throw new InvalidStateException( "No values remaining in iterator" );

            return index;
        }
    }
}

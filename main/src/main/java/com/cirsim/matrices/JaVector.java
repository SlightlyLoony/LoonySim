package com.cirsim.matrices;

import com.cirsim.util.Numbers;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.Arrays;

/**
 * Implements a vector of real numbers by using a standard Java array.  Instances of this class are most suitable for relatively small vectors.
 *<p>
 * Note that some methods of this class make use of "fuzzy" equality checking for element values.  See
 * {@link com.cirsim.util.Numbers#nearlyEqual(double, double, int) Numbers.nearlyEqual()} for details on this.
 *<p>
 * Instances of this class are mutable and are <i>not</i> threadsafe.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class JaVector implements Vector {

    // holds the elements of this vector...
    private final double[] vector;

    // the maximum number of ulps two doubles may differ by and still be considered equal...
    private final int epsilon;

    // caches the hash code; cache is invalidated by any mutation...
    private boolean dirty = true;
    private int hashCache;


    /**
     * Creates a new instance of this class with the given length, all elements containing zero, and the default epsilon.
     *
     * @param _length the length of the vector
     */
    public JaVector( final int _length ) {
        this( _length, MatrixStuff.DEFAULT_EPSILON );
    }


    /**
     * Creates a new instance of this class with the given length, all elements containing zero, and the given epsilon.
     *
     * @param _length the length of the vector
     * @param _epsilon the epsilon to use in equality checking
     */
    public JaVector( final int _length, final int _epsilon ) {

        if( _length < 1 )
            throw new IllegalArgumentException( "Invalid vector length: " + _length );

        if( _epsilon < 0 )
            throw new IllegalArgumentException( "Invalid epsilon: " + _epsilon );

        vector = new double[_length];
        epsilon = _epsilon;
    }


    /**
     * Creates a new instance of this class from the given array, with the default epsilon.  The resulting vector will have the same length as the
     * given array, and its elements will in the same order and with the same value as those in the given array.
     *
     * @param _vector the array to create a vector from
     */

    public JaVector( final double[] _vector ) {
        this( _vector, MatrixStuff.DEFAULT_EPSILON );
    }


    /**
     * Creates a new instance of this class from the given array, with the given epsilon.  The resulting vector will have the same length as the
     * given array, and its elements will be in the same order and with the same value as those in the given array.
     *
     * @param _vector the array to create a vector from
     * @param _epsilon the epsilon to use in equality checking
     */
    public JaVector( final double[] _vector, final int _epsilon ) {

        if( (_vector == null) || (_vector.length < 1) )
            throw new IllegalArgumentException( "Vector missing or length zero" );

        if( _epsilon < 0 )
            throw new IllegalArgumentException( "Invalid epsilon: " + _epsilon );

        vector = _vector;
        epsilon = _epsilon;
    }


    /**
     * Creates a new instance of this class that is equivalent to the given vector.  The new vector will have the same length as the given vector,
     * its elements will be in the same order and with the same value as those in the given vector, and its epsilon will be the same.  This
     * constructor is essentially a copy constructor, except that the give vector may be of any class that implements <code>Vector</code>.
     *
     * @param _vector the Vector to make a copy of
     */
    public JaVector( final Vector _vector ) {

        if( _vector == null )
            throw new IllegalArgumentException( "Vector missing" );

        vector = new double[_vector.length()];
        VectorIterator vi = _vector.iterator( IteratorMode.UNORDERED_AND_SPARSE );
        while( vi.hasNext() ) {
            vector[vi.index()] = vi.next();
        }

        epsilon = _vector.getEpsilon();
    }


    /**
     * Adds the given vector to this vector, element by element, returning the sum in a new vector.  The vector implementation class of the result
     * is the same as that of this instance.  In other words, <code>X[n] = T[n] + S[n]</code>, where <code>X</code> is the returned vector,
     * <code>T</code> is this vector, <code>S</code> is the given vector, and <code>n</code> is the set of all index values
     * <code>0 .. T.length - 1</code>.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a different length
     * than this instance.
     *
     * @param _vector the vector to add to this vector.
     * @return a new vector containing the element-by-element sum of this instance and the given vector.
     */
    public Vector add( final Vector _vector ) {

        if( !isSameLength( _vector ) )
            throw new IllegalArgumentException( "Vector missing or not the same length" );

        JaVector result = new JaVector( vector.length, epsilon );
        VectorIterator vi = _vector.iterator( IteratorMode.UNORDERED_AND_SPARSE );
        while( vi.hasNext() ) {
            result.vector[vi.index()] = Numbers.subtractWithZeroDetection( vector[vi.index()], -vi.next(), epsilon );
        }

        return result;
    }


    /**
     * Subtracts the given vector from this vector, element by element, returning the difference in a new vector.  The vector implementation class of
     * the result is the same as that of this instance.  In other words, <code>X[n] = T[n] - S[n]</code>, where <code>X</code> is the returned vector,
     * <code>T</code> is this vector, <code>S</code> is the given vector, and <code>n</code> is the set of all index values
     * <code>0 .. T.length - 1</code>.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a
     * different length than this instance.
     *
     * @param _vector the vector to subtract from this vector.
     * @return a new vector containing the element-by-element difference of this instance and the given vector.
     */
    public Vector subtract( final Vector _vector ) {

        if( !isSameLength( _vector ) )
            throw new IllegalArgumentException( "Vector missing or not the same length" );

        JaVector result = new JaVector( vector.length, epsilon );
        VectorIterator vi = _vector.iterator( IteratorMode.UNORDERED_AND_SPARSE );
        while( vi.hasNext() ) {
            result.vector[vi.index()] = Numbers.subtractWithZeroDetection( vector[vi.index()], vi.next(), epsilon );
        }

        return result;
    }


    /**
     * Adds the given multiple of the given vector to this vector, element by element, returning the sum in a new vector.  The vector implementation
     * class of the result is the same as that of this instance.  In other words, <code>X[n] = T[n] + S[n] * m</code>, where <code>X</code> is the
     * returned vector, <code>T</code> is this vector, <code>S</code> is the given vector, <code>m</code> is the given multiplier, and <code>n</code>
     * is the set of all index values <code>0 .. T.length - 1</code>.  Throws an <code>IllegalArgumentException</code> if the given vector is missing
     * or is a different length than this instance.
     *
     * @param _vector the vector to add a multiple of to this vector.
     * @return a new vector containing the element-by-element sum of this instance and the given multiple of the given vector.
     */
    @Override
    public Vector addMultiple( final Vector _vector, final double _multiplier ) {

        if( !isSameLength( _vector ) )
            throw new IllegalArgumentException( "Vector missing or not the same length" );

        JaVector result = new JaVector( vector.length, epsilon );
        VectorIterator vi = _vector.iterator( IteratorMode.UNORDERED_AND_SPARSE );
        while( vi.hasNext() ) {
            result.vector[vi.index()] = Numbers.subtractWithZeroDetection( vector[vi.index()], -vi.next() * _multiplier, epsilon );
        }

        return result;
    }


    /**
     * Returns the value at the given index (zero based) in this vector.  Throws an <code>IndexOutOfBoundsException</code> if the given index is less
     * than zero, or equal to or greater than the vector's length.
     *
     * @param _index the index of the value to get
     * @return the value of the element at the given index
     */
    public double get( final int _index ) {

        if( !isValidIndex( _index ) )
            throw new IndexOutOfBoundsException( "Vector index out of bounds: " + _index );

        return vector[_index];
    }


    /**
     * Sets the value at the given index (zero based) in this vector to the given value.  Throws an <code>IndexOutOfBoundsException</code> if the
     * given index is less than zero, or equal to or greater than the vector's length.
     *
     * @param _index the index of the value to set
     * @param _value the value to set at the given index
     */
    public void set( final int _index, final double _value ) {

        if( !isValidIndex( _index ) )
            throw new IndexOutOfBoundsException( "Vector index out of bounds: " + _index );

        vector[_index] = _value;
        dirty = true;
    }


    /**
     * Sets the value of all elements of this vector to the given value.
     *
     * @param _value the value to set all elements to
     */
    public void set( final double _value ) {
        Arrays.fill( vector, _value );
        dirty = true;
    }


    /**
     * Returns the value of epsilon used by this instance.  Epsilon is the amount that two values may differ when compared, and still be considered
     * equal.  It is a technique used to get around the inexact representation of numbers with double precision floating point; otherwise, many
     * comparisons <i>expected</i> to be equal would instead appear to be unequal.  The value of epsilon is expressed in ulps (Units in the Last
     * Place), which are the magnitude of the LSB of a floating point number.
     *
     * @return the value of epsilon for this vector
     */
    public int getEpsilon() {
        return epsilon;
    }


    /**
     * Returns the length of this vector, which is the same as the number of elements in the vector (including both empty or zero elements and set
     * or nonzero elements).
     *
     * @return the length of this vector
     */
    public int length() {
        return vector.length;
    }


    /**
     * Returns the number of nonzero (or not empty) elements in this vector.  This operation requires traversing all the elements in the vector to
     * count the ones that are empty.
     *
     * @return the number of nonzero elements in this vector
     */
    @Override
    public int nonZeroElementCount() {
        int elementCount = 0;
        for( double n : vector )
            if( n != 0.0 )  // safe as empty elements are guaranteed to have perfect zeros...
                elementCount++;

        return elementCount;
    }


    /**
     * Returns true if and only if the given index is valid for this vector, which means that it is not less than zero and not greater than or equal
     * to the length of this vector.
     *
     * @param _index the index to validate
     * @return true if the given index is valid
     */
    public boolean isValidIndex( final int _index ) {
        return (_index >= 0) && (_index < vector.length);
    }


    /**
     * Returns true if and only if the given length is equal to the length of this vector.
     *
     * @param _length the length to check
     * @return true if the given length is the same as the length of this vector
     */
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


    /**
     * Returns a deep copy of this vector, using the same implementation class as this vector's.  The copy will contain no instances of shared
     * objects.
     *
     * @return a new vector that is a deep copy of this vector
     */
    public Vector deepCopy() {
        return new JaVector( this );
    }


    /**
     * Returns a new vector whose element values are this vector's element values multiplied by the given multiplier, element-by-element.  The vector
     * implementation class of the result is the same as that of this instance.  In other words, <code>X[n] = T[n] * m</code>, where <code>X</code> is
     * the returned vector, <code>T</code> is this vector, <code>m</code> is the given multiplier, and <code>n</code> is the set of all index values
     * <code>0 .. T.length - 1</code>.
     *
     * @param _multiplier the multiplier
     * @return a new vector that is the multiple of this vector, using the given multiplier
     */
    public Vector multiply( final double _multiplier ) {

        JaVector result = new JaVector( vector.length, epsilon );

        VectorIterator vi = iterator( IteratorMode.UNORDERED_AND_SPARSE );
        while( vi.hasNext() ) {
            result.vector[vi.index()] = vector[vi.index()] * _multiplier;
        }

        return result;
    }


    /**
     * Returns a vector that is a contiguous subvector of this vector.  The given start index must be a valid index for this vector, and the value
     * at the start index will be the first value in the returned vector.  The given end index must be in the range of <code>t .. l</code>, where
     * <code>t</code> is the start index + 1, and <code>l</code> is the length of this vector.  The length of the returned vector is equal to start -
     * end.  Throws an <code>IndexOutOfBoundsException</code> if either the given start or end indices are out of bounds.
     *
     * @param _start the start index within this vector for the returned vector
     * @param _end the end index within this vector for the returned vector
     * @return the subvector
     */
    public Vector subVector( final int _start, final int _end ) {

        if( !isValidIndex( _start ) )
            throw new IndexOutOfBoundsException( "Start index out of bounds: " + _start );

        if( (_end <= _start) || (_end > vector.length) )
            throw new IndexOutOfBoundsException( "End index out of bounds: " + _end );

        return new JaVector( Arrays.copyOfRange( vector, _start, _end ), epsilon );
    }


    /**
     * Returns an ordinary array containing the values of all the elements of this instance (both zero or empty values and nonzero or set values).
     * The size of the array is equal to the length of this vector.
     *
     * @return the array containing all the values of this vector
     */
    public double[] toArray() {
        return Arrays.copyOf( vector, vector.length );
    }


    /**
     * Returns a <code>JaVector</code> instance that is exactly equivalent to this vector.  If this vector <i>is</i> an instance of
     * <code>JaVector</code>, then this vector is simply returned.  Otherwise a new instance of <code>JaVector</code> is created that is a copy of
     * this vector.  The resulting vector will compare with this vector as equal using the <code>equals()</code> method on either instance, and the
     * result of <code>hashCode()</code> for each will be the same.
     *
     * @return a JaVector equivalent to this vector
     */
    public JaVector toJaVector() {
        return this;
    }


    /**
     * Returns a vector iterator over this vector's elements in the given mode.  There are two aspects to the mode, each of which has two
     * possibilities - so there are four possible combinations of modes.
     * <ul>
     *     <li><b>Ordered or unordered:</b> an ordered iterator returns values in index order (though if the iterator is also sparse, some indices
     *     may be skipped).  An unordered iterator returns values in an unspecified order, not necessarily predictable, and possibly the same as
     *     when ordered.</li>
     *     <li><b>Dense or sparse:</b> A dense iterator iterates over <i>all</i> elements in the vector, whether set (nonzero) or empty (zero).  a
     *     sparse iterator iterates over <i>only</i> the set (nonzero) elements.</li>
     * </ul>
     *
     * @param _iteratorMode the mode for this iterator: ordered or unordered, dense or sparse
     * @return the iterator over this vector's elements in the given mode
     */
    public VectorIterator iterator( final IteratorMode _iteratorMode) {
        return new Iterator( _iteratorMode );
    }


    /**
     * Returns true if this instance is equal to the given instance.  Note that the result of this method is independent of any particular
     * implementation of Vector.  Equivalent vectors with different implementations will compare as equal even if the implementation class is
     * different.
     *
     * @param _obj the object to compare for equality with this vector
     * @return true if the given object is equal to this vector
     */
    @Override
    public boolean equals( final Object _obj ) {

        if( this == _obj )
            return true;

        if( !(_obj instanceof Vector) )
            return false;

        Vector vect = (Vector) _obj;

        if( epsilon != vect.getEpsilon() )
            return false;

        if( vect.length() != vector.length )
            return false;

        if( hashCode() != vect.hashCode() )
            return false;

        int elementCount = 0;
        VectorIterator vi = vect.iterator( IteratorMode.UNORDERED_AND_SPARSE );
        while( vi.hasNext() ) {
            elementCount++;
            if( vi.next() != vector[vi.index()] )  // comparison is ok, as to be equal the values must be identical...
                return false;
        }

        return elementCount == nonZeroElementCount();
    }


    /**
     * Returns the hash code for this instance.  Note that the hash code is identical for all implementations of Vector, and is based on all non-zero
     * elements, their indices, and the epsilon.  Other implementation-dependent properties are <i>not</i> included in the hash code.
     *
     * @return the hash code for this vector.
     */
    @Override
    public int hashCode() {

        // if we have a cached value, use it...
        if( !dirty )
            return hashCache;

        hashCache = MatrixStuff.vectorHash( this );
        dirty = false;
        return hashCache;
    }


    private class Iterator implements VectorIterator {


        private final IteratorMode iteratorMode;
        private int index;
        private boolean hasNext;
        private int elementCount = -1;


        private Iterator( final IteratorMode _iteratorMode ) {
            iteratorMode = _iteratorMode;
            index = -1;
        }


        public boolean hasNext() {

            if( isSparse() ) {

                // zero comparison safe as empty elements are guaranteed to have perfect zeroes...
                while( (index + 1 < vector.length) && (vector[index + 1] == 0) ) {
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


        @Override
        public boolean isOrdered() {
            return true;
        }


        @Override
        public boolean isSparse() {
            return (iteratorMode == IteratorMode.ORDERED_AND_SPARSE) || (iteratorMode == IteratorMode.UNORDERED_AND_SPARSE);
        }


        @Override
        public int elements() {
            return isSparse() ? nonZeroElementCount() : vector.length;
        }
    }
}

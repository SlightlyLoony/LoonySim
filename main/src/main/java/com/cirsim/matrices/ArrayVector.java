package com.cirsim.matrices;

import sun.plugin.dom.exception.InvalidStateException;

import java.util.Arrays;

/**
 * Implements a vector of real numbers by using a standard Java array.  Instances of this class are most suitable for relatively small vectors.
 *<p>
 * Note that some methods of this class make use of "fuzzy" equality checking for entry values.  See
 * {@link com.cirsim.util.Numbers#nearlyEqual(double, double, int) Numbers.nearlyEqual()} for details on this.
 *<p>
 * Instances of this class are mutable and are <i>not</i> threadsafe.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ArrayVector extends AVector implements Vector {


    // holds the entries of this vector...
    private final double[] vector;


    /**
     * Creates a new instance of this class with the given length, all entries containing zero, and the default epsilon.
     *
     * @param _length the length of the vector
     */
    public ArrayVector( final int _length ) {
        this( _length, MatrixStuff.DEFAULT_EPSILON );
    }


    /**
     * Creates a new instance of this class with the given length, all entries containing zero, and the given epsilon.
     *
     * @param _length the length of the vector
     * @param _epsilon the epsilon to use in equality checking
     */
    public ArrayVector( final int _length, final int _epsilon ) {
        super( _epsilon );

        if( _length < 1 )
            throw new IllegalArgumentException( "Invalid vector length: " + _length );

        if( _epsilon < 0 )
            throw new IllegalArgumentException( "Invalid epsilon: " + _epsilon );

        vector = new double[_length];
    }


    /**
     * Creates a new instance of this class from the given array, with the default epsilon.  The resulting vector will have the same length as the
     * given array, and its entries will in the same order and with the same value as those in the given array.
     *
     * @param _vector the array to create a vector from
     */

    public ArrayVector( final double[] _vector ) {
        this( _vector, MatrixStuff.DEFAULT_EPSILON );
    }


    /**
     * Creates a new instance of this class from the given array, with the given epsilon.  The resulting vector will have the same length as the
     * given array, and its entries will be in the same order and with the same value as those in the given array.
     *
     * @param _vector the array to create a vector from
     * @param _epsilon the epsilon to use in equality checking
     */
    public ArrayVector( final double[] _vector, final int _epsilon ) {
        super( _epsilon );

        if( (_vector == null) || (_vector.length < 1) )
            throw new IllegalArgumentException( "Vector missing or length zero" );

        if( _epsilon < 0 )
            throw new IllegalArgumentException( "Invalid epsilon: " + _epsilon );

        vector = _vector;
    }


    /**
     * Creates a new instance of this class that is equivalent to the given vector.  The new vector will have the same length as the given vector,
     * its entries will be in the same order and with the same value as those in the given vector, and its epsilon will be the same.  This
     * constructor is essentially a copy constructor, except that the give vector may be of any class that implements <code>Vector</code>.
     *
     * @param _vector the Vector to make a copy of
     */
    public ArrayVector( final Vector _vector ) {
        super( (_vector == null) ? 0 : _vector.getEpsilon() );

        if( _vector == null )
            throw new IllegalArgumentException( "Vector missing" );

        vector = new double[_vector.length()];
        VectorIterator vi = _vector.iterator( VectorIteratorOrderMode.UNSPECIFIED, VectorIteratorFilterMode.SPARSE );
        while( vi.hasNext() ) {
            vi.next();
            vector[vi.index()] = vi.value();
        }
    }


    /**
     * Adds the given vector to this vector, entry by entry, returning the sum in a new vector.  The vector implementation class of the result
     * is the same as that of this instance.  In other words, <code>X[n] = T[n] + S[n]</code>, where <code>X</code> is the returned vector,
     * <code>T</code> is this vector, <code>S</code> is the given vector, and <code>n</code> is the set of all index values
     * <code>0 .. T.length - 1</code>.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a different length
     * than this instance.
     *
     * @param _vector the vector to add to this vector.
     * @return a new vector containing the entry-by-entry sum of this instance and the given vector.
     */
    public Vector add( final Vector _vector ) {
        return operation( _vector, new ArrayVector( vector.length, epsilon ), ADD );
    }


    /**
     * Subtracts the given vector from this vector, entry by entry, returning the difference in a new vector.  The vector implementation class of
     * the result is the same as that of this instance.  In other words, <code>X[n] = T[n] - S[n]</code>, where <code>X</code> is the returned vector,
     * <code>T</code> is this vector, <code>S</code> is the given vector, and <code>n</code> is the set of all index values
     * <code>0 .. T.length - 1</code>.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a
     * different length than this instance.
     *
     * @param _vector the vector to subtract from this vector.
     * @return a new vector containing the entry-by-entry difference of this instance and the given vector.
     */
    public Vector subtract( final Vector _vector ) {
        return operation( _vector, new ArrayVector( vector.length, epsilon ), SUB );
    }


    /**
     * Adds the given multiple of the given vector to this vector, entry by entry, returning the sum in a new vector.  The vector implementation
     * class of the result is the same as that of this instance.  In other words, <code>X[n] = T[n] + S[n] * m</code>, where <code>X</code> is the
     * returned vector, <code>T</code> is this vector, <code>S</code> is the given vector, <code>m</code> is the given multiplier, and <code>n</code>
     * is the set of all index values <code>0 .. T.length - 1</code>.  Throws an <code>IllegalArgumentException</code> if the given vector is missing
     * or is a different length than this instance.
     *
     * @param _vector the vector to add a multiple of to this vector.
     * @param _multiplier the multiplier
     * @return a new vector containing the entry-by-entry sum of this instance and the given multiple of the given vector.
     */
    @Override
    public Vector addMultiple( final Vector _vector, final double _multiplier ) {
        return operation( _vector, new ArrayVector( vector.length, epsilon ), new AddMulOp( _multiplier ) );
    }


    /**
     * Returns the value at the given index (zero based) in this vector.  Throws an <code>IndexOutOfBoundsException</code> if the given index is less
     * than zero, or equal to or greater than the vector's length.
     *
     * @param _index the index of the value to get
     * @return the value of the entry at the given index
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
     * Sets the value of all entries of this vector to the given value.
     *
     * @param _value the value to set all entries to
     */
    public void set( final double _value ) {
        Arrays.fill( vector, _value );
        dirty = true;
    }


    /**
     * Returns the length of this vector, which is the same as the number of entries in the vector (including both empty or zero entries and set
     * or nonzero entries).
     *
     * @return the length of this vector
     */
    public int length() {
        return vector.length;
    }


    /**
     * Clears all entries in the vector to pure zeros, and releases all memory previously allocated to hold values.
     */
    @Override
    public void clear() {
        set(0);
    }


    /**
     * Returns the number of nonzero (or not empty) entries in this vector.  This operation requires traversing all the entries in the vector to
     * count the ones that are empty.
     *
     * @return the number of nonzero entries in this vector
     */
    @Override
    public int nonZeroEntryCount() {
        int elementCount = 0;
        for( double n : vector )
            if( n != 0.0 )  // safe as empty entries are guaranteed to have perfect zeros...
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
        return _length == vector.length;
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
        return new ArrayVector( this );
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
        return subVectorInternal( _start, _end, new ArrayVector( vector.length, epsilon ) );
    }


    /**
     * Returns an ordinary array containing the values of all the entries of this instance (both zero or empty values and nonzero or set values).
     * The size of the array is equal to the length of this vector.
     *
     * @return the array containing all the values of this vector
     */
    public double[] toArray() {
        return Arrays.copyOf( vector, vector.length );
    }


    /**
     * Returns a <code>ArrayVector</code> instance that is exactly equivalent to this vector.  If this vector <i>is</i> an instance of
     * <code>ArrayVector</code>, then this vector is simply returned.  Otherwise a new instance of <code>ArrayVector</code> is created that is a copy of
     * this vector.  The resulting vector will compare with this vector as equal using the <code>equals()</code> method on either instance, and the
     * result of <code>hashCode()</code> for each will be the same.
     *
     * @return a ArrayVector equivalent to this vector
     */
    public ArrayVector toArrayVector() {
        return this;
    }


    /**
     * Returns a <code>MapVector</code> instance that is exactly equivalent to this vector.  If this vector <i>is</i> an instance of
     * <code>MapVector</code>, then this vector is simply returned.  Otherwise a new instance of <code>MapVector</code> is created that is a copy of
     * this vector.  The resulting vector will compare with this vector as equal using the <code>equals()</code> method on either instance, and the
     * result of <code>hashCode()</code> for each will be the same.
     *
     * @return a MapVector equivalent to this vector
     */
    @Override
    public MapVector toMapVector() {
        return new MapVector( this );
    }


    /**
     * Returns a <code>TreeVector</code> instance that is exactly equivalent to this vector.  If this vector <i>is</i> an instance of
     * <code>TreeVector</code>, then this vector is simply returned.  Otherwise a new instance of <code>TreeVector</code> is created that is a copy of
     * this vector.  The resulting vector will compare with this vector as equal using the <code>equals()</code> method on either instance, and the
     * result of <code>hashCode()</code> for each will be the same.
     *
     * @return a TreeVector equivalent to this vector
     */
    @Override
    public TreeVector toTreeVector() {
        return new TreeVector( this );
    }


    /**
     * Returns a vector iterator over this vector's entries in the given order and filter modes.
     * <p>
     * The order mode determines the order that the returned iterator will iterate over the vector's entries.  This may be either <i>index</i> order
     * (which means in numerical index order, <i>0 .. n</i>), or <i>unspecified</i> order (which means any order at all, including <i>index</i>.
     * Some <code>Vector</code> implementations iterate faster in <i>unspecified</i> order mode.
     * <p>
     * The filter mode determines <i>which</i> of this vector's entries the returned iterator will iterate over.  This may be either
     * <i>unfiltered</i> (which means <i>all</i> entries) or <i>sparse</i> (which means only set, or nonzero, entries).  For sparsely populated
     * vectors, the <i>sparse</i> filter mode can be significantly faster.
     *
     * @param _orderMode the order mode for the returned iterator (either index order or unspecified order)
     * @param _filterMode the filter mode for the returned iterator (either unfiltered, or set entries)
     * @return the iterator over this vector's entries in the given order and filter mode
     */
    public VectorIterator iterator( final VectorIteratorOrderMode _orderMode, final VectorIteratorFilterMode _filterMode ) {
        return new JAVectorIterator( _orderMode, _filterMode );
    }


    /**
     * Implements {@link VectorIterator} for {@link ArrayVector} instances.
     */
    private class JAVectorIterator extends AVectorIterator implements VectorIterator {


        /**
         * Creates a new instance of this vector iterator, with the given order and filter modes.
         *
         * @param _orderMode the order mode for this vector iterator
         * @param _filterMode the filter mode for this vector iterator
         */
        private JAVectorIterator( final VectorIteratorOrderMode _orderMode, final VectorIteratorFilterMode _filterMode ) {
            super( _orderMode, _filterMode );
        }


        /**
         * Returns true if and only if this iterator has another entry to return.
         *
         * @return true if this iterator has another entry
         */
        public boolean hasNext() {
            return indexInternal < vector.length;
        }


        /**
         * Advances to the next entry.  After invoking this method, the {@link #value()} and {@link #index()} methods will return the values of that
         * entry.
         */
        public void next() {

            if( indexInternal >= vector.length )
                throw new InvalidStateException( "No values remaining in iterator" );

            // retrieve the value and make it available for the value() getter...
            value = vector[indexInternal];
            index = indexInternal;

            // no matter what, we'll always bump the index up at least by one...
            indexInternal++;

            // then, IF this is a sparse iterator, and IF we're not already at the end, we'll bump past all the perfect zeroes...
            // zero comparison safe as empty entries are guaranteed to have perfect zeroes...
            while( (indexInternal < vector.length) && (filterMode == VectorIteratorFilterMode.SPARSE) && (vector[indexInternal] == MatrixStuff.PURE_ZERO) ) {
                indexInternal++;
            }
        }


        /**
         * Returns the count of entries that will be returned by this iterator.  For unfiltered iterators, this is always equal to the length of the
         * vector, and for sparse filtered iterators, it is equal to the number of set (nonzero) entries.  Note that this value is not the
         * <i>remaining</i> entries, but rather the total that will be returned; this value does not change during iteration.
         *
         * @return the count of entries that will be returned by this iterator.
         */
        @Override
        public int entryCount() {
            return (filterMode == VectorIteratorFilterMode.SPARSE) ? nonZeroEntryCount() : vector.length;
        }
    }
}

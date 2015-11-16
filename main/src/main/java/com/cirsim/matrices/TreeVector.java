package com.cirsim.matrices;

import static com.cirsim.matrices.VectorIteratorFilterMode.SPARSE;
import static com.cirsim.matrices.VectorIteratorOrderMode.INDEX;

/**
 * Implements {@link Vector} based on a compressed vector representation that is especially suited for the sorts of sparse vectors found in circuit
 * simulations, where vectors may be several thousands of entries long, but are under 50% populated (and often as little as 1%).
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class TreeVector extends AVector implements Vector, MemoryInstrumentation {

    private final ExpandingValueStore store;
    private final TreeIndex index;
    private final int maxLength;


    /**
     * Creates a new instance of {@link TreeVector} with the given minimum length (number of entries and maximum length, with the default epsilon
     * (see {@link com.cirsim.util.Numbers#nearlyEqual(double, double, int)}.
     *
     * @param _minLength the minimum number of entries to store (used to compute initial storage size)
     * @param _maxLength the maximum number of entries this vector might have
     */
    public TreeVector( final int _minLength, final int _maxLength ) {
        this( _minLength, _maxLength, MatrixStuff.DEFAULT_EPSILON );
    }


    /**
     * Creates a new instance of {@link TreeVector} with the given minimum length (number of entries, maximum length, and epsilon (see {@link
     * com.cirsim.util.Numbers#nearlyEqual(double, double, int)}.
     *
     * @param _minLength the minimum number of entries to store (used to compute initial storage size)
     * @param _maxLength the maximum number of entries this vector might have
     * @param _epsilon the epsilon to use in equality checking
     */
    public TreeVector( final int _minLength, final int _maxLength, final int _epsilon ) {
        super( _epsilon );
        store = new ExpandingValueStore( _minLength, _maxLength );
        index = new TreeIndex( _minLength, _maxLength );
        maxLength = _maxLength;
    }


    /**
     * Creates a new instance of this class that is equivalent to the given vector.  The new vector will have the same length as the given vector,
     * its entries will be in the same order and with the same value as those in the given vector, and its epsilon will be the same.  This
     * constructor is essentially a copy constructor, except that the give vector may be of any class that implements <code>Vector</code>.
     *
     * @param _vector the Vector to make a copy of
     */
    public TreeVector( final Vector _vector ) {
        super( (_vector == null) ? 0 : _vector.getEpsilon() );

        if( _vector == null )
            throw new IllegalArgumentException( "Vector missing" );

        store = new ExpandingValueStore( _vector.length(), _vector.length() );
        index = new TreeIndex( _vector.length(), _vector.length() );
        maxLength = _vector.length();
        VectorIterator vi = _vector.iterator( VectorIteratorOrderMode.UNSPECIFIED, VectorIteratorFilterMode.SPARSE );
        while( vi.hasNext() ) {
            vi.next();
            if( vi.value() != MatrixStuff.PURE_ZERO )
                set( vi.index(), vi.value() );
        }
    }


    /**
     * Adds the given vector to this vector, entry by entry, returning the sum in a new vector.  The vector implementation class of the result is the
     * same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a different length than
     * this instance.
     *
     * @param _vector the vector to add to this vector.
     * @return a new vector containing the entry-by-entry sum of this instance and the given vector.
     */
    @Override
    public Vector add( final Vector _vector ) {
        return operation( _vector, new TreeVector( maxLength, maxLength, epsilon ), ADD );
    }


    /**
     * Subtracts the given vector from this vector, entry by entry, returning the difference in a new vector.  The vector implementation class of the
     * result is the same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a different
     * length than this instance.
     *
     * @param _vector the vector to subtract from this vector.
     * @return a new vector containing the entry-by-entry difference of this instance and the given vector.
     */
    @Override
    public Vector subtract( final Vector _vector ) {
        return operation( _vector, new TreeVector( maxLength, maxLength, epsilon ), SUB );
    }


    /**
     * Adds the given multiple of the given vector to this vector, entry by entry, returning the sum in a new vector.  The vector implementation class
     * of the result is the same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a
     * different length than this instance.
     *
     * @param _vector     the vector to add a multiple of to this vector.
     * @param _multiplier the multiplier
     * @return a new vector containing the entry-by-entry sum of this instance and the given multiple of the given vector.
     */
    @Override
    public Vector addMultiple( final Vector _vector, final double _multiplier ) {
        return operation( _vector, new TreeVector( maxLength, maxLength, epsilon ), new AddMulOp( _multiplier ) );
    }


    /**
     * Returns the value at the given index (zero based) in this vector.  Throws an <code>IndexOutOfBoundsException</code> if the given index is less
     * than zero, or equal to or greater than the vector's length.
     *
     * @param _index the index of the value to get
     * @return the value of the entry at the given index
     */
    @Override
    public double get( final int _index ) {
        return 0;
    }


    /**
     * Sets the value at the given index (zero based) in this vector to the given value.  Throws an <code>IndexOutOfBoundsException</code> if the
     * given index is less than zero, or equal to or greater than the vector's length.
     *
     * @param _index the index of the value to set
     * @param _value the value to set at the given index
     */
    @Override
    public void set( final int _index, final double _value ) {

        if( !isValidIndex( _index ) )
            throw new IndexOutOfBoundsException( "Vector index out of bounds: " + _index );

        if( _value != MatrixStuff.PURE_ZERO ) {
            int valueKey = index.get( _index );
            if( valueKey == TreeIndex.VALUE_NULL ) {
                valueKey = store.create();
                store.put( valueKey, _value );
                index.put( _index, valueKey );
            }
            else {
                store.put( valueKey, _value );
            }
            dirty = true;
        }
        else {
            int valueKey = index.get( _index );
            if( valueKey != TreeIndex.VALUE_NULL ) {
                index.remove( _index );
                store.delete( valueKey );
                dirty = true;
            }
        }
    }


    /**
     * Sets the value of all entries of this vector to the given value.
     *
     * @param _value the value to set all entries to
     */
    @Override
    public void set( final double _value ) {

        if( _value == MatrixStuff.PURE_ZERO ) {
            clear();
            dirty = true;
        }

        else {
            for( int i = 0; i < maxLength; i++ ) {
                set( i, _value );
            }
            dirty = true;
        }
    }


    /**
     * Clears all entries in the vector to pure zeros, and releases all memory previously allocated to hold values.
     */
    public void clear() {
        store.clear();
        index.clear();
    }


    /**
     * Returns the number of nonzero (or not empty) entries in this vector.  In some implementations this operation may require traversing all the
     * entries in the vector to count the ones that are empty.
     *
     * @return the number of nonzero entries in this vector
     */
    @Override
    public int nonZeroEntryCount() {
        return index.size();
    }


    /**
     * Returns true if and only if the given index is valid for this vector, which means that it is not less than zero and not greater than or equal
     * to the length of this vector.
     *
     * @param _index the index to validate
     * @return true if the given index is valid
     */
    @Override
    public boolean isValidIndex( final int _index ) {
        return (_index >= 0) && (_index < maxLength);
    }


    /**
     * Returns true if and only if the given length is equal to the length of this vector.
     *
     * @param _length the length to check
     * @return true if the given length is the same as the length of this vector
     */
    @Override
    public boolean isSameLength( final int _length ) {
        return _length == maxLength;
    }


    /**
     * Returns true if and only if the given vector is non-null and is the same length as this vector.
     *
     * @param _vector the vector to check the length of
     * @return true if the given vector is non-null and is the same length as this vector
     */
    @Override
    public boolean isSameLength( final Vector _vector ) {
        return maxLength == _vector.length();
    }


    /**
     * Returns a deep copy of this vector, using the same implementation class as this vector's.  The copy will contain no instances of shared
     * objects.
     *
     * @return a new vector that is a deep copy of this vector
     */
    @Override
    public Vector deepCopy() {
        VectorIterator vi = iterator( INDEX, SPARSE );
        Vector result = new TreeVector( maxLength, maxLength, epsilon );
        while( vi.hasNext() ) {
            vi.next();
            result.set( vi.index(), vi.value() );
        }
        return result;
    }


    /**
     * Returns a vector that is a contiguous subvector of this vector.  The given start index must be a valid index for this vector, and the value at
     * the start index will be the first value in the returned vector.  The given end index must be in the range of <code>t .. l</code>, where
     * <code>t</code> is the start index + 1, and <code>l</code> is the length of this vector.  The length of the returned vector is equal to start -
     * end.  Throws an <code>IndexOutOfBoundsException</code> if either the given start or end indices are out of bounds.
     *
     * @param _start the start index within this vector for the returned vector
     * @param _end   the end index within this vector for the returned vector
     * @return the subvector
     */
    @Override
    public Vector subVector( final int _start, final int _end ) {
        return subVectorInternal( _start, _end, new TreeVector( maxLength, maxLength, epsilon ) );
    }


    /**
     * Returns an ordinary array containing the values of all the entries of this instance (both zero or empty values and nonzero or set values). The
     * size of the array is equal to the length of this vector.
     *
     * @return the array containing all the values of this vector
     */
    @Override
    public double[] toArray() {
        double[] result = new double[maxLength];
        VectorIterator vi = iterator( INDEX, SPARSE );
        while( vi.hasNext() ) {
            vi.next();
            result[ vi.index() ] = vi.value();
        }
        return result;
    }


    /**
     * Returns a <code>ArrayVector</code> instance that is exactly equivalent to this vector.  If this vector <i>is</i> an instance of
     * <code>ArrayVector</code>, then this vector is simply returned.  Otherwise a new instance of <code>ArrayVector</code> is created that is a copy of
     * this vector.  The resulting vector will compare with this vector as equal using the <code>equals()</code> method on either instance, and the
     * result of <code>hashCode()</code> for each will be the same.
     *
     * @return a ArrayVector equivalent to this vector
     */
    @Override
    public ArrayVector toArrayVector() {
        return new ArrayVector( this );
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
        return this;
    }


    /**
     * Returns the length of this vector, which is the same as the number of entries in the vector (including both empty or zero entries and set or
     * nonzero entries).
     *
     * @return the length of this vector
     */
    @Override
    public int length() {
        return maxLength;
    }


    /**
     * Returns a vector iterator over this vector's entries in the given order and filter modes.
     * <p>
     * The order mode determines the order that the returned iterator will iterate over the vector's entries.  This may be either <i>index</i> order
     * (which means in numerical index order, <i>0 .. n</i>), or <i>unspecified</i> order (which means any order at all, including <i>index</i>. Some
     * <code>Vector</code> implementations iterate faster in <i>unspecified</i> order mode.
     * <p>
     * The filter mode determines <i>which</i> of this vector's entries the returned iterator will iterate over.  This may be either <i>unfiltered</i>
     * (which means <i>all</i> entries) or <i>sparse</i> (which means only set, or nonzero, entries).  For sparsely populated vectors, the
     * <i>sparse</i> filter mode can be significantly faster.
     *
     * @param _orderMode  the order mode for the returned iterator (either index order or unspecified order)
     * @param _filterMode the filter mode for the returned iterator (either unfiltered, or set entries)
     * @return the iterator over this vector's entries in the given order and filter mode
     */
    @Override
    public VectorIterator iterator( final VectorIteratorOrderMode _orderMode, final VectorIteratorFilterMode _filterMode ) {
        return null;
    }


    /**
     * Returns an <i>estimate</i> of the total bytes of memory that has been allocated by this instance.  The return value is equal to the sum of the
     * values returned by {@link #memoryUsed()} and {@link #memoryUnused()}.
     * <p>
     * This value must be estimated because actual memory consumed is different for different CPU architectures and Java runtime implementations, and
     * possibly even on flags used to invoke the runtime.
     *
     * @return the estimated bytes of memory allocated by this instance
     */
    @Override
    public long memoryAllocated() {
        return 0;
    }


    /**
     * Returns an <i>estimate</i> of the total bytes of memory actually in use by this instance.
     * <p>
     * This value must be estimated because actual memory consumed is different for different CPU architectures and Java runtime implementations, and
     * possibly even on flags used to invoke the runtime.
     *
     * @return the estimated bytes of memory actually in use by this instance
     */
    @Override
    public long memoryUsed() {
        return 0;
    }


    /**
     * Returns an <i>estimate</i> of the total bytes of memory allocated, but not actually in use by this instance.
     * <p>
     * This value must be estimated because actual memory consumed is different for different CPU architectures and Java runtime implementations, and
     * possibly even on flags used to invoke the runtime.
     *
     * @return the estimated bytes of memory allocated but not in use by this instance
     */
    @Override
    public long memoryUnused() {
        return 0;
    }
}

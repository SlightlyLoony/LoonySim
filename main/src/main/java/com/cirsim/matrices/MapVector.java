package com.cirsim.matrices;

import com.cirsim.util.Numbers;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Implements a vector of real numbers by using a map to contain set (nonzero) entries.  Instances of this class are fast when enumerating set
 * entries, and adding or removing set entries.  They are relatively slow at enumerating all entries, and slightly slower at retrieving set
 * entries.  <code>MapVectors</code> are perhaps most useful for initially building a large sparse vector.  The memory consumption for instances
 * of this class is higher than most <code>Vector</code> implementations.
 *<p>
 * The implementation uses a {@link java.util.TreeMap TreeMap} to enable fast iteration in index order.  This choice means that the getter and setter
 * have log(vector length) performance, which is still quite good on a sparse vector such as typically seen in circuit simulation, with sparseness
 * typically around 0.02 or so.
 *<p>
 * Note that some methods of this class make use of "fuzzy" equality checking for entry values.  See
 * {@link com.cirsim.util.Numbers#nearlyEqual(double, double, int) Numbers.nearlyEqual()} for details on this.
 *<p>
 * Instances of this class are mutable and are <i>not</i> threadsafe.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class MapVector extends AVector implements Vector {


    // holds the entries of this vector...
    private final TreeMap<Integer, Double> vector;

    // holds the length of this vector...
    private final int length;


    /**
     * Creates a new instance of this class with the given length, all entries containing zero, and the default epsilon.
     *
     * @param _length the length of the vector
     */
    public MapVector( final int _length ) {
        this( _length, MatrixStuff.DEFAULT_EPSILON );
    }


    /**
     * Creates a new instance of this class with the given length, all entries containing zero, and the given epsilon.
     *
     * @param _length the length of the vector
     * @param _epsilon the epsilon to use in equality checking
     */
    public MapVector( final int _length, final int _epsilon ) {
        super( _epsilon );

        if( _length < 1 )
            throw new IllegalArgumentException( "Invalid vector length: " + _length );

        if( _epsilon < 0 )
            throw new IllegalArgumentException( "Invalid epsilon: " + _epsilon );

        vector = new TreeMap<>();
        length = _length;
    }


    /**
     * Creates a new instance of this class that is equivalent to the given vector.  The new vector will have the same length as the given vector,
     * its entries will be in the same order and with the same value as those in the given vector, and its epsilon will be the same.  This
     * constructor is essentially a copy constructor, except that the give vector may be of any class that implements <code>Vector</code>.
     *
     * @param _vector the Vector to make a copy of
     */
    public MapVector( final Vector _vector ) {
        super( (_vector == null) ? 0 : _vector.getEpsilon() );

        if( _vector == null )
            throw new IllegalArgumentException( "Vector missing" );

        vector = new TreeMap<>();
        VectorIterator vi = _vector.iterator( VectorIteratorOrderMode.UNSPECIFIED, VectorIteratorFilterMode.SPARSE );
        while( vi.hasNext() ) {
            vi.next();
            if( vi.value() != 0.0d )
                vector.put( vi.index(), vi.value() );
        }
        length = _vector.length();
    }


    /**
     * Adds the given vector to this vector, entry by entry, returning the sum in a new vector.  The vector implementation class of the result is
     * the same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a different length
     * than this instance.
     *
     * @param _vector the vector to add to this vector.
     * @return a new vector containing the entry-by-entry sum of this instance and the given vector.
     */
    @Override
    public Vector add( final Vector _vector ) {

        if( !isSameLength( _vector ) )
            throw new IllegalArgumentException( "Vector missing or not the same length" );

        MapVector result = new MapVector( length, epsilon );
        VectorIterator vi = _vector.iterator( VectorIteratorOrderMode.UNSPECIFIED, VectorIteratorFilterMode.SPARSE );
        while( vi.hasNext() ) {
            vi.next();
            result.set(vi.index(), Numbers.addWithZeroDetection( get( vi.index() ), vi.value(), epsilon ) );
        }

        return result;
    }


    /**
     * Subtracts the given vector from this vector, entry by entry, returning the difference in a new vector.  The vector implementation class of
     * the result is the same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a
     * different length than this instance.
     *
     * @param _vector the vector to subtract from this vector.
     * @return a new vector containing the entry-by-entry difference of this instance and the given vector.
     */
    @Override
    public Vector subtract( final Vector _vector ) {

        if( !isSameLength( _vector ) )
            throw new IllegalArgumentException( "Vector missing or not the same length" );

        MapVector result = new MapVector( length, epsilon );
        VectorIterator vi = _vector.iterator( VectorIteratorOrderMode.UNSPECIFIED, VectorIteratorFilterMode.SPARSE );
        while( vi.hasNext() ) {
            vi.next();
            result.set(vi.index(), Numbers.subtractWithZeroDetection( get( vi.index() ), vi.value(), epsilon ) );
        }

        return result;
    }


    /**
     * Adds the given multiple of the given vector to this vector, entry by entry, returning the sum in a new vector.  The vector implementation
     * class of the result is the same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is
     * a different length than this instance.
     *
     * @param _vector     the vector to add a multiple of to this vector.
     * @param _multiplier the multiplier
     * @return a new vector containing the entry-by-entry sum of this instance and the given multiple of the given vector.
     */
    @Override
    public Vector addMultiple( final Vector _vector, final double _multiplier ) {

        if( !isSameLength( _vector ) )
            throw new IllegalArgumentException( "Vector missing or not the same length" );

        MapVector result = new MapVector( length, epsilon );
        VectorIterator vi = _vector.iterator( VectorIteratorOrderMode.UNSPECIFIED, VectorIteratorFilterMode.SPARSE );
        while( vi.hasNext() ) {
            vi.next();
            result.set(vi.index(), Numbers.addWithZeroDetection( get( vi.index() ), vi.value() * _multiplier, epsilon ) );
        }

        return result;
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

        if( !isValidIndex( _index ) )
            throw new IndexOutOfBoundsException( "Vector index out of bounds: " + _index );

        Double result = vector.get( _index );
        return (result == null) ? 0.0d : result;
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

        if( _value != 0.0d ) {
            vector.put( _index, _value );
            dirty = true;
        }
    }


    /**
     * Sets the value of all entries of this vector to the given value.
     *
     * @param _value the value to set all entries to
     */
    @Override
    public void set( final double _value ) {
        if( _value == 0.0d ) {
            vector.clear();
            dirty = false;
        }

        else {
            for( int i = 0; i < length; i++ ) {
                vector.put( i, _value );
            }
            dirty = true;
        }
    }


    /**
     * Returns the length of this vector, which is the same as the number of entries in the vector (including both empty or zero entries and set or
     * nonzero entries).
     *
     * @return the length of this vector
     */
    @Override
    public int length() {
        return length;
    }


    /**
     * Returns the number of nonzero (or not empty) entries in this vector.  In some implementations this operation may require traversing all the
     * entries in the vector to count the ones that are empty.
     *
     * @return the number of nonzero entries in this vector
     */
    @Override
    public int nonZeroEntryCount() {
        return vector.size();
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
        return (_index >= 0) && (_index < length);
    }


    /**
     * Returns true if and only if the given length is equal to the length of this vector.
     *
     * @param _length the length to check
     * @return true if the given length is the same as the length of this vector
     */
    @Override
    public boolean isSameLength( final int _length ) {
        return _length == length;
    }


    /**
     * Returns true if and only if the given vector is non-null and is the same length as this vector.
     *
     * @param _vector the vector to check the length of
     * @return true if the given vector is non-null and is the same length as this vector
     */
    @Override
    public boolean isSameLength( final Vector _vector ) {
        return (_vector != null) && (_vector.length() == length);
    }


    /**
     * Returns a deep copy of this vector, using the same implementation class as this vector's.  The copy will contain no instances of shared
     * objects.
     *
     * @return a new vector that is a deep copy of this vector
     */
    @Override
    public Vector deepCopy() {
        return new MapVector( this );
    }


    /**
     * Returns a new vector whose entry values are this vector's entry values multiplied by the given multiplier, entry-by-entry.  The vector
     * implementation class of the result is the same as that of this instance.  In other words, <code>X[n] = T[n] * m</code>, where <code>X</code> is
     * the returned vector, <code>T</code> is this vector, <code>m</code> is the given multiplier, and <code>n</code> is the set of all index values
     * <code>0 .. T.length - 1</code>.
     *
     * @param _multiplier the multiplier
     * @return a new vector that is the multiple of this vector, using the given multiplier
     */
    @Override
    public Vector multiply( final double _multiplier ) {

        MapVector result = new MapVector( length, epsilon );

        VectorIterator vi = iterator( VectorIteratorOrderMode.UNSPECIFIED, VectorIteratorFilterMode.SPARSE );
        while( vi.hasNext() ) {
            vi.next();
            result.set( vi.index(), vi.value() * _multiplier );
        }

        return result;
    }


    /**
     * Returns a vector that is a contiguous subvector of this vector.  The given start index must be a valid index for this vector, and the value at
     * the start index will be the first value in the returned vector.  The given end index must be in the range of <code>t .. l</code>, where
     * <code>t</code> is the start index + 1, and <code>l</code> is the length of this vector.  The length of the returned vector is equal to start -
     * end.  Throws an <code>IndexOutOfBoundsException</code> if either the given start or end indices are out of bounds.
     * <p>
     * This operation force a value lookup (in the internal map) for every possible index in the subrange, a potentially slow operation if the
     * subvector is large and sparse.
     *
     * @param _start the start index within this vector for the returned vector
     * @param _end   the end index within this vector for the returned vector
     * @return the subvector
     */
    @Override
    public Vector subVector( final int _start, final int _end ) {

        if( !isValidIndex( _start ) )
            throw new IndexOutOfBoundsException( "Start index out of bounds: " + _start );

        if( (_end <= _start) || (_end > length) )
            throw new IndexOutOfBoundsException( "End index out of bounds: " + _end );

        MapVector result = new MapVector( length, epsilon );
        for( int i = _start; i < _end; i++ ) {
            Double val = vector.get( i );
            if( val != null ) {
                result.set( i, val );
            }
        }
        return result;
    }


    /**
     * Returns an ordinary array containing the values of all the entries of this instance (both zero or empty values and nonzero or set values). The
     * size of the array is equal to the length of this vector.
     *
     * @return the array containing all the values of this vector
     */
    @Override
    public double[] toArray() {
        double[] result = new double[length];
        for( Map.Entry<Integer, Double> entry : vector.entrySet() ) {
            result[ entry.getKey() ] = entry.getValue();
        }
        return result;
    }


    /**
     * Returns a <code>JAVector</code> instance that is exactly equivalent to this vector.  If this vector <i>is</i> an instance of
     * <code>JAVector</code>, then this vector is simply returned.  Otherwise a new instance of <code>JAVector</code> is created that is a copy of
     * this vector.  The resulting vector will compare with this vector as equal using the <code>equals()</code> method on either instance, and the
     * result of <code>hashCode()</code> for each will be the same.
     *
     * @return a JAVector equivalent to this vector
     */
    @Override
    public JAVector toJAVector() {
        JAVector result = new JAVector( length, epsilon );
        for( Map.Entry<Integer, Double> entry : vector.entrySet() ) {
            result.set( entry.getKey(), entry.getValue() );
        }
        return result;
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
        return new MapVectorIterator( _orderMode, _filterMode );
    }


    /**
     * Implements {@link VectorIterator} for {@link MapVector} instances.
     */
    private class MapVectorIterator extends AVectorIterator implements VectorIterator {

        // an iterator over the map's entries, in key order...
        private Iterator<Map.Entry<Integer, Double>> mapIterator;

        /**
         * Creates a new instance of this vector iterator, with the given order and filter modes.
         *
         * @param _orderMode the order mode for this vector iterator
         * @param _filterMode the filter mode for this vector iterator
         */
        private MapVectorIterator( final VectorIteratorOrderMode _orderMode, final VectorIteratorFilterMode _filterMode ) {
            super( _orderMode, _filterMode );

            // if we're in sparse filter mode, get an iterator over the map's entries, in key order...
            if( filterMode == VectorIteratorFilterMode.SPARSE ) {
                mapIterator = vector.entrySet().iterator();
            }
        }


        /**
         * Returns true if and only if this iterator has another entry to return.
         *
         * @return true if this iterator has another entry
         */
        @Override
        public boolean hasNext() {
            return (filterMode == VectorIteratorFilterMode.SPARSE) ? mapIterator.hasNext() : index < length;
        }


        /**
         * Advances to the next entry.  After invoking this method, the {@link #value()} and {@link #index()} methods will return the values of that
         * entry.
         */
        @Override
        public void next() {
            if( filterMode == VectorIteratorFilterMode.SPARSE ) {

                if( !mapIterator.hasNext() )
                    throw new InvalidStateException( "No values remaining in iterator" );

                Map.Entry<Integer, Double> entry = mapIterator.next();
                index = entry.getKey();
                value = entry.getValue();
            }
            else {

                if( indexInternal >= length )
                    throw new InvalidStateException( "No values remaining in iterator" );

                index = indexInternal;
                Double val = vector.get( indexInternal );
                value = (val == null) ? 0.0d : val;
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
            return (filterMode == VectorIteratorFilterMode.SPARSE) ? nonZeroEntryCount() : vector.size();
        }
    }
}

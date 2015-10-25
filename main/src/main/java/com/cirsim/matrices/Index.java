package com.cirsim.matrices;

/**
 * Implemented by classes that provide indexes for integer keys in the range 0..2^12-2, inclusive.  Each key provides access to a 24 bit integer
 * value key, intended for use as a key to a {@link ValueStore}.  A single index instance may be used to provide indexes for sparse vector
 * implementations.  For sparse matrices of m x n dimension, m + n index instances will be needed to provide access to row and column values.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface Index {


    /**
     * Returns the 24 bit integer value associated with the given key, or the special value NULL (0xFFFFFF) if the given key is not contained in the
     * index, but <i>is</i> within the range of the index.  If the given key is out of range (negative or greater than the index's capacity), an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param _key the key to look up the associated value with
     * @return the value associated with the given key
     */
    int get( final int _key );


    /**
     * Puts the given 24 bit integer value into the index, and associates it with the given key.  If the given key is out of range (negative or
     * greater than the index's capacity), an {@link IllegalArgumentException} is thrown.  If the given value is outside the range of a 24 bit
     * unsigned integer, is negative, or is equal to the special value NULL (0xFFFFFF), an {@link IllegalArgumentException} is thrown.
     *
     * @param _key the key to associate the value with, and store in the index
     * @param _value the value to associate with the key
     */
    void put( final int _key, final int _value );


    /**
     * Returns an iterator over the entries in this index, with the given order and filter mode.
     * <p>
     * The order mode determines the order that the returned iterator will iterate over the index's entries.  This may be either <i>index</i> order
     * (which means in numerical index order, <i>0 .. n</i>), or <i>unspecified</i> order (which means any order at all, including <i>index</i>.
     * Some <code>Vector</code> implementations iterate faster in <i>unspecified</i> order mode.
     * <p>
     * The filter mode determines <i>which</i> of this vector's entries the returned iterator will iterate over.  This may be either
     * <i>unfiltered</i> (which means <i>all</i> entries) or <i>sparse</i> (which means only set, or nonzero, entries).  For sparsely populated
     * vectors, the <i>sparse</i> filter mode can be significantly faster.
     *
     * @param _orderMode the order mode for the returned iterator (either index order or unspecified order)
     * @param _filterMode the filter mode for the returned iterator (either unfiltered, or set entries)
     * @return the iterator over this index's entries in the given order and filter mode
     */
    IndexIterator iterator( final IndexIteratorOrderMode _orderMode, final IndexIteratorFilterMode _filterMode );


    /**
     * Returns an <i>estimate</i> of the total bytes of memory that has been allocated by this instance.  The return value is equal to the sum
     * of the values returned by {@link #memoryUsed()} and {@link #memoryUnused()}.
     * <p>
     * This value must be estimated because actual memory consumed is different for different CPU architectures and Java runtime implementations,
     * and possibly even on flags used to invoke the runtime.
     *
     * @return the estimated bytes of memory allocated by this instance
     */
    long memoryAllocated();


    /**
     * Returns an <i>estimate</i> of the total bytes of memory actually in use by this instance.
     * <p>
     * This value must be estimated because actual memory consumed is different for different CPU architectures and Java runtime implementations,
     * and possibly even on flags used to invoke the runtime.
     *
     * @return the estimated bytes of memory actually in use by this instance
     */
    long memoryUsed();


    /**
     * Returns an <i>estimate</i> of the total bytes of memory allocated, but not actually in use by this instance.
     * <p>
     * This value must be estimated because actual memory consumed is different for different CPU architectures and Java runtime implementations,
     * and possibly even on flags used to invoke the runtime.
     *
     * @return the estimated bytes of memory allocated but not in use by this instance
     */
    long memoryUnused();
}

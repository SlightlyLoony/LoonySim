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
     * Special key value that indicates a null or non-existent key.
     */
    int NULL        = 0xFFF;

    /**
     * Special value that represents a null or non-existent value.
     */
    int VALUE_NULL  = 0xFF_FFFF;


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
     * unsigned integer, is negative, or is equal to the special value VALUE_NULL (0xFFFFFF), an {@link IllegalArgumentException} is thrown.  If
     * there was a previous value associated with this key, that value is returned.  Otherwise, a VALUE_NULL is returned.
     *
     * @param _key the key to associate the value with, and store in the index
     * @param _value the value to associate with the key
     * @return the previous value associated with the given key, or VALUE_NULL if there was none.
     */
    int put( final int _key, final int _value );


    /**
     * Removes the given key and its associated value from this index.  If the given key is out of range (negative or greater than the index's
     * capacity), an {@link IllegalArgumentException} is thrown.  The value associated with the given key is returned.  If the given key isn't in
     * this index, a VALUE_NULL is returned.
     *
     * @param _key the key to remove from this index
     * @return the value previously associated with the given key, or VALUE_NULL if there was none.
     */
    int remove( final int _key );


    /**
     * Returns the number of keys (and their associated values) are contained in this index.
     *
     * @return the number of keys (and their associated values) are contained in this index
     */
    int size();


    /**
     * Clears all entries from this index and releases all memory previously allocated to hold them.
     */
    void clear();


    /**
     * Returns an iterator over the entries in this index.
     *
     * @return the iterator over this index's entries
     */
    IndexIterator iterator();
}

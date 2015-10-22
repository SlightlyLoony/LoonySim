package com.cirsim.matrices;

/**
 * Implemented by classes providing key/value stores for integer keys and double values.
 */
public interface ValueStore {


    /**
     * Creates a new slot to contain a double value, initializes that value to a pure zero, and returns the new key.
     *
     * @return the key for the new double storage slot
     */
    int create();


    /**
     * Deletes the double storage slot with the given key, and returns the value it contained before deletion.
     *
     * @param _key the key for the storage slot to delete.
     * @return the value contained in the slot deleted
     */
    double delete( final int _key );


    /**
     * Returns the current double value in the storage slot with the given key.
     *
     * @param _key the key for the storage slot to be retrieved.
     * @return the value in the slot with the given key.
     */
    double get( final int _key );


    /**
     * Update the value of the storage slot with the given key to the given double value.
     *
     * @param _key the key for the storage slot to be updated
     * @param _value the value to set the storage slot to
     */
    void put( final int _key, final double _value );
}

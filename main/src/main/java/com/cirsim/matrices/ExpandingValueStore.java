package com.cirsim.matrices;

import java.util.Arrays;

import static com.cirsim.util.Numbers.closestBinaryPower;

/**
 * Implements {@link ValueStore} to provide a simple key/value store for doubles.  Instances of this class are intended for use in vectors or matrices
 * in circuit simulations, where the number of values will be limited to 2^24.  Because hundreds or thousands of instances of this class will be
 * created for every instance of a matrix, this class implements some memory consumption optimizations so that small matrices don't consume absurdly
 * large amounts of memory.  These optimizations are automatically skipped for bigger matrices.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ExpandingValueStore implements ValueStore {

    private static final int MIN_INITIAL_BLOCK_SIZE = 4;  // set low so that sparsely populated vectors take little room...
    private static final int MIN_BLOCK_SIZE         = 32; // to keep us from having a block array filled with really tiny blocks on small stores...
    private static final int NULL                   = 0xFFFFFF;
    private static final int MAX_ENTRIES            = 0xFFFFFF;
    private static final int SLOT_MASK              = 0xFFFFFF;

    /*
     * Deleted slots are used to store the link to the next deleted slot in the linked list of deleted slots, thus saving us from having to keep an
     * external list.  We do this by storing a NaN with the mantissa set to the slot number (these are valid NaN bits).  We then ensure that no NaNs
     * are entered through a put() invocation, and we check for the on get() invocations (where, if we find one, it's an error).
     */
    private static final long NAN_EXPONENT = 0x7FF0_0000_0100_0000L;  // bit 24 is set so that a zero slot value won't look like an INF...
    private static final long LONG_SLOT_MASK = (long) SLOT_MASK;

    // we assume 64 bit pointers and 8 byte alignment, to be conservative...
    private static final long MEMORY_FIXED_OVERHEAD = 16 + 8 * 8; // for object overhead, the seven ints, and pointer to the blocks array...


    private final int blockSize;
    private final int initialBlockSize;
    private final double[][] blocks;
    private final int offsetMask;
    private final int blockOffsetShift;

    private int nextSlot;
    private int deletedSlots = NULL;
    private int unusedSlots;


    /**
     * Creates a new instance of this class that is initially configured to store the given minimum number of entries, but which can expand
     * automatically to store the given maximum number of entries.  Both of these numbers are guidelines for instances of this class, not guarantees.
     * In particular, instances <i>may</i> actually be able to store more entries than the given maximum number, and they <i>may</i> be able to store
     * more entries than the given minimum number without allocating additional memory.
     *
     * @param _minEntries the minimum number of entries to store (used to compute initial store size)
     * @param _maxEntries the maximum number of entries to store
     */
    public ExpandingValueStore( final int _minEntries, final int _maxEntries ) {

        if( _minEntries < 0 )
            throw new IllegalArgumentException( "Min entries out of bounds: " + _minEntries );

        if( (_maxEntries > MAX_ENTRIES) || (_maxEntries < _minEntries) )
            throw new IllegalArgumentException( "Max entries out of bounds: " + _maxEntries );

        // determine the block size we want to use, and the array to hold them, based on the maximum number of entries...
        // by taking 1/32 of the max size as the block size, we're saying each block is about 3% of the max...
        blockSize = closestBinaryPower( Math.max( MIN_BLOCK_SIZE, _maxEntries >>> 5 ) );
        blocks = new double[(_maxEntries + blockSize - 1)/blockSize][];

        // determine the initial first block size, based on the minimum number of entries...
        initialBlockSize = closestBinaryPower( Math.max( MIN_INITIAL_BLOCK_SIZE, _minEntries ) );

        // determine the mask and shift for decoding slots into block numbers and offsets...
        offsetMask = blockSize - 1;
        blockOffsetShift = Integer.numberOfTrailingZeros( blockSize );
    }


    /**
     * Creates a new slot to contain a double value, initializes that value to a pure zero, and returns the new key.
     *
     * @return the key for the new double storage slot
     */
    @Override
    public int create() {

        // if we have any deleted slots, return one of them, undeleted...
        if( deletedSlots != NULL ) {

            unusedSlots--;
            int newSlot = deletedSlots;
            int block = newSlot >>> blockOffsetShift;
            int offset = newSlot & offsetMask;
            deletedSlots = SLOT_MASK & (int) Double.doubleToRawLongBits( blocks[block][offset] );
            blocks[block][offset] = MatrixStuff.PURE_ZERO;
            return newSlot;
        }

        // there are no deleted slots, so we're going to allocate the next never-used one...
        int block = nextSlot >>> blockOffsetShift;
        int offset = nextSlot & offsetMask;

        if( block >= blocks.length )
            throw new IllegalStateException( "Value store is completely full" );

        // make a new block if necessary...
        if( blocks[block] == null ) {
            blocks[block] = new double[(block == 0) ? initialBlockSize : blockSize];
            return nextSlot++;
        }

        // if we're on the initial block, expand as required...
        if( (block == 0) && (offset >= blocks[0].length) ) {
            blocks[0] = Arrays.copyOf( blocks[0], blocks[0].length << 1 );
        }

        return nextSlot++;
    }


    /**
     * Deletes the double storage slot with the given key, and returns the value it contained before deletion.
     *
     * @param _key the key for the storage slot to delete.
     * @return the value contained in the slot deleted
     */
    @Override
    public double delete( final int _key ) {

        if( (_key < 0) || (_key >= nextSlot))
            throw new IllegalArgumentException( "Key out of range: " + _key );

        int block = _key >>> blockOffsetShift;
        int offset = _key & offsetMask;

        double value = blocks[block][offset];

        if( Double.isNaN( value ) )
            throw new IllegalArgumentException( "Slot has already been deleted: " + _key );

        blocks[block][offset] = Double.longBitsToDouble( NAN_EXPONENT | (LONG_SLOT_MASK & deletedSlots) );
        deletedSlots = _key;

        unusedSlots++;

        return value;
    }


    /**
     * Returns the current double value in the storage slot with the given key.
     *
     * @param _key the key for the storage slot to be retrieved.
     * @return the value in the slot with the given key.
     */
    @Override
    public double get( final int _key ) {

        if( (_key < 0) || (_key >= nextSlot))
            throw new IllegalArgumentException( "Key out of range: " + _key );

        int block = _key >>> blockOffsetShift;
        int offset = _key & offsetMask;

        double value = blocks[block][offset];

        if( Double.isNaN( value ) )
            throw new IllegalArgumentException( "Slot has been deleted: " + _key );

        return value;
    }


    /**
     * Update the value of the storage slot with the given key to the given double value.
     *
     * @param _key   the key for the storage slot to be updated
     * @param _value the value to set the storage slot to
     */
    @Override
    public void put( final int _key, final double _value ) {

        if( (_key < 0) || (_key >= nextSlot))
            throw new IllegalArgumentException( "Key out of range: " + _key );

        if( Double.isNaN( _value ))
            throw new IllegalArgumentException( "Attempted to store NaN: " + _value );

        int block = _key >>> blockOffsetShift;
        int offset = _key & offsetMask;

        double value = blocks[block][offset];

        if( Double.isNaN( value ) )
            throw new IllegalArgumentException( "Slot has been deleted: " + _key );

        blocks[block][offset] = _value;
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
        int blockArray = 8 * blocks.length + 16;  // the first dimension, eight bytes per pointer plus Java's array overhead...
        for( double[] block : blocks )
            blockArray += 8 * block.length + 16;  // the second dimension, eight bytes per double plus Java's array overhead...
        return MEMORY_FIXED_OVERHEAD + blockArray;
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
        return memoryAllocated() - memoryUnused();
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
        return unusedSlots * 8;
    }


    /*
     * T E S T   H A R N E S S
     *
     * All the following methods are here strictly for testing purposes.  There's no purpose for them in actual use, and they should be avoided
     * as they may well have signature changes or disappear.  In other words, these are NOT supported API!
     */


    /**
     * Returns the total bytes allocated in the internal block array.
     * <p>
     * This method is present strictly for testing purposes.  There's no purpose for it in actual use, and its use should be avoided, as it may well
     * have signature changes or disappear.  In other words, it is <i>not</i> supported API!
     *
     * @return the total bytes allocated in the internal block array
     */
    @Deprecated
    public int getAllocatedSize() {
        int result = 0;
        int block = 0;
        while( true ) {
            if( (block >= blocks.length) || (blocks[block] == null) )
                break;
            result += blocks[block].length;
            block++;
        }
        return result;
    }
}

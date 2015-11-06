package com.cirsim.matrices;

import java.util.Arrays;
import java.util.HashSet;

import static com.cirsim.util.Numbers.closestBinaryPower;

/**
 * Provides a specialized index intended for vectors and matrices of real numbers, used in circuit simulation applications.  The key for this index
 * is a 12 bit number, accepting values in the range 0..4094 (0x0..0xFFE).  The value 4095 (0xFFF) is reserved for a null indication.  The values
 * associated with the keys are 24 bit numbers in the range 0..16,777,214 (0x0..0xFFFFFE).  The value 16,777,215 (0xFFFFFF) is reserved for a null
 * indication.  The values are used as keys to a store of real numbers (see {@link ValueStore}).
 * <p>
 * The implementation is based on a classic red/black tree, with an eye to memory conservation and performance.  The nodes of the tree are stored
 * in an encoded long, which is in turn stored in an array.  That means that each node consumes just 8 bytes of memory.  When these indices are used
 * in a matrix, there may be as many as 8,190 of them, each with up to 4,095 entries, for a total of 33,538,050 entries occupying 268,304,400 bytes
 * (along with a bit of overhead).  The store of real numbers occupies a similar amount of memory.  That total is roughly 15% of what an
 * implementation based on {@link java.util.TreeMap} would occupy.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */

/*
 * References in code comments prefaced with "CLR:xx@ll" are references to the book "Introduction to Algorithms", by Thomas H. Cormen, Charles E.
 * Leiserson, and Ronald L. Rivest in the third edition, first printing of 2009, ISBN 978-0-262-03384-8, where "xx" is the page number (or range)
 * and "ll" is the line number (or range).
 */
public class TreeIndex implements Index, MemoryInstrumentation {

    public static final int MAX_ENTRIES = 4095;

    private static final int MIN_INITIAL_BLOCK_SIZE = 4;  // set low so that sparsely populated trees take little room...
    private static final int MIN_BLOCK_SIZE         = 32; // to keep us from having a block array filled with really tiny blocks on small stores...

    // we assume 64 bit pointers and 8 byte alignment, to be conservative...
    private static final long MEMORY_FIXED_OVERHEAD = 16 + 9 * 8; // for object overhead, the eight ints, and pointer to the blocks array...

    private final int blockOffsetShift;
    private final int blockOffsetMask;
    private final int initialBlockSize;
    private final int blockSize;
    private final long[][] blocks;

    private int deletedNodes;
    private int nextSlot;
    private int treeRoot;
    private int size;


    public TreeIndex( final int _minEntries, final int _maxEntries ) {

        deletedNodes = NULL;
        nextSlot = 0;
        treeRoot = NULL;

        if( _minEntries < 0 )
            throw new IllegalArgumentException( "Min entries out of bounds: " + _minEntries );

        if( (_maxEntries > MAX_ENTRIES) || (_maxEntries < _minEntries) )
            throw new IllegalArgumentException( "Max entries out of bounds: " + _maxEntries );

        // determine the block size we want to use, and the array to hold them, based on the maximum number of entries...
        // by taking 1/32 of the max size as the block size, we're saying each block is about 3% of the max...
        blockSize = closestBinaryPower( Math.max( MIN_BLOCK_SIZE, _maxEntries >>> 5 ) );
        blocks = new long[(_maxEntries + blockSize - 1)/blockSize][];

        // determine the initial first block size, based on the minimum number of entries...
        initialBlockSize = closestBinaryPower( Math.max( MIN_INITIAL_BLOCK_SIZE, _minEntries ) );

        // determine the mask and shift for decoding slots into block numbers and offsets...
        blockOffsetMask = blockSize - 1;
        blockOffsetShift = Integer.numberOfTrailingZeros( blockSize );
    }


    /**
     * Returns the 24 bit integer value associated with the given key, or the special value VALUE_NULL if the given key is not contained in the
     * index, but <i>is</i> within the range of the index.  If the given key is out of range (negative or greater than 4094), an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param _key the key to look up the associated value with
     * @return the value associated with the given key
     */
    @Override
    public int get( final int _key ) {

        if( (_key < 0) || (_key >= MAX_ENTRIES) )
            throw new IllegalArgumentException( "Key out of range: " + _key );

        // walk down the tree until we find our value, or fail...
        // done with a loop for speed (saves call overhead on recursion)...
        int index = treeRoot;
        long bits = 0;

        // search until we find our key or run out of tree entries...
        while( true ) {

            // if we have a null index, we've hit the end of the (tree) road...
            if( index == NULL )
                break;

            // if this node contains the key we're looking for, time to end this search...
            bits = getNodeBits( index );
            int key = Node.key( bits );
            if( key == _key )
                break;

            // otherwise, set the index to the next node we need to search...
            index = (key > _key) ? Node.leftChild( bits ) : Node.rightChild( bits );
        }

        return (index == NULL) ? VALUE_NULL : Node.value( bits );
    }


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
    @Override
    public int put( final int _key, final int _value ) {

        if( (_key < 0) || (_key >= MAX_ENTRIES) )
            throw new IllegalArgumentException( "Key out of range: " + _key );

        if( (_value < 0) || (_value >= VALUE_NULL) )
            throw new IllegalArgumentException( "Value out of range: " + _value );

        if( size >= MAX_ENTRIES )
            throw new IllegalStateException( "Attempted to add entry that would exceed the maximum size: " + MAX_ENTRIES );

        // algorithm below lifted straight from CLR: page 315, but modified to eliminate mirrored code...

        // if we already have a node with the given index, just update its value (does the same as CLR:315@1-10)...
        Ref y = search( _key );
        if( y.isNULL() && y.isTreeRoot() ) {  // if we got a reference to the tree's root, create it fill it in, and leave...
            Node node = Node.newNode( _key, _value );
            node.isRed = false;
            node.key = _key;
            int index = allocateNode();
            putNodeBits( index, node.encode() );
            treeRoot = index;
            return VALUE_NULL;
        }
        else if( y.notNULL() ) {  // if we got a reference to a node with our key, just update the value and leave...
            y.value( _value );
            return VALUE_NULL;
        }

        // if we get here, then we have to insert a new node where the NULL y reference (returned from search() above) is in the tree...

        // create the new node, store it in the tree, make it y (does the same as CLR:315@11-16)...
        Ref z = y.parent().child( y.whichChild, _key, _value );

        // while z's parent is red (CLR:316@1)...
        while( !z.isTreeRoot() && z.parent().isRed() ) {

            y = z.uncle();
            Dir dir = z.parent().whichChild; // controls direction of several things, to remove mirrored code...

            // if the uncle is red, we have the simple case 1 (CLR:316@4-8)...
            if( y.isRed() ) {

                // handle the simple recoloring case...
                z.parent().paintBlack();
                y.paintBlack();
                z.grandparent().paintRed();
                z = z.grandparent();
            }

            // otherwise, we have the more complex cases (CLR:316@9)...
            else {

                // if y and z are on the same side of their parents, set z to its parent and rotate the right way (CLR:316@9-11, case 2)...
                if( y.whichChild == z.whichChild ) {
                    z = z.parent();
                    rotate( dir, z );
                }

                // paint some new colors and rotate right (CLR:316@12-14, case 3)...
                z.parent().paintBlack();
                z.grandparent().paintRed();
                rotate( dir.oppo(), z.grandparent() );
            }
        }

        // paint the root black (CLR: page 168, line 18)...
        z.root().paintBlack();

        return VALUE_NULL;
    }


    /**
     * Removes the given key and its associated value from this index.  If the given key is out of range (negative or greater than the index's
     * capacity), an {@link IllegalArgumentException} is thrown.  The value associated with the given key is returned.  If the given key isn't in this
     * index, a VALUE_NULL is returned.
     *
     * @param _key the key to remove from this index
     * @return the value previously associated with the given key, or VALUE_NULL if there was none.
     */
    @Override
    public int remove( final int _key ) {

        if( (_key < 0) || (_key >= MAX_ENTRIES) )
            throw new IllegalArgumentException( "Key out of range: " + _key );

        // if the tree is empty, just leave with a VALUE_NULL return value...
        if( treeRoot == NULL )
            return VALUE_NULL;

        // if we don't have an entry with the given key, just bail out with a VALUE_NULL return value...
        Ref z = search( _key );
        if( z.isNULL() )
            return VALUE_NULL;

        // algorithm lifted straight from CLR:323-329, but modified to work with NULLs, removed mirrored code...

        // some positions we'll need...
        Ref y;
        Ref x;

        // save the old value so that we can return it when we're all finished...
        int oldValue = z.value();

        // for the moment, assume that we're splicing out the node we're deleting (CLR:324@1)...
        y = z;

        // if we have the easy case of z having a single child, handle that (CLR:324@2-8)...
        boolean yWasBlack = y.isBlack();
        if( z.leftChild().isNULL() ) {
            x = z.rightChild();
            z.transplant( z.rightChild() );
        }
        else if( z.rightChild().isNULL() ) {
            x = z.leftChild();
            z.transplant( z.leftChild() );
        }

        // otherwise we have the more challenging case of z having two children (CLR:324@9-20)...
        else {
            y = z.rightChild().minimum();  // finds the minimum node (smallest key) greater than z's key...
            yWasBlack = y.isBlack();
            x = y.rightChild();
            if( !z.equals( y.parent() )) {
                y.transplant( y.rightChild() );
                y.rightChild( z.rightChild() );
            }
            z.transplant( y );
            y.leftChild( z.leftChild() );
            y.paintLike( z );
        }

        // if we need to fix things up, go do it (CLR:324@21-22)...
        if( yWasBlack ) removeFixup( x );

        deleteNode( z.index() );  // get rid of the node in our tree storage...

        return oldValue;
    }


    /**
     * Fixes up any red/black tree principle violations after the basic node removal operation.
     *
     * @param _x the child below the removal splice
     */
    private void removeFixup( final Ref _x ) {

        // algorithm lifted from CLR:326...

        Ref x = _x;

        // so long as we still have fixing to do (CLR:326@1)...
        while( !x.isTreeRoot() && x.isBlack() ) {

            // get the direction this thing is working in (used to remove mirrored code, CLR:326@22)...
            Dir dir = x.whichChild;

            // get the sibling of our fixup node (CLR:326@2-3)...
            Ref w = x.parent().child( dir.oppo() );

            // handle case 1 (CLR:326@4-8)...
            if( w.isRed() ) {
                w.paintBlack();
                x.parent.paintRed();
                rotate( dir, x.parent() );
                w = x.parent().child( dir.oppo() );
            }

            // handle case 2 (CLR:326@9-11)...
            if( w.leftChild().isBlack() && w.rightChild().isBlack() ) {
                w.paintRed();
                x = x.parent();
            }

            // handle cases 3 and 4 (CLR:326@12)...
            else {

                // handle case 3 (CLR:326@12-16)...
                if( w.child( dir.oppo() ).isBlack() ) {
                    w.child( dir ).paintBlack();
                    w.paintRed();
                    rotate( dir.oppo(), w );
                    w = x.parent().child( dir.oppo() );
                }

                // handle case 4 (CLR:326@17-21)...
                w.paintLike( x.parent() );
                x.parent().paintBlack();
                w.child( dir.oppo() ).paintBlack();
                rotate( dir, x.parent() );
                x = x.root();
            }
        }

        // make sure the root is black (CLR:326@23)...
        x.paintBlack();
    }


    /**
     * Returns the number of keys (and their associated values) are contained in this index.
     *
     * @return the number of keys (and their associated values) are contained in this index
     */
    @Override
    public int size() {
        return size;
    }


    /**
     * Clears all entries from this index and releases all memory previously allocated to hold them.
     */
    @Override
    public void clear() {

        for( int i = 0; i < blocks.length; i++ )
            blocks[i] = null;

        deletedNodes = NULL;
        nextSlot = 0;
        treeRoot = NULL;
        size = 0;
    }


    /**
     * Searches the tree for the given key, and returns a reference to either the desired node (if it's already present) or to the NULL node where the
     * desired entry would have been if present.  If we started with an empty tree, a NULL with no parent is returned.
     *
     * @param _key the key to search for
     * @return a reference to either the desired node (if present) or a NULL node in the right place
     */
    private Ref search( final int _key ) {

        // if we have an empty tree, just return a NULL reference with no parent...
        if( treeRoot == NULL )
            return new Ref( NULL );

        // start our search at the root...
        Ref current = new Ref( treeRoot );

        // search until we find our key or run out of tree entries...
        while( current.notNULL() && (_key != current.key()) ) {
            current = (current.key() > _key) ? current.leftChild() : current.rightChild();
        }

        return current;
    }


    /**
     * Returns an iterator over the entries in this index.
     *
     * @return the iterator over this index's entries
     */
    @Override
    public IndexIterator iterator() {
        return new TreeIndexIterator();
    }


    /**
     * Implements an {@link IndexIterator} for this class.
     */
    private class TreeIndexIterator implements IndexIterator {

        private Ref current;
        private int value;
        private int key;

        private TreeIndexIterator() {
            current = toMinimum( new Ref( treeRoot ) );
        }


        private Ref toMinimum( final Ref _from ) {
            Ref x = _from;
            while( x.notNULL() ) {
                x = x.leftChild();
            }
            return x.parent();
        }


        /**
         * Returns true if and only if this iterator has another entry to return.
         *
         * @return true if this iterator has another entry
         */
        @Override
        public boolean hasNext() {
            return current.notNULL();
        }


        /**
         * Advances to the next entry in key order.  After invoking this method, the {@link #value()} and {@link #key()} methods will return the values of
         * that entry.
         */
        @Override
        public void next() {

            if( !hasNext() )
                throw new IllegalStateException( "Attempted to invoke next() when hasNext() is false" );

            // save our return values at the current position...
            value = current.value();
            key = current.key();

            // now advance as required to the next one, if there is one...
            if( current.rightChild().isNULL() ) {

                // move to the first parent node where we transited a left child link...
                boolean transitedLeft;
                do {

                    // if we're about to leave the root node, we're all done...
                    if( current.parent == null ) {
                        current = new Ref( NULL );
                        transitedLeft = true;
                    }

                    // otherwise move to the parent, and if we transited a left child link then we're done...
                    else {
                        transitedLeft = current.isLeftChild();
                        current = current.parent();

                        // lop off the child we just transited from (to save memory)...
                        if( transitedLeft )
                            current.leftChild = null;
                        else
                            current.rightChild = null;
                    }

                } while( !transitedLeft );

            }
            else {
                current = toMinimum( current.rightChild() );
            }
        }


        /**
         * Returns the value of the entry most recently advanced to through an invocation of {@link #next()}.
         *
         * @return the value of the current iterator entry
         */
        @Override
        public int value() {
            return value;
        }


        /**
         * Returns the key of the entry most recently advanced to through an invocation of {@link #next()}.
         *
         * @return the key of the current iterator entry
         */
        @Override
        public int key() {
            return key;
        }


        /**
         * Returns the count of entries that will be returned by this iterator.  This value does not change during iteration.
         *
         * @return the count of entries that will be returned by this iterator.
         */
        @Override
        public int entryCount() {
            return size;
        }
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
        for( long[] block : blocks )
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
        return 8 * (blocks.length * blocks[0].length - size);
    }


    /**
     * Rotate tree operation, as described in CLR:312-314, chapter 13.2, except that mirrored functions (for direction) have been replaced with a
     * single method that takes a direction parameter.
     *
     * @param _dir the direction (LEFT or RIGHT) of the rotation
     * @param _x a reference to the node to be rotated
     */
    private void rotate( final Dir _dir, final Ref _x ) {

        // algorithm lifted from CLR:313...

        // get the right child of the current node, modified to remove need for a RIGHT-ROTATE() (CLR:313@1)...
        @SuppressWarnings("UnnecessaryLocalVariable")
        Ref x = _x;
        Ref y = x.child( _dir.oppo() );

        // move y's left subtree to x's right child (CLR:313@2-5)...
        x.child( _dir.oppo(), y.child( _dir ) );

        // if the current node was the root, now y is (CLR:313@6-7)...
        if( x.isTreeRoot() ) {
            treeRoot = y.index();
            y.parent = null;
        }

        // otherwise, point x's parent to y instead of x (CLR:313@8-9)...
        else
            x.parent().child( x.whichChild, y );

        // make the x the child of y (CLR:313@11-12)...
        y.child( _dir, x );
    }


    /**
     * Provides a reference to a tree node, with pointers to its parent and children, and a record of whether it's the left or right child of its
     * parent.  Tree structures built with instances of this class are used ephemerally to represent portions of the tree structure used by
     * {@link #put(int, int)}, {@link #remove(int)} and their associated methods.  The main purpose of this class is to eliminate the need for
     * storing parent pointers in the encoded tree structure, thus saving considerable memory (by allowing a node to be encoded into a single long).
     * A secondary purpose is to provide convenience methods for accessing and changing the encoded node itself, in the process making the higher
     * level code (particularly in {@link #put(int, int)}, {@link #remove(int)} and their associated methods) much easier to read.
     */
    private class Ref {

        private int index;
        private Ref parent;
        private Dir whichChild;
        private Ref leftChild;
        private Ref rightChild;


        private Ref( final int _index ) {
            index = _index;
        }


        private Ref( final int _index, final Ref _parent, final Dir _whichChild ) {
            index = _index;
            parent = _parent;
            whichChild = _whichChild;
        }


        private long bits() {
            if( index == NULL )
                throw new IllegalStateException( "Attempt to dereference NULL index" );
            return getNodeBits( index );
        }


        private int index() {
            return index;
        }


        private boolean isTreeRoot() {
            return parent == null;
        }


        private boolean isLeftChild() {
            return whichChild == Dir.LEFT;
        }


        private boolean isNULL() {
            return index == NULL;
        }


        private boolean notNULL() {
            return index != NULL;
        }


        private int value() {
            return Node.value( bits() );
        }


        /**
         * Sets the referenced node's value, and returns the previous value.
         *
         * @param _value the new value for the referenced node
         * @return the previous value of the referenced node
         */
        private int value( final int _value ) {
            long oldBits = bits();
            int oldValue = Node.value( oldBits );
            long newBits = Node.replaceValue( oldBits, _value );
            putNodeBits( index, newBits );
            return oldValue;
        }


        private int key() {
            return Node.key( bits() );
        }


        private Ref leftChild() {
            if( leftChild == null )
                leftChild = new Ref( Node.leftChild( bits() ), this, Dir.LEFT );
            return leftChild;
        }


        private Ref leftChild( final Ref _newChild ) {
            _newChild.parent = this;
            _newChild.whichChild = Dir.LEFT;
            long newBits = Node.replaceLeftChild( bits(), _newChild.index );
            putNodeBits( index, newBits );
            leftChild = _newChild;
            return leftChild;
        }


        private Ref leftChild( final int _key, final int _value ) {
            int newIndex = allocateNode();
            long newBits = Node.newNode( _key, _value ).encode();
            putNodeBits( newIndex, newBits );
            return leftChild( new Ref( newIndex ) );
        }


        private Ref rightChild() {
            if( rightChild == null )
                rightChild = new Ref( Node.rightChild( bits() ), this, Dir.RIGHT );
            return rightChild;
        }


        private Ref rightChild( final Ref _newChild ) {
            _newChild.parent = this;
            _newChild.whichChild = Dir.RIGHT;
            long newBits = Node.replaceRightChild( bits(), _newChild.index );
            putNodeBits( index, newBits );
            rightChild = _newChild;
            return rightChild;
        }


        private Ref rightChild( final int _key, final int _value ) {
            int newIndex = allocateNode();
            long newBits = Node.newNode( _key, _value ).encode();
            putNodeBits( newIndex, newBits );
            return rightChild( new Ref( newIndex ) );
        }


        private Ref child( final Dir _side ) {
            return (_side == Dir.LEFT) ? leftChild() : rightChild();
        }


        private Ref child( final Dir _side, final Ref _newChild ) {
            return (_side == Dir.LEFT) ? leftChild( _newChild ) : rightChild( _newChild );
        }


        private Ref child( final Dir _side, final int _key, final int _value ) {
            return (_side == Dir.LEFT) ? leftChild( _key, _value ) : rightChild( _key, _value );
        }


        private boolean isRed() {
            return (index != NULL) && Node.isRed( bits() );
        }


        private boolean isBlack() {
            return (index == NULL) || Node.isBlack( bits() );
        }


        private void paintRed() {
            putNodeBits( index(), Node.paintRed( bits() ) );
        }


        private void paintBlack() {
            if( notNULL() )
                putNodeBits( index(), Node.paintBlack( bits() ) );
        }


        private void paintLike( final Ref _x ) {
            if( _x.isBlack() )
                paintBlack();
            else
                paintRed();
        }


        private Ref root() {
            return (parent == null) ? this : parent.root();
        }


        private Ref parent() {
            if( parent == null )
                throw new IllegalStateException( "Attempted to reference null parent" );
            return parent;
        }


        private Ref grandparent() {
            return parent().parent();
        }


        private Ref uncle() {
            return (parent().whichChild == Dir.LEFT) ? grandparent().rightChild() : grandparent().leftChild();
        }


        public String toString() {
            return "Ref: " + index;
        }


        /**
         * Returns a reference to the minimum key under this reference, which <i>may</i> be this reference itself.
         *
         * @return a reference to the minimum key under the given reference
         */
        private Ref minimum() {
            Ref x = this;
            while( x.leftChild().notNULL() )
                x = x.leftChild();
            return x;
        }


        /**
         * Replaces this node (and its sub-nodes) with the given node (and its sub-nodes).
         *
         * @param _v the replacement node
         */
        private void transplant( final Ref _v ) {
            if( isTreeRoot() ) {
                treeRoot = _v.index();
                _v.parent = parent;
            }
            else
                parent().child( whichChild, _v );
        }


        @Override
        public boolean equals( final Object o ) {
            if( this == o ) return true;
            if( o == null || getClass() != o.getClass() ) return false;

            Ref ref = (Ref) o;

            return index == ref.index;
        }


        @Override
        public int hashCode() {
            return index;
        }
    }


    private Node getNode( final int _index ) {
        return Node.decode( getNodeBits( _index ) );
    }


    private int allocateNode() {

        // track the number of used slots...
        size++;

        // first we see if there's a deleted node available...
        if( deletedNodes != NULL ) {
            int result = deletedNodes;  // we're going to return the first deleted node as the allocated node...
            deletedNodes = Node.key( getNodeBits( deletedNodes ) );
            return result;
        }

        // otherwise, we take the next free slot (space never previously allocated)...
        int block = nextSlot >>> blockOffsetShift;
        int offset = nextSlot & blockOffsetMask;

        if( block >= blocks.length )
            throw new IllegalStateException( "Index size exceeded" );

        // make a new block if necessary...
        if( blocks[block] == null ) {
            blocks[block] = new long[(block == 0) ? initialBlockSize : blockSize];
            return nextSlot++;
        }

        // if we're on the initial block, expand as required...
        if( (block == 0) && (offset >= blocks[0].length) ) {
            blocks[0] = Arrays.copyOf( blocks[0], blocks[0].length << 1 );
        }

        return nextSlot++;
    }


    private void checkValidIndex( final int _index ) {

        if( (_index < 0) || (_index >= nextSlot) )
            throw new IllegalArgumentException( "Index out of range: " + _index );
    }


    private void checkValidOccupiedNode( final int _index ) {

        if( Node.isDeleted( getNodeBits( _index ) ) )
            throw new IllegalStateException( "Attempt to access deleted node at index: " + _index );
    }


    private long getNodeBits( final int _index ) {
        checkValidIndex( _index );
        return blocks[_index >>> blockOffsetShift][_index & blockOffsetMask];
    }


    private void putNodeBits( final int _index, final long _bits ) {
        checkValidIndex( _index );
        blocks[_index >>> blockOffsetShift][_index & blockOffsetMask] = _bits;
    }


    private void deleteNode( final int _index ) {
        size--;
        checkValidOccupiedNode( _index );
        putNodeBits( _index, Node.deleted( deletedNodes ) );
        deletedNodes = _index;
    }


    private enum Dir {

        LEFT, RIGHT;

        private Dir oppo() {
            return (this == LEFT) ? RIGHT : LEFT;
        }
    }


    /*
     * Node format for occupied node
     *
     *   63  62  61  60 59             36 35     24 23     12 11      0
     * ----------------------------------------------------------------
     * | C | I |  NU  |         V        |     RI  |    LI   |    K   |
     * ----------------------------------------------------------------
     *
     * where:
     *     C: color (0 = black, 1 = red)
     *     I: node type ID (0 = occupied, 1 = deleted)
     *    NU: not used
     *     V: value
     *    RI: right child index (NULL = empty)
     *    LI: left child index (NULL = empty)
     *     K: key
     *
     *
     * Block format for deleted node
     *
     *   63  62  61                                           11      0
     * ----------------------------------------------------------------
     * |NU | I |                       NU                    |  DNI   |
     * ----------------------------------------------------------------
     *
     * where:
     *   DNI: deleted node index (link to next deleted node, or NULL if the end)
     *    NU: not used
     *     I: node type ID (0 = occupied, 1 = deleted)
     */

    /**
     * Wraps a node entry stored in a long slot with methods to encode and decode the entry into more convenient Java fields.
     */
    private static class Node {

        private static final int INDEX_MASK = 0x0000_0FFF;
        private static final int VALUE_MASK = 0x00FF_FFFF;

        private static final long COLOR_BIT   = 1L << 63;
        private static final long DELETED_BIT = 1L << 62;

        private static final int VALUE_OFFSET       = 36;
        private static final int LEFT_CHILD_OFFSET  = 12;
        private static final int RIGHT_CHILD_OFFSET = 24;

        private static final long REPLACE_VALUE_MASK       = ~((long)VALUE_MASK << VALUE_OFFSET);
        private static final long REPLACE_LEFT_CHILD_MASK  = ~((long)INDEX_MASK << LEFT_CHILD_OFFSET);
        private static final long REPLACE_RIGHT_CHILD_MASK = ~((long)INDEX_MASK << RIGHT_CHILD_OFFSET);

        private int     key;
        private int     leftChild;
        private int     rightChild;
        private int     value;
        private boolean isRed;
        private boolean isOccupied;


        private static long deleted( final int _index ) {
            return DELETED_BIT | _index;
        }


        private long encode() {

            return isOccupied
                ?
                    (isRed ? COLOR_BIT : 0) |
                    ((long)value << VALUE_OFFSET) |
                    ((long)rightChild << RIGHT_CHILD_OFFSET) |
                    (leftChild << LEFT_CHILD_OFFSET) |
                    key
                :
                    DELETED_BIT | key;
        }


        private static Node newNode( final int _key, final int _value ) {
            Node node = new Node();
            node.isRed = true;
            node.isOccupied = true;
            node.key = _key;
            node.value = _value;
            node.leftChild = NULL;
            node.rightChild = NULL;
            return node;
        }


        private static long replaceValue( final long _bits, final int _value ) {
            return (_bits & REPLACE_VALUE_MASK) | ((long)_value << VALUE_OFFSET);
        }


        private static long replaceLeftChild( final long _bits, final int _leftChild ) {
            return (_bits & REPLACE_LEFT_CHILD_MASK) | ((long)_leftChild << LEFT_CHILD_OFFSET);
        }


        private static long replaceRightChild( final long _bits, final int _rightChild ) {
            return (_bits & REPLACE_RIGHT_CHILD_MASK) | ((long)_rightChild << RIGHT_CHILD_OFFSET);
        }


        private static long paintBlack( final long _bits ) {
            return _bits & ~COLOR_BIT;
        }


        private static long paintRed( final long _bits ) {
            return _bits | COLOR_BIT;
        }


        private static int key( final long _bits ) {
            return ((int)_bits) & INDEX_MASK;
        }


        private static int leftChild( final long _bits ) {
            return ((int)(_bits >>> LEFT_CHILD_OFFSET)) & INDEX_MASK;
        }


        private static int rightChild( final long _bits ) {
            return ((int)(_bits >>> RIGHT_CHILD_OFFSET)) & INDEX_MASK;
        }


        private static int value( final long _bits ) {
            return ((int)(_bits >>> VALUE_OFFSET)) & VALUE_MASK;
        }


        private static boolean isOccupied( final long _bits ) {
            return (_bits & DELETED_BIT) == 0;
        }


        private static boolean isDeleted( final long _bits ) {
            return (_bits & DELETED_BIT) != 0;
        }


        private static boolean isRed( final long _bits ) {
            return (_bits & COLOR_BIT) != 0;
        }


        private static boolean isBlack( final long _bits ) {
            return (_bits & COLOR_BIT) == 0;
        }


        private static Node decode( final long _bits ) {

            Node result = new Node();
            result.key      = key( _bits );
            result.leftChild  = leftChild( _bits );
            result.rightChild = rightChild( _bits );
            result.value      = value( _bits );
            result.isOccupied = isOccupied( _bits );
            result.isRed      = isRed( _bits );
            return result;
        }
    }


    /*
     * T E S T   H A R N E S S
     *
     * All the following methods are here strictly for testing purposes.  There's no purpose for them in actual use, and they should be avoided
     * as they may well have signature changes or disappear.  In other words, these are NOT supported API!
     */


    /**
     * Validates the structure of the internal red/black tree and collects some statistics.  This method is intended for testing purposes only, and
     * may be removed from the API.  Errors found during validation are printed to the system console.
     *
     * @return the statistics collected during validation.
     */
    @Deprecated
    public Stats validate() {
        return (treeRoot == NULL) ? new Stats() : validationWalk( treeRoot, new HashSet<>(), -1 );
    }


    private Stats validationWalk( final int _index, final HashSet<Integer> _circularity, final int _lastKey ) {
        Node node = getNode( _index );
        Stats mine = new Stats();
        Stats left = new Stats();
        Stats right = new Stats();

        boolean beenHere = _circularity.contains( _index );
        _circularity.add( _index );

        if( !beenHere ) {

            if( node.leftChild != NULL ) {
                left = validationWalk( node.leftChild, _circularity, _lastKey );
                if( node.key < left.key ) {
                    mine.valid = false;
                    out( "Child key out of order: " + node.key + " < " + left.key );
                }
            } else {
                if( node.key < _lastKey ) {
                    mine.valid = false;
                    out( "My key out of order: " + node.key + " < " + _lastKey );
                }
            }

            if( node.rightChild != NULL ) {
                right = validationWalk( node.rightChild, _circularity, node.key );
            }
        }

        mine.isRed = node.isRed;
        mine.key = node.key;
        if( node.isRed && (left.isRed || right.isRed) ) {
            mine.valid = false;
            out( "Node with key " + node.key + ", at index " + _index + " is red and has at least one red child" );
        }
        if( left.blackHeight != right.blackHeight ) {
            mine.valid = false;
            out( "Node with key " + node.key + ", at index " + _index + " has mismatched black height in its children (" +
                    left.blackHeight + " and " + right.blackHeight + ")");
        }
        if( !(left.valid && right.valid) )
            mine.valid = false;
        mine.blackHeight = left.blackHeight + (node.isRed ? 0 : 1);
        mine.minHeight = 1 + Math.min( left.minHeight, right.minHeight );
        mine.maxHeight = 1 + Math.max( left.maxHeight, right.maxHeight );
        mine.reds = left.reds + right.reds + (node.isRed ? 1 : 0);
        mine.blacks = left.blacks + right.blacks + (node.isRed ? 0 : 1);
        mine.nodes = 1 + left.nodes + right.nodes;
        if( beenHere ) {
            mine.valid = false;
            out( "Node with key " + node.key + ", at index " + _index + " has a circular reference to it" );
        }

        return mine;
    }


    private void out( final String _msg ) {
        System.out.println( _msg );
    }


    public class Stats {
        public boolean valid = true;
        public boolean isRed;
        public int blackHeight = 0;
        public int minHeight = 0;
        public int maxHeight = 0;
        public int reds = 0;
        public int blacks = 0;
        public int nodes = 0;
        public int key = 0;

        public String toString() {
            StringBuilder sb = new StringBuilder();
            if( valid ) sb.append( "Valid, " ); else sb.append( "Invalid, " );
            if( isRed ) sb.append( "red, " ); else sb.append( "black, " );
            sb.append( "nodes: " );
            sb.append( nodes );
            sb.append( ", " );
            sb.append( "black height: " );
            sb.append( blackHeight );
            sb.append( ", " );
            sb.append( "min height: " );
            sb.append( minHeight );
            sb.append( ", " );
            sb.append( "max height: " );
            sb.append( maxHeight );
            sb.append( ", " );
            sb.append( "black nodes: " );
            sb.append( blacks );
            sb.append( ", " );
            sb.append( "red nodes: " );
            sb.append( reds );
            sb.append( "." );
            return sb.toString();
        }
    }
}

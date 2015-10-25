package com.cirsim.matrices;

import com.cirsim.util.Numbers;

import java.util.Arrays;
import java.util.HashSet;

import static com.cirsim.util.Numbers.closestBinaryPower;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */

/*
 * References in code comments prefaced with "CLR:" are references to the book "Introduction to Algorithms", by Thomas H. Cormen, Charles E.
 * Leiserson, and Ronald L. Rivest.  Page numbers refer to the pages in the first edition, second printing of 1990, ISBN 0-262-03141-8.
 */
public class TreeIndex implements Index {

    public static final int MAX_ENTRIES = 4096;
    public static final int NULL        = 0xFFF;
    public static final int VALUE_NULL  = 0xFF_FFFF;

    private static final int MIN_INITIAL_BLOCK_SIZE = 4;  // set low so that sparsely populated trees take little room...
    private static final int MIN_BLOCK_SIZE         = 32; // to keep us from having a block array filled with really tiny blocks on small stores...

    private final int blockOffsetShift;
    private final int blockOffsetMask;
    private final int initialBlockSize;
    private final int blockSize;
    private final Stack stack;

    private long[][] blocks;
    private int deletedNodes;
    private int nextSlot;
    private int treeRoot;


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

        // determine the stack size and initialize it...
        stack = new Stack( Numbers.closestBinaryPowerLog( _maxEntries ) << 1 );
    }


    /**
     * Returns the 24 bit integer value associated with the given key, or the special value VALUE_NULL if the given key is not contained in the
     * index, but <i>is</i> within the range of the index.  If the given key is out of range (negative or greater than the index's capacity), an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param _key the key to look up the associated value with
     * @return the value associated with the given key
     */
    @Override
    public int get( final int _key ) {
        return getValue( _key );
    }


    /**
     * Puts the given 24 bit integer value into the index, and associates it with the given key.  If the given key is out of range (negative or
     * greater than the index's capacity), an {@link IllegalArgumentException} is thrown.  If the given value is outside the range of a 24 bit
     * unsigned integer, is negative, or is equal to the special value NULL (0xFFFFFF), an {@link IllegalArgumentException} is thrown.
     *
     * @param _key   the key to associate the value with, and store in the index
     * @param _value the value to associate with the key
     */
    @Override
    public void put( final int _key, final int _value ) {
        putTree( _key, _value );
    }


    /**
     * Returns an iterator over the entries in this index, with the given order and filter mode.
     * <p>
     * The order mode determines the order that the returned iterator will iterate over the index's entries.  This may be either <i>index</i> order
     * (which means in numerical index order, <i>0 .. n</i>), or <i>unspecified</i> order (which means any order at all, including <i>index</i>. Some
     * <code>Vector</code> implementations iterate faster in <i>unspecified</i> order mode.
     * <p>
     * The filter mode determines <i>which</i> of this vector's entries the returned iterator will iterate over.  This may be either <i>unfiltered</i>
     * (which means <i>all</i> entries) or <i>sparse</i> (which means only set, or nonzero, entries).  For sparsely populated vectors, the
     * <i>sparse</i> filter mode can be significantly faster.
     *
     * @param _orderMode  the order mode for the returned iterator (either index order or unspecified order)
     * @param _filterMode the filter mode for the returned iterator (either unfiltered, or set entries)
     * @return the iterator over this index's entries in the given order and filter mode
     */
    @Override
    public IndexIterator iterator( final IndexIteratorOrderMode _orderMode, final IndexIteratorFilterMode _filterMode ) {
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


    /**\
     *  Puts the given value into the tree at the given key.  If the tree already contains an entry with that key, the associated value is
     *  updated and the previous value returned.  If the tree has no entry at that key, the entry is inserted and assigned the given value, and
     *  VALUE_NULL is returned.
     *
     * @param _key the key to save a value for
     * @param _value the value to associate with the given key
     * @return the former value associated with the given key, or VALUE_NULL if there was none
     */
    private int putTree( final int _key, final int _value ) {

        // if the tree is empty, just store the new node as the root node and leave...
        if( treeRoot == NULL ) {
            int index = allocateNode();
            Node newNode = Node.newNode( _key, _value );
            newNode.isRed = false;
            putNode( index, newNode );
            treeRoot = index;
            return VALUE_NULL;
        }

        // we reuse the stack to avoid the overhead of destroying and reallocating it...
        stack.clear();

        // if we already have a node with the given index, just update its value...
        if( walkToPut( treeRoot, _key ) ) {
            return stack.top.value( _value );
        }

        // if we get here, then we have to insert a new node at the right place in the tree...
        // note that the top of stack will contain the correct parent for our new node...

        // create the new node, store it in the tree, and push it on our stack...
        int newIndex = allocateNode();
        long newBits = Node.newNode( _key, _value ).encode();
        putNodeBits( newIndex, newBits );
        if( stack.top.key() > _key )
            stack.top.leftChild( newIndex );
        else
            stack.top.rightChild( newIndex );
        stack.push( newIndex, newBits );

        // algorithm lifted straight from CLR: page 268, but modified to eliminate mirrored code and to work without parent pointers...

        // while x is not the root and x's parent is red...
        Stack.Position x = stack.top;
        while( !x.isTreeRoot() && x.parent().isRed() ) {

            // if x's parent is its grandparent's left child, then its uncle is the grandparent's right child (and vice versa)...
            // this determines whether several things below operate on right or left side...
            boolean uncleOnRight = x.parent().index() == x.grandparent().leftChild();

            int uncleIndex = uncleOnRight ? x.grandparent().rightChild() : x.grandparent().leftChild();
            if( isNotNullAndRed( uncleIndex ) ) {

                // handle the simple recoloring case...
                x.parent().paintBlack();
                paintBlackIfNotNull( uncleIndex );
                x.grandparent().paintRed();
                x = x.grandparent();
            }
            else {

                // if the x is the right child of its parent, set x to its parent and rotate left
                if( x.index() == (uncleOnRight ? x.parent().rightChild() : x.parent().leftChild()) ) {
                    rotate( uncleOnRight ? Dir.LEFT : Dir.RIGHT, x.parent() );
                    stack.swap( x, x.parent() );   //  correcting the stack;  this has the side-effect of making x's parent become x...
                }

                // paint some new colors and rotate right (CLR: page 268, lines 14-16)...
                x.parent().paintBlack();
                x.grandparent().paintRed();
                rotate( uncleOnRight ? Dir.RIGHT : Dir.LEFT, x.grandparent() );
                stack.delete( x.grandparent() );   // correcting the stack for the effects of rotation...
            }
        }

        // paint the root black (CLR: page 168, line 18)...
        putNodeBits( treeRoot, Node.paintBlack( getNodeBits( treeRoot ) ) );

        return VALUE_NULL;
    }


    private void paintBlackIfNotNull( final int _index ) {
        if( _index != NULL )
            putNodeBits( _index, Node.paintBlack( getNodeBits( _index ) ) );
    }


    private boolean isNotNullAndRed( final int _index ) {

        // leaves are black by definition...
        return (_index != NULL) && Node.isRed( getNodeBits( _index ) );
    }


    // algorithm from CLR: page 266, figure 14.3...
    private void rotate( final Dir _dir, final Stack.Position _pos ) {

        boolean isLeft = (_dir == Dir.LEFT);

        // get the right child of the current node (which is one higher on the stack in our case)...
        // CLR: page 266, line 1...
        @SuppressWarnings("UnnecessaryLocalVariable")
        Stack.Position x = _pos;
        Stack.Position y = _pos.child();
        Stack.Position p = _pos.parent();

        // move y's left subtree to x's right child...
        // CLR: page 266, lines 2-5 (note that parent fixups are not required in our parent-less tree)...
        if( isLeft ) x.rightChild( y.leftChild() ); else x.leftChild( y.rightChild() );

        // if the current node was the root, now y is (CLR: page 266, lines 6-7)...
        // otherwise, point x's parent to y instead of x (CLR: page 266, lines 8-10)...
        if( x.isTreeRoot() )
            treeRoot = y.index();
        else if( x.index() == p.leftChild() )
            p.leftChild( y.index() );
        else
            p.rightChild( y.index() );

        // make the x the left child of y (CLR: page 266, lines 11-12, but note the parent fixup is not required)...
        if( isLeft ) y.leftChild( x.index() ); else y.rightChild( x.index() );
    }


    private boolean walkToPut( final int _index, final int _key ) {

        // if we have a NULL index, the given index wasn't found...
        if( _index == NULL )
            return false;

        // get the basic information and push it onto our stack...
        long bits = getNodeBits( _index );
        stack.push( _index, bits );

        // if this node has the index we're searching for, return with our glorious success (info is at top of stack)...
        int key = Node.key( bits );
        if( key == _key )
            return true;

        // otherwise, we need to keep right on searching...
        return (key > _key) ? walkToPut( Node.leftChild( bits ), _key ) : walkToPut( Node.rightChild( bits ), _key ) ;
    }


    /**
     * Validates the structure of the internal red/black tree and collects some statistics.  This method is intended for testing purposes only, and
     * may be removed from the API.  Errors found during validation are printed to the system console.
     *
     * @return the statistics collected during validation.
     */
    @Deprecated
    public Stats validate() {
        return validationWalk( treeRoot, new HashSet<Integer>() );
    }


    private Stats validationWalk( final int _index, final HashSet<Integer> _circularity ) {
        Node node = getNode( _index );
        Stats left = new Stats();
        Stats right = new Stats();

        boolean beenHere = _circularity.contains( _index );
        _circularity.add( _index );

        if( !beenHere ) {

            if( node.leftChild != NULL ) {
                left = validationWalk( node.leftChild, _circularity );
            }

            if( node.rightChild != NULL ) {
                right = validationWalk( node.rightChild, _circularity );
            }
        }

        Stats mine = new Stats();
        mine.isRed = node.isRed;
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


    private class Stack {

        private final long[] bits;
        private final int[]  index;
//        private int sp;
        private Position top;


        private Stack( final int _depth ) {
            bits = new long[_depth];
            index = new int[_depth];
            clear();
        }


        private void clear() {
            top = new Position( -1 );
        }


        private void push( final int _index, final long _bits ) {
            top = top.inc();
            index[top.pos] = _index;
            bits[top.pos] = _bits;
        }


        private void swap( final Position _a, final Position _b ) {
            long bs       = _a.bits();
            int ns        = _a.index();

            bits [_a.pos] = bits [_b.pos];
            index[_a.pos] = index[_b.pos];

            bits [_b.pos] = bs;
            index[_b.pos] = ns;
        }


        private void delete( Position _a ) {
            if( (_a.pos > top.pos || ((_a.pos) < 0)) )
                throw new IllegalArgumentException( "Delete position: " + _a );

            System.arraycopy( bits,  _a.pos + 1, bits,  _a.pos, top.pos - _a.pos );
            System.arraycopy( index, _a.pos + 1, index, _a.pos, top.pos - _a.pos );
            top = top.dec();
        }


        private class Position {

            private final int pos;

            private Position( final int _pos ) {
                pos = _pos;
            }


            private Position inc() {
                return new Position( pos + 1 );
            }


            private Position dec() {
                return new Position( pos - 1 );
            }


            private long bits() {
                return bits[pos];
            }


            private long bits( final long _bits ) {
                long oldBits = bits[pos];
                bits[pos] = _bits;
                putNodeBits( index[pos], _bits );
                return oldBits;
            }


            private int index() {
                return index[pos];
            }


            private boolean isTreeRoot() {
                return index[pos] == treeRoot;
            }


            private int value() {
                return Node.value( bits[pos] );
            }


            private int value( final int _value ) {
                int oldValue = Node.value( bits[pos] );
                bits[pos] = Node.replaceValue( bits[pos], _value );
                putNodeBits( index[pos], bits[pos] );
                return oldValue;
            }


            private int key() {
                return Node.key( bits[pos] );
            }


            private int leftChild() {
                return Node.leftChild( bits[pos] );
            }


            private int leftChild( final int _index ) {
                int oldLeftChild = Node.leftChild( bits[pos] );
                bits[pos] = Node.replaceLeftChild( bits[pos], _index );
                putNodeBits( index[pos], bits[pos] );
                return oldLeftChild;
            }


            private int rightChild() {
                return Node.rightChild( bits[pos] );
            }


            private int rightChild( final int _index ) {
                int oldRightChild = Node.rightChild( bits[pos] );
                bits[pos] = Node.replaceRightChild( bits[pos], _index );
                putNodeBits( index[pos], bits[pos] );
                return oldRightChild;
            }


            private boolean isRed() {
                return Node.isRed( bits[pos] );
            }


            private boolean isBlack() {
                return Node.isBlack( bits[pos] );
            }


            private void paintRed() {
                bits[pos] = Node.paintRed( bits[pos] );
                putNodeBits( index[pos], bits[pos] );
            }


            private void paintBlack() {
                bits[pos] = Node.paintBlack( bits[pos] );
                putNodeBits( index[pos], bits[pos] );
            }


            private Position parent() {
                return new Position( pos - 1 );
            }


            private Position grandparent() {
                return new Position( pos - 2 );
            }


            private Position child() {
                return new Position( pos + 1 );
            }

            public String toString() {
                return "" + pos;
            }
        }
    }


    /**
     * Return the value associated with the given index.  If there is no entry in the tree with the given index, return VALUE_NULL.
     *
     * @param _index the index to find a value for
     * @return the value associated with the given index
     */
    private int getValue( final int _index ) {

        int index = walkTo( treeRoot, _index );
        return (index == NULL) ? -1 : Node.value( getNodeBits( _index ) );
    }


    private int walkTo( final int _index, final int _key ) {

        if( _index == NULL )
            return NULL;

        long bits = getNodeBits( _index );
        int key = Node.key( bits );

        if( key == _key )
            return _index;

        return (key > _key) ? walkTo( Node.leftChild( bits ), _key ) : walkTo( Node.rightChild( bits ), _key );
    }


    private Node getNode( final int _index ) {
        return Node.decode( getNodeBits( _index ) );
    }


    private void putNode( final int _index, final Node _node ) {
        putNodeBits( _index, _node.encode() );
    }


    private int allocateNode() {

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
        checkValidOccupiedNode( _index );
        putNodeBits( _index, Node.deleted( deletedNodes ) );
        deletedNodes = _index;
    }


    private enum Dir {
        LEFT, RIGHT;
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
            return (_bits & (~((long)VALUE_MASK << VALUE_OFFSET))) | ((long)_value << VALUE_OFFSET);
        }


        private static long replaceLeftChild( final long _bits, final int _leftChild ) {
            return (_bits & (~((long)INDEX_MASK << LEFT_CHILD_OFFSET))) | ((long)_leftChild << LEFT_CHILD_OFFSET);
        }


        private static long replaceRightChild( final long _bits, final int _rightChild ) {
            return (_bits & (~((long)INDEX_MASK << RIGHT_CHILD_OFFSET))) | ((long)_rightChild << RIGHT_CHILD_OFFSET);
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
}

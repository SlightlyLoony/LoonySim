package com.cirsim.matrices;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class TreeIndex implements Index {

    private static final int MAX_VECTOR_ENTRIES = 4096;
    private static final int NODES_PER_BLOCK = 64;
    private static final int LONGS_PER_NODE = 2;
    private static final int MAX_BLOCKS = MAX_VECTOR_ENTRIES / NODES_PER_BLOCK;
    private static final int NULL = 0xFFF;
    private static final int INDEX_MASK = 0xFFF;
    private static final int BLOCK_NUMBER_OFFSET = 6;
    private static final int BLOCK_OFFSET_MASK = 0x3F;
    private static final int LEFT_CHILD_OFFSET = 12;
    private static final int RIGHT_CHILD_OFFSET = 24;
    private static final long COLOR_BIT = 0x80000000L;
    private static final long UNALLOCATED_BIT = 0x40000000L;

    /*
     * Node format for occupied node
     *
     *   63  62  61  60 59             36 35     24 23     12 11      0
     * ----------------------------------------------------------------
     * | C | I |  NU  |        VK        |    RNI  |   LNI   |   EI   |
     * ----------------------------------------------------------------
     *
     * where:
     *     C: color (0 = black, 1 = red)
     *     I: node type ID (0 = occupied, 1 = deleted)
     *    NU: not used
     *    VK: value key
     *   RNI: right child node index (NULL = empty)
     *   LNI: left child node index (NULL = empty)
     *    EI: entry index
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
     *   DNI: deleted node index (link to next one, NULL if the end)
     *    NU: not used
     *     I: node type ID (0 = occupied, 1 = deleted)
     */

    private final long[][] treeBlocks = new long[MAX_BLOCKS][];
    private int deletedNodes;
    private int nextFreeNode;
    private int treeRoot;

    public TreeIndex() {

        deletedNodes = NULL;
        nextFreeNode = 0;
        treeRoot = NULL;
    }


    /**
     * Returns the 24 bit integer value associated with the given key, or the special value NULL (0xFFFFFF) if the given key is not contained in the
     * index, but <i>is</i> within the range of the index.  If the given key is out of range (negative or greater than the index's capacity), an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param _key the key to look up the associated value with
     * @return the value associated with the given key
     */
    @Override
    public int get( final int _key ) {
        return 0;
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

    private void putTree( final int _col, final double _val ) {
    }


    private double getTree( final int _col ) {

        Node node = walkTo( treeRoot, _col );

        return 0;  // TODO stopping point
    }


    private Node walkTo( final int _nodeIndex, final int _col ) {

        if( _nodeIndex == NULL )
            return null;

        Node node = getNode( _nodeIndex );

        if( node.column == _col )
            return node;

        if( node.column > _col )
            return walkTo( node.leftChild, _col );

        return walkTo( node.rightChild, _col );
    }


    private Node getNode( final int _nodeIndex ) {
        int block = _nodeIndex >>> BLOCK_NUMBER_OFFSET;
        int offset = (_nodeIndex & BLOCK_OFFSET_MASK) << 1;  // left shift one to multiply by two, fast...

        if( treeBlocks[block] == null)
            throw new IllegalStateException( "Attempt to reference non-existent block: " + block );

        return new Node( treeBlocks[block][offset], treeBlocks[block][offset + 1] );
    }


    private void putNode( final int _nodeIndex, final Node _node ) {
        int block = _nodeIndex >>> BLOCK_NUMBER_OFFSET;
        int offset = (_nodeIndex & BLOCK_OFFSET_MASK) << 1;  // left shift one to multiply by two, fast...

        if( treeBlocks[block] == null)
            throw new IllegalStateException( "Attempt to reference non-existent block: " + block );

        treeBlocks[block][offset] = _node.getMetaBits();
        treeBlocks[block][offset + 1] = _node.getValueBits();
    }


    private int allocateNode() {

        // first we see if there's a deleted node available...
        if( deletedNodes != NULL ) {
            int result = deletedNodes;  // we're going to return the first deleted node as the allocated node...
            Node newRoot = getNode( result );
            return result;
        }

        // otherwise, we take the next free node (space never previously allocated)...
        else {

            int block = nextFreeNode >>> BLOCK_NUMBER_OFFSET;
            int offset = (nextFreeNode & BLOCK_OFFSET_MASK) << 1;  // left shift one to multiply by two, fast...
            int result = nextFreeNode;

            // if we're allocating the first node in a block, we need to allocate the whole block...
            if( offset == 0)
                treeBlocks[block] = new long[LONGS_PER_NODE * NODES_PER_BLOCK];

            nextFreeNode++;
            if( nextFreeNode >= MAX_BLOCKS )
                throw new IllegalStateException( "Maximum number of blocks exceeded" );

            return result;
        }
    }


    private void deleteNode( final int _nodeIndex ) {
        Node wiper = new Node( deletedNodes, 0 );
        wiper.isUnallocated = true;
        putNode( _nodeIndex, wiper );
        deletedNodes = _nodeIndex;
    }


    private static class Node {
        private int column;
        private int leftChild;
        private int rightChild;
        private boolean isRed;
        private double value;
        private boolean isUnallocated;


        private long getMetaBits() {

            if( isUnallocated ) {
                return UNALLOCATED_BIT | column;
            }
            else {
                long result = (rightChild << RIGHT_CHILD_OFFSET) | (leftChild << LEFT_CHILD_OFFSET) | column;
                if( isRed ) result |= COLOR_BIT;
                return result;
            }
        }


        private long getValueBits() {
            return Double.doubleToRawLongBits( value );
        }


        private Node( final long _meta, final long _value ) {

            // decode all the fields...
            column =     ((int) _meta) & INDEX_MASK;
            leftChild =  ((int)(_meta >>> LEFT_CHILD_OFFSET)) & INDEX_MASK;
            rightChild = ((int)(_meta >>> RIGHT_CHILD_OFFSET)) & INDEX_MASK;
            isRed = _meta < 0;
            value = Double.longBitsToDouble( _value );
        }
    }
}

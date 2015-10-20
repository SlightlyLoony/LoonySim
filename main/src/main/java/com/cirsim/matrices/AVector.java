package com.cirsim.matrices;

import com.cirsim.util.Numbers;

/**
 * Abstract base class for concrete <code>Vector</code> implementations.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class AVector {


    // defines 1/2 of the delta between between rotation distances in vectorHash()...
    // the precise number isn't significant so long as it's more than about 2 or 3 (from experimentation)...
    private static final int VECTOR_HASH_ROTATION_DELTA = 7;

    // caches the hash code; cache is invalidated by any mutation...
    protected boolean dirty = true;
    protected int hashCache;


    // the maximum number of ulps two doubles may differ by and still be considered equal...
    protected final int epsilon;


    protected AVector( final int _epsilon ) {
        epsilon = _epsilon;
    }


    /**
     * Returns true if this instance is equal to the given instance.  Note that the result of this method is independent of any particular
     * implementation of Vector.  Equivalent vectors with different implementations will compare as equal even if the implementation class is
     * different.
     *
     * @param _obj the object to compare for equality with this vector
     * @return true if the given object is equal to this vector
     */
    @Override
    public boolean equals( final Object _obj ) {

        if( this == _obj )
            return true;

        if( !(_obj instanceof Vector) )
            return false;

        Vector vect = (Vector) _obj;

        if( epsilon != vect.getEpsilon() )
            return false;

        if( vect.length() != length() )
            return false;

        if( hashCode() != vect.hashCode() )
            return false;

        int elementCount = 0;
        VectorIterator vi = vect.iterator( VectorIteratorOrderMode.UNSPECIFIED, VectorIteratorFilterMode.SPARSE );
        while( vi.hasNext() ) {
            elementCount++;
            vi.next();
            if( vi.value() != get( vi.index() ) )  // comparison is ok, as to be equal the values must be identical...
                return false;
        }

        return elementCount == nonZeroEntryCount();
    }


    /**
     * Returns the value at the given index (zero based) in this vector.  Throws an <code>IndexOutOfBoundsException</code> if the given index is less
     * than zero, or equal to or greater than the vector's length.
     *
     * @param _index the index of the value to get
     * @return the value of the entry at the given index
     */
    abstract public double get( final int _index );


    /**
     * Returns the number of nonzero (or not empty) entries in this vector.  In some implementations this operation may require traversing all the
     * entries in the vector to count the ones that are empty.
     *
     * @return the number of nonzero entries in this vector
     */
    abstract public int nonZeroEntryCount();


    /**
     * Returns the length of this vector, which is the same as the number of entries in the vector (including both empty or zero entries and set
     * or nonzero entries).
     *
     * @return the length of this vector
     */
    abstract public int length();


    /**
     * Computes a hash value for this vector that will be the same for any two equivalent vectors, independent of the vector implementation class.  To
     * accomplish this, the hash must depend on the nonzero entry values, their indices, and the value of epsilon.  To make computing the hash as
     * fast as possible for all implementation classes, the order in which entries are processed in computing the hash must not matter.  This allows
     * traversing the nonzero entries with unordered sparse iterator, which is guaranteed to be the fastest iterator for <i>every</i> vector
     * implementation class.
     * <p>
     * Two properties are important in this hash's algorithm.  First, the contribution of each value and its index to the hash must depend on the
     * properties of <i>both</i>.  This reduces the risk of hash collision in the case that two arrays with identical entries except that two of them
     * have swapped indices.  This property is made more challenging by the second property: the hash computation must be completely insensitive to
     * the order in which the values and their indices are processed.  The techniques most commonly seen in Java <code>hashCode()</code>
     * implementations fail in this regard, which is why we need something special here.
     *
     * @return this vector's hash code
     */
    @Override
    public int hashCode() {

        // if we have a cached value, use it...
        if( !dirty )
            return hashCache;

        hashCache = 0;
        VectorIterator vi = iterator( VectorIteratorOrderMode.UNSPECIFIED, VectorIteratorFilterMode.SPARSE );
        while( vi.hasNext() ) {
            vi.next();

            /*
             * This bit twiddling results in a value that depends on the COMBINATION of the value and the index.  The double value is converted to
             * a 64 bit pattern, which is then split into two 32 bit patterns.  Each of those is rotated by a number of bits dependent on the
             * index value, then the exclusive or of those two results and the index itself provides the end result.
             *
             * This code uses some fairly obscure Java features that may not be familiar to all programmers, so here's a bit of a "cheat sheet":
             * -- the "^" operator does a bit-wise exclusive-or of the two operands.
             * -- Integer.rotateLeft() can actually rotate either right or left, depending on the sign of the second argument.
             * -- the ">>" operator does a bit-wise right shift of the left operand by the number of bit positions in the right operand.
             */
            long eb = Double.doubleToRawLongBits( vi.value() );  // effectively casts the binary representation of a double to a long...
            hashCache ^= Integer.rotateLeft( (int)  eb,        vi.index() + VECTOR_HASH_ROTATION_DELTA );
            hashCache ^= Integer.rotateLeft( (int) (eb >> 32), vi.index() - VECTOR_HASH_ROTATION_DELTA );
            hashCache ^= vi.index();
        }
        hashCache = Numbers.hash( epsilon, hashCache );
        dirty = false;
        return hashCache;
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
    abstract public VectorIterator iterator( final VectorIteratorOrderMode _orderMode, final VectorIteratorFilterMode _filterMode );


    /**
     * Returns the value of epsilon used by this instance.  Epsilon is the amount that two values may differ when compared, and still be considered
     * equal.  It is a technique used to get around the inexact representation of numbers with double precision floating point; otherwise, many
     * comparisons <i>expected</i> to be equal would instead appear to be unequal.  The value of epsilon is expressed in ulps (Units in the Last
     * Place), which are the magnitude of the LSB of a floating point number.
     *
     * @return the value of epsilon for this vector
     */
    public int getEpsilon() {
        return epsilon;
    }
}

package com.cirsim.matrices;

import com.cirsim.util.Numbers;

/**
 * Static container class for constants and functions related to matrices (or vectors).
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class MatrixStuff {

    // Defines the value considered by vectors and matrices to be "empty"...
    public static final double PURE_ZERO = 0.0d;

    // Defines the default maximum number of ulps that two double values may be and still be considered equal within the matrices package...
    public static final int DEFAULT_EPSILON = 10;


    // defines 1/2 of the delta between between rotation distances in vectorHash()...
    // the precise number isn't significant so long as it's more than about 2 or 3 (from experimentation)...
    private static final int VECTOR_HASH_ROTATION_DELTA = 17;

    /**
     * Computes a hash value for a vector that will be the same for any two equivalent vectors, independent of the vector implementation class.  To
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
     * @param _vector the vector whose hash code is being computed
     * @return the given vector's hash code
     */
    public static int vectorHash( final Vector _vector ) {

        int result = 0;
        VectorIterator vi = _vector.iterator( VectorIteratorOrderMode.UNSPECIFIED, VectorIteratorFilterMode.SPARSE );
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
            result ^= Integer.rotateLeft( (int)  eb,        vi.index() + VECTOR_HASH_ROTATION_DELTA );
            result ^= Integer.rotateLeft( (int) (eb >> 32), vi.index() - VECTOR_HASH_ROTATION_DELTA );
            result ^= vi.index();
        }
        result = Numbers.hash( _vector.getEpsilon(), result );
        return result;
    }
}

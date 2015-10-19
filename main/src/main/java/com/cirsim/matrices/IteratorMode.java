package com.cirsim.matrices;

/**
 * Controls the possible modes for iterators over vectors or matrices.
 *
 * An ordered iterator returns entries in index order (for vectors) or row, column order (for matrices).  Ordering the elements obtained from an
 * iterator can be slow, depending on the implementation.  An unordered iterator returns entries in whatever order is fastest for the implementation,
 * which <i>may</i> be ordered.
 *
 * A dense iterator returns all the elements in the vector or matrix being iterated over.  In a sparsely populated vector or matrix, this may be slow
 * simply because of the number of elements being returned.  In some implementations, a dense iterator is even more costly, as the empty elements must
 * be synthesized on the fly.  A sparse iterator returns only those elements with a non-zero value (e.g., the non-empty elements).  In some
 * implementations, a sparse iterator is particularly fast.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public enum IteratorMode {

    UNORDERED_AND_DENSE,
    ORDERED_AND_DENSE,
    UNORDERED_AND_SPARSE,
    ORDERED_AND_SPARSE;
}

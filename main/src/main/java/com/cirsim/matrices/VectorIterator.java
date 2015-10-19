package com.cirsim.matrices;

/**
 * Implemented by iterators over vectors.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface VectorIterator {

    boolean hasNext();

    double next();

    int index();

    boolean isOrdered();

    boolean isSparse();

    int elements();
}

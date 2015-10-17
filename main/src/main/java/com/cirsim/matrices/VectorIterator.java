package com.cirsim.matrices;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface VectorIterator {

    boolean hasNext();

    double next();

    int index();
}

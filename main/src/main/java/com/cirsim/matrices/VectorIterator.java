package com.cirsim.matrices;

/**
 * Implemented by iterators over vectors.  Note that the semantics of this iterator are slightly different than that of Java's standard iterators.
 * In particular, the <code>next()</code> invocation does not return any value.  Instead, there are two getters (<code>value()</code> and
 * <code>index()</code> that can separately retrieve the results.  We chose this approach to avoid creating (and later destroying) a new object
 * on every iteration just to return the two values.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface VectorIterator {


    /**
     * Returns true if and only if this iterator has another entry to return.
     *
     * @return true if this iterator has another entry
     */
    boolean hasNext();


    /**
     * Advances to the next entry.  After invoking this method, the {@link #value()} and {@link #index()} methods will return the values of that
     * entry.
     */
    void next();


    /**
     * Returns the value of the entry most recently advanced to through an invocation of {@link #next()}.
     *
     * @return the value of the current iterator entry
     */
    double value();


    /**
     * Returns the index of the entry most recently advanced to through an invocation of {@link #next()}.
     *
     * @return the index of the current iterator entry
     */
    int index();


    /**
     * Returns the order mode for this iterator.
     *
     * @return this iterator's order mode
     */
    VectorIteratorOrderMode getOrderMode();


    /**
     * Returns the filter mode for this iterator.
     *
     * @return this iterator's filter mode
     */
    VectorIteratorFilterMode getFilterMode();


    /**
     * Returns the count of entries that will be returned by this iterator.  For unfiltered iterators, this is always equal to the length of the
     * vector, and for sparse filtered iterators, it is equal to the number of set (nonzero) entries.  Note that this value is not the
     * <i>remaining</i> entries, but rather the total that will be returned; this value does not change during iteration.
     *
     * @return the count of entries that will be returned by this iterator.
     */
    int entryCount();
}

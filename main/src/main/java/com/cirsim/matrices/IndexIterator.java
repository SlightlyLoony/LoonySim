package com.cirsim.matrices;

/**
 * Implemented by iterators over indices.  Note that the semantics of this iterator are slightly different than that of Java's standard iterators.
 * In particular, the <code>next()</code> invocation does not return any value.  Instead, there are two getters (<code>value()</code> and
 * <code>key()</code> that can separately retrieve the results.  We chose this approach to avoid creating (and later destroying) a new object
 * on every iteration just to return the two values.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface IndexIterator {


    /**
     * Returns true if and only if this iterator has another entry to return.
     *
     * @return true if this iterator has another entry
     */
    boolean hasNext();


    /**
     * Advances to the next entry in key order.  After invoking this method, the {@link #value()} and {@link #key()} methods will return the values
     * of that entry.
     */
    void next();


    /**
     * Returns the value of the entry most recently advanced to through an invocation of {@link #next()}.
     *
     * @return the value of the current iterator entry
     */
    int value();


    /**
     * Returns the key of the entry most recently advanced to through an invocation of {@link #next()}.
     *
     * @return the key of the current iterator entry
     */
    int key();


    /**
     * Returns the count of entries that will be returned by this iterator.  This value does not change during iteration.
     *
     * @return the count of entries that will be returned by this iterator.
     */
    int entryCount();
}

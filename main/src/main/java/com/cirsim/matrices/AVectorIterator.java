package com.cirsim.matrices;

/**
 * Base class for <code>VectorIterator</code> implementations.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class AVectorIterator {


    protected final VectorIteratorOrderMode orderMode;
    protected final VectorIteratorFilterMode filterMode;
    protected int index;
    protected int indexInternal;
    protected double value;


    protected AVectorIterator( final VectorIteratorOrderMode _orderMode, final VectorIteratorFilterMode _filterMode ) {
        orderMode = _orderMode;
        filterMode = _filterMode;
    }


    /**
     * Returns true if and only if this iterator has another entry to return.
     *
     * @return true if this iterator has another entry
     */
    abstract public boolean hasNext();


    /**
     * Advances to the next entry.  After invoking this method, the {@link #value()} and {@link #index()} methods will return the values of that
     * entry.
     */
    abstract public void next();


    /**
     * Returns the count of entries that will be returned by this iterator.  For unfiltered iterators, this is always equal to the length of the
     * vector, and for sparse filtered iterators, it is equal to the number of set (nonzero) entries.  Note that this value is not the
     * <i>remaining</i> entries, but rather the total that will be returned; this value does not change during iteration.
     *
     * @return the count of entries that will be returned by this iterator.
     */
    abstract public int entryCount();


    /**
     * Returns the value of the entry most recently advanced to through an invocation of {@link #next()}.
     *
     * @return the value of the current iterator entry
     */
    public double value() {
        return value;
    }


    /**
     * Returns the index of the entry most recently advanced to through an invocation of {@link #next()}.
     *
     * @return the index of the current iterator entry
     */
    public int index() {
        return index;
    }


    /**
     * Returns the order mode for this iterator.
     *
     * @return this iterator's order mode
     */
    public VectorIteratorOrderMode getOrderMode() {
        return orderMode;
    }


    /**
     * Returns the filter mode for this iterator.
     *
     * @return this iterator's filter mode
     */
    public VectorIteratorFilterMode getFilterMode() {
        return filterMode;
    }
}



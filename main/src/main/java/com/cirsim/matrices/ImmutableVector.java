package com.cirsim.matrices;

/**
 * Instances of this class wrap concrete <code>Vector</code> instances to disable any mutator methods, thus making the wrapped instance effectively
 * immutable.  Any attempt to use a mutator method results in a <code>UnsupportedOperationException</code> being thrown.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ImmutableVector implements Vector {


    // the wrapped mutable Vector instance...
    private Vector vector;


    /**
     * Creates a new instance of this class that wraps the given <code>Vector</code> to enforce immutability.
     *
     * @param _vector the Vector to wrap in immutability
     */
    public ImmutableVector( final Vector _vector ) {

        if( _vector == null )
            throw new IllegalArgumentException( "Vector is missing" );

        vector = _vector;
    }


    /**
     * Adds the given vector to this vector, entry by entry, returning the sum in a new vector.  The vector implementation class of the result is
     * the same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a different length
     * than this instance.
     *
     * @param _vector the vector to add to this vector.
     * @return a new vector containing the entry-by-entry sum of this instance and the given vector.
     */
    @Override
    public Vector add( final Vector _vector ) {
        return vector.add( _vector );
    }


    /**
     * Subtracts the given vector from this vector, entry by entry, returning the difference in a new vector.  The vector implementation class of
     * the result is the same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a
     * different length than this instance.
     *
     * @param _vector the vector to subtract from this vector.
     * @return a new vector containing the entry-by-entry difference of this instance and the given vector.
     */
    @Override
    public Vector subtract( final Vector _vector ) {
        return vector.subtract( _vector );
    }


    /**
     * Adds the given multiple of the given vector to this vector, entry by entry, returning the sum in a new vector.  The vector implementation
     * class of the result is the same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is
     * a different length than this instance.
     *
     * @param _vector     the vector to add a multiple of to this vector.
     * @param _multiplier the multiplier
     * @return a new vector containing the entry-by-entry sum of this instance and the given multiple of the given vector.
     */
    @Override
    public Vector addMultiple( final Vector _vector, final double _multiplier ) {
        return vector.addMultiple( _vector, _multiplier );
    }


    /**
     * Returns the value at the given index (zero based) in this vector.  Throws an <code>IndexOutOfBoundsException</code> if the given index is less
     * than zero, or equal to or greater than the vector's length.
     *
     * @param _index the index of the value to get
     * @return the value of the entry at the given index
     */
    @Override
    public double get( final int _index ) {
        return vector.get( _index );
    }


    /**
     * Sets the value at the given index (zero based) in this vector to the given value.  Throws an <code>IndexOutOfBoundsException</code> if the
     * given index is less than zero, or equal to or greater than the vector's length.
     *
     * @param _index the index of the value to set
     * @param _value the value to set at the given index
     */
    @Override
    public void set( final int _index, final double _value ) {
        throw new UnsupportedOperationException( "ImmutableVector does not support set()" );
    }


    /**
     * Sets the value of all entries of this vector to the given value.
     *
     * @param _value the value to set all entries to
     */
    @Override
    public void set( final double _value ) {
        throw new UnsupportedOperationException( "ImmutableVector does not support set()" );
    }


    /**
     * Returns the value of epsilon used by this instance.  Epsilon is the amount that two values may differ when compared, and still be considered
     * equal.  It is a technique used to get around the inexact representation of numbers with double precision floating point; otherwise, many
     * comparisons <i>expected</i> to be equal would instead appear to be unequal.  The value of epsilon is expressed in ulps (Units in the Last
     * Place), which are the magnitude of the LSB of a floating point number.
     *
     * @return the value of epsilon for this vector
     */
    @Override
    public int getEpsilon() {
        return vector.getEpsilon();
    }


    /**
     * Returns the length of this vector, which is the same as the number of entries in the vector (including both empty or zero entries and set or
     * nonzero entries).
     *
     * @return the length of this vector
     */
    @Override
    public int length() {
        return vector.length();
    }


    /**
     * Returns the number of nonzero (or not empty) entries in this vector.  In some implementations this operation may require traversing all the
     * entries in the vector to count the ones that are empty.
     *
     * @return the number of nonzero entries in this vector
     */
    @Override
    public int nonZeroEntryCount() {
        return vector.nonZeroEntryCount();
    }


    /**
     * Returns true if and only if the given index is valid for this vector, which means that it is not less than zero and not greater than or equal
     * to the length of this vector.
     *
     * @param _index the index to validate
     * @return true if the given index is valid
     */
    @Override
    public boolean isValidIndex( final int _index ) {
        return vector.isValidIndex( _index );
    }


    /**
     * Returns true if and only if the given length is equal to the length of this vector.
     *
     * @param _length the length to check
     * @return true if the given length is the same as the length of this vector
     */
    @Override
    public boolean isSameLength( final int _length ) {
        return vector.isSameLength( _length );
    }


    /**
     * Returns true if and only if the given vector is non-null and is the same length as this vector.
     *
     * @param _vector the vector to check the length of
     * @return true if the given vector is non-null and is the same length as this vector
     */
    @Override
    public boolean isSameLength( final Vector _vector ) {
        return vector.isSameLength( _vector );
    }


    /**
     * Returns a deep copy of this vector, using the same implementation class as this vector's.  The copy will contain no instances of shared
     * objects.
     *
     * @return a new vector that is a deep copy of this vector
     */
    @Override
    public Vector deepCopy() {
        return vector.deepCopy();
    }


    /**
     * Returns a new vector whose entry values are this vector's entry values multiplied by the given multiplier, entry-by-entry.  The vector
     * implementation class of the result is the same as that of this instance.  In other words, <code>X[n] = T[n] * m</code>, where <code>X</code> is
     * the returned vector, <code>T</code> is this vector, <code>m</code> is the given multiplier, and <code>n</code> is the set of all index values
     * <code>0 .. T.length - 1</code>.
     *
     * @param _multiplier the multiplier
     * @return a new vector that is the multiple of this vector, using the given multiplier
     */
    @Override
    public Vector multiply( final double _multiplier ) {
        return vector.multiply( _multiplier );
    }


    /**
     * Returns a vector that is a contiguous subvector of this vector.  The given start index must be a valid index for this vector, and the value at
     * the start index will be the first value in the returned vector.  The given end index must be in the range of <code>t .. l</code>, where
     * <code>t</code> is the start index + 1, and <code>l</code> is the length of this vector.  The length of the returned vector is equal to start -
     * end.  Throws an <code>IndexOutOfBoundsException</code> if either the given start or end indices are out of bounds.
     *
     * @param _start the start index within this vector for the returned vector
     * @param _end   the end index within this vector for the returned vector
     * @return the subvector
     */
    @Override
    public Vector subVector( final int _start, final int _end ) {
        return vector.subVector( _start, _end );
    }


    /**
     * Returns an ordinary array containing the values of all the entries of this instance (both zero or empty values and nonzero or set values). The
     * size of the array is equal to the length of this vector.
     *
     * @return the array containing all the values of this vector
     */
    @Override
    public double[] toArray() {
        return vector.toArray();
    }


    /**
     * Returns a <code>JAVector</code> instance that is exactly equivalent to this vector.  If this vector <i>is</i> an instance of
     * <code>JAVector</code>, then this vector is simply returned.  Otherwise a new instance of <code>JAVector</code> is created that is a copy of
     * this vector.  The resulting vector will compare with this vector as equal using the <code>equals()</code> method on either instance, and the
     * result of <code>hashCode()</code> for each will be the same.
     *
     * @return a JAVector equivalent to this vector
     */
    @Override
    public JAVector toJAVector() {
        return vector.toJAVector();
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
    @Override
    public VectorIterator iterator( final VectorIteratorOrderMode _orderMode, final VectorIteratorFilterMode _filterMode ) {
        return vector.iterator( _orderMode, _filterMode );
    }
}

package com.cirsim.matrices;

/**
 * Implemented by classes representing vectors (single dimensional arrays of real numbers).
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface Vector {


    /**
     * Adds the given vector to this vector, entry by entry, returning the sum in a new vector.  The vector implementation class of the result
     * is the same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a different length
     * than this instance.
     *
     * @param _vector the vector to add to this vector.
     * @return a new vector containing the entry-by-entry sum of this instance and the given vector.
     */
    Vector add( final Vector _vector );


    /**
     * Subtracts the given vector from this vector, entry by entry, returning the difference in a new vector.  The vector implementation class of
     * the result is the same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a
     * different length than this instance.
     *
     * @param _vector the vector to subtract from this vector.
     * @return a new vector containing the entry-by-entry difference of this instance and the given vector.
     */
    Vector subtract( final Vector _vector );


    /**
     * Adds the given multiple of the given vector to this vector, entry by entry, returning the sum in a new vector.  The vector implementation
     * class of the result is the same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is
     * a different length than this instance.
     *
     * @param _vector the vector to add a multiple of to this vector.
     * @param _multiplier the multiplier
     * @return a new vector containing the entry-by-entry sum of this instance and the given multiple of the given vector.
     */
    Vector addMultiple( final Vector _vector, final double _multiplier );


    /**
     * Returns the value at the given index (zero based) in this vector.  Throws an <code>IndexOutOfBoundsException</code> if the given index is less
     * than zero, or equal to or greater than the vector's length.
     *
     * @param _index the index of the value to get
     * @return the value of the entry at the given index
     */
    double get( final int _index );


    /**
     * Sets the value at the given index (zero based) in this vector to the given value.  Throws an <code>IndexOutOfBoundsException</code> if the
     * given index is less than zero, or equal to or greater than the vector's length.
     *
     * @param _index the index of the value to set
     * @param _value the value to set at the given index
     */
    void set( final int _index, final double _value );


    /**
     * Sets the value of all entries of this vector to the given value.
     *
     * @param _value the value to set all entries to
     */
    void set( final double _value );


    /**
     * Returns the value of epsilon used by this instance.  Epsilon is the amount that two values may differ when compared, and still be considered
     * equal.  It is a technique used to get around the inexact representation of numbers with double precision floating point; otherwise, many
     * comparisons <i>expected</i> to be equal would instead appear to be unequal.  The value of epsilon is expressed in ulps (Units in the Last
     * Place), which are the magnitude of the LSB of a floating point number.
     *
     * @return the value of epsilon for this vector
     */
    int getEpsilon();


    /**
     * Returns the length of this vector, which is the same as the number of entries in the vector (including both empty or zero entries and set
     * or nonzero entries).
     *
     * @return the length of this vector
     */
    int length();


    /**
     * Returns the number of nonzero (or not empty) entries in this vector.  In some implementations this operation may require traversing all the
     * entries in the vector to count the ones that are empty.
     *
     * @return the number of nonzero entries in this vector
     */
    int nonZeroEntryCount();


    /**
     * Returns true if and only if the given index is valid for this vector, which means that it is not less than zero and not greater than or equal
     * to the length of this vector.
     *
     * @param _index the index to validate
     * @return true if the given index is valid
     */
    boolean isValidIndex( final int _index );


    /**
     * Returns true if and only if the given length is equal to the length of this vector.
     *
     * @param _length the length to check
     * @return true if the given length is the same as the length of this vector
     */
    boolean isSameLength( final int _length );


    /**
     * Returns true if and only if the given vector is non-null and is the same length as this vector.
     *
     * @param _vector the vector to check the length of
     * @return true if the given vector is non-null and is the same length as this vector
     */
    boolean isSameLength( final Vector _vector );


    /**
     * Returns a deep copy of this vector, using the same implementation class as this vector's.  The copy will contain no instances of shared
     * objects.
     *
     * @return a new vector that is a deep copy of this vector
     */
    Vector deepCopy();


    /**
     * Returns a new vector whose entry values are this vector's entry values multiplied by the given multiplier, entry-by-entry.  The vector
     * implementation class of the result is the same as that of this instance.  In other words, <code>X[n] = T[n] * m</code>, where <code>X</code> is
     * the returned vector, <code>T</code> is this vector, <code>m</code> is the given multiplier, and <code>n</code> is the set of all index values
     * <code>0 .. T.length - 1</code>.
     *
     * @param _multiplier the multiplier
     * @return a new vector that is the multiple of this vector, using the given multiplier
     */
    Vector multiply( final double _multiplier );


    /**
     * Returns a vector that is a contiguous subvector of this vector.  The given start index must be a valid index for this vector, and the value
     * at the start index will be the first value in the returned vector.  The given end index must be in the range of <code>t .. l</code>, where
     * <code>t</code> is the start index + 1, and <code>l</code> is the length of this vector.  The length of the returned vector is equal to start -
     * end.  Throws an <code>IndexOutOfBoundsException</code> if either the given start or end indices are out of bounds.
     *
     * @param _start the start index within this vector for the returned vector
     * @param _end the end index within this vector for the returned vector
     * @return the subvector
     */
    Vector subVector( final int _start, final int _end );


    /**
     * Returns an ordinary array containing the values of all the entries of this instance (both zero or empty values and nonzero or set values).
     * The size of the array is equal to the length of this vector.
     *
     * @return the array containing all the values of this vector
     */
    double[] toArray();


    /**
     * Returns a <code>ArrayVector</code> instance that is exactly equivalent to this vector.  If this vector <i>is</i> an instance of
     * <code>ArrayVector</code>, then this vector is simply returned.  Otherwise a new instance of <code>ArrayVector</code> is created that is a copy of
     * this vector.  The resulting vector will compare with this vector as equal using the <code>equals()</code> method on either instance, and the
     * result of <code>hashCode()</code> for each will be the same.
     *
     * @return a ArrayVector equivalent to this vector
     */
    ArrayVector toArrayVector();


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
    VectorIterator iterator( final VectorIteratorOrderMode _orderMode, final VectorIteratorFilterMode _filterMode );


    /**
     * Returns true if this instance is equal to the given instance.  Note that the result of this method is independent of any particular
     * implementation of Vector.  Equivalent vectors (same entry:index tuples and epsilon) with different implementation classes will still compare
     * as equal.
     *
     * @param _obj the object to compare for equality with this vector
     * @return true if the given object is equal to this vector
     */
    boolean equals( final Object _obj );


    /**
     * Returns the hash code for this instance.  Note that the hash code is identical for all implementations of Vector, and is based on all non-zero
     * entries, their indices, and the epsilon.  Other implementation-dependent properties are <i>not</i> included in the hash code.
     *
     * @return the hash code for this vector.
     */
    int hashCode();
}



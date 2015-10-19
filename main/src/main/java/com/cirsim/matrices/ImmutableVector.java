package com.cirsim.matrices;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ImmutableVector implements Vector {


    private Vector vector;


    public ImmutableVector( final Vector _vector ) {

        if( _vector == null )
            throw new IllegalArgumentException( "Vector is missing" );

        vector = _vector;
    }


    /**
     * Adds the given vector to this vector, element by element, returning the sum in a new vector.  The vector implementation class of the result is
     * the same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a different length
     * than this instance.
     *
     * @param _vector the vector to add to this vector.
     * @return a new vector containing the element-by-element sum of this instance and the given vector.
     */
    @Override
    public Vector add( final Vector _vector ) {
        return null;
    }


    /**
     * Subtracts the given vector from this vector, element by element, returning the difference in a new vector.  The vector implementation class of
     * the result is the same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is a
     * different length than this instance.
     *
     * @param _vector the vector to subtract from this vector.
     * @return a new vector containing the element-by-element difference of this instance and the given vector.
     */
    @Override
    public Vector subtract( final Vector _vector ) {
        return null;
    }


    /**
     * Adds the given multiple of the given vector to this vector, element by element, returning the sum in a new vector.  The vector implementation
     * class of the result is the same as that of this instance.  Throws an <code>IllegalArgumentException</code> if the given vector is missing or is
     * a different length than this instance.
     *
     * @param _vector     the vector to add a multiple of to this vector.
     * @param _multiplier
     * @return a new vector containing the element-by-element sum of this instance and the given multiple of the given vector.
     */
    @Override
    public Vector addMultiple( final Vector _vector, final double _multiplier ) {
        return null;
    }


    /**
     * Returns the value at the given index (zero based) in this vector.  Throws an <code>IndexOutOfBoundsException</code> if the given index is less
     * than zero, or equal to or greater than the vector's length.
     *
     * @param _index the index of the value to get
     * @return the value of the element at the given index
     */
    @Override
    public double get( final int _index ) {
        return 0;
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

    }


    /**
     * Sets the value of all elements of this vector to the given value.
     *
     * @param _value the value to set all elements to
     */
    @Override
    public void set( final double _value ) {

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
        return 0;
    }


    /**
     * Returns the length of this vector, which is the same as the number of elements in the vector (including both empty or zero elements and set or
     * nonzero elements).
     *
     * @return the length of this vector
     */
    @Override
    public int length() {
        return 0;
    }


    /**
     * Returns the number of nonzero (or not empty) elements in this vector.  In some implementations this operation may require traversing all the
     * elements in the vector to count the ones that are empty.
     *
     * @return the number of nonzero elements in this vector
     */
    @Override
    public int nonZeroElementCount() {
        return 0;
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
        return false;
    }


    /**
     * Returns true if and only if the given length is equal to the length of this vector.
     *
     * @param _length the length to check
     * @return true if the given length is the same as the length of this vector
     */
    @Override
    public boolean isSameLength( final int _length ) {
        return false;
    }


    /**
     * Returns true if and only if the given vector is non-null and is the same length as this vector.
     *
     * @param _vector the vector to check the length of
     * @return true if the given vector is non-null and is the same length as this vector
     */
    @Override
    public boolean isSameLength( final Vector _vector ) {
        return false;
    }


    /**
     * Returns a deep copy of this vector, using the same implementation class as this vector's.  The copy will contain no instances of shared
     * objects.
     *
     * @return a new vector that is a deep copy of this vector
     */
    @Override
    public Vector deepCopy() {
        return null;
    }


    /**
     * Returns a new vector whose element values are this vector's element values multiplied by the given multiplier, element-by-element.  The vector
     * implementation class of the result is the same as that of this instance.  In other words, <code>X[n] = T[n] * m</code>, where <code>X</code> is
     * the returned vector, <code>T</code> is this vector, <code>m</code> is the given multiplier, and <code>n</code> is the set of all index values
     * <code>0 .. T.length - 1</code>.
     *
     * @param _multiplier the multiplier
     * @return a new vector that is the multiple of this vector, using the given multiplier
     */
    @Override
    public Vector multiply( final double _multiplier ) {
        return null;
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
        return null;
    }


    /**
     * Returns an ordinary array containing the values of all the elements of this instance (both zero or empty values and nonzero or set values). The
     * size of the array is equal to the length of this vector.
     *
     * @return the array containing all the values of this vector
     */
    @Override
    public double[] toArray() {
        return new double[0];
    }


    /**
     * Returns a <code>JaVector</code> instance that is exactly equivalent to this vector.  If this vector <i>is</i> an instance of
     * <code>JaVector</code>, then this vector is simply returned.  Otherwise a new instance of <code>JaVector</code> is created that is a copy of
     * this vector.  The resulting vector will compare with this vector as equal using the <code>equals()</code> method on either instance, and the
     * result of <code>hashCode()</code> for each will be the same.
     *
     * @return a JaVector equivalent to this vector
     */
    @Override
    public JaVector toJaVector() {
        return null;
    }


    /**
     * Returns a vector iterator over this vector's elements in the given mode.  There are two aspects to the mode, each of which has two
     * possibilities - so there are four possible combinations of modes. <ul> <li><b>Ordered or unordered:</b> an ordered iterator returns values in
     * index order (though if the iterator is also sparse, some indices may be skipped).  An unordered iterator returns values in an unspecified
     * order, not necessarily predictable, and possibly the same as when ordered.</li> <li><b>Dense or sparse:</b> A dense iterator iterates over
     * <i>all</i> elements in the vector, whether set (nonzero) or empty (zero).  a sparse iterator iterates over <i>only</i> the set (nonzero)
     * elements.</li> </ul>
     *
     * @param _iteratorMode the mode for this iterator: ordered or unordered, dense or sparse
     * @return the iterator over this vector's elements in the given mode
     */
    @Override
    public VectorIterator iterator( final IteratorMode _iteratorMode ) {
        return null;
    }
}

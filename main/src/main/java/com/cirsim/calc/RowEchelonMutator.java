package com.cirsim.calc;

/**
 * Instances of this class hold a matrix that represents a system of linear equations that will be solved through Gaussian-Jordan elimination.  This
 * matrix will be mutated through row-swapping as needed: first to ensure non-zero values in all the pivots, and additionally as needed to prevent
 * unintentional eliminations of pivots.  For instance, given this matrix as input (which has only one non-zero pivot):<br/>
 *     <br/>
 *     1: 0 a 0 b<br/>
 *     2: 0 0 0 c<br/>
 *     3: d e f 0<br/>
 *     4: g 0 h 0<br/>
 *     <br/>
 * one could reorder the rows as below to get a non-zero value into each pivot position:<br/>
 *     <br/>
 *     3: d e f 0<br/>
 *     1: 0 a 0 b<br/>
 *     4: g 0 h 0<br/>
 *     2: 0 0 0 c<br/>
 *     <br/>
 * This row-reordered matrix is now a <i>candidate</i> for the first phase of the Gaussian-Jordan elimination process, putting the matrix in row
 * echelon form.  Note that there may be more than one row permutation that produces non-zero pivots.  In fact, there may be many.  Some of those
 * permutations produce a row echelon form.  For example, in the row-reordered matrix above, the process of eliminating 'g' might <i>also</i>
 * eliminate 'h' (his would happen if g/d = h/f).  When this unintentional elimination is detected, the only cure is to try another permutation of
 * row order to fix it.  For example, this order could be tried (continuing the example):<br/>
 *     <br/>
 *     4: g 0 h 0<br/>
 *     1: 0 a 0 b<br/>
 *     3: d e f 0<br/>
 *     2: 0 0 0 c<br/>
 *     <br/>
 * This new row-reordered matrix still has non-zero pivots, but it won't work, as if g/d = f/h, then of course d/g = h/f, and 'f' will be zeroed
 * while trying to eliminate 'd'.  Since this matrix has a value in the third column <i>only</i> in rows 3 and 4, there is no way to get it in row
 * echelon order, and therefore the system of linear equations the matrix represents has no solution.  This occurs because in this case rows 3 and 4
 * are actually equivalent, and there is missing information about some variable as a result.<br/>
 * <br/>
 * In other cases, however, there may be a solution via another permutation.  Consider this different matrix<br/>
 *     <br/>
 *     1: 0 a b 0<br/>
 *     2: 0 0 0 c<br/>
 *     3: d e f 0<br/>
 *     4: g 0 0 h<br/>
 *     <br/>
 * An initial permutation with non-zero pivots might be this:<br/>
 *     <br/>
 *     4: g 0 0 h<br/>
 *     3: d e f 0<br/>
 *     1: 0 a b j<br/>
 *     2: 0 0 i c<br/>
 *     <br/>
 * In this case, if e/a=f/b, then when eliminating 'a', 'b' will also be eliminated.  This time, however, there's another permutation that may avoid
 * the problem:<br/>
 *     <br/>
 *     4: g 0 0 h<br/>
 *     3: d e f 0<br/>
 *     2: 0 0 i c<br/>
 *     1: 0 a b j<br/>
 *     <br/>
 * Now row 2 introduces new values, including an already eliminated value where 'a' used to be.  While a solution is not guaranteed, unless we are
 * extraordinarily unfortunate, it should work.  This class provides simple means for the Gaussian-Jordan solver to obtain new permutations of the
 * original matrix, while tracking all the row swaps so that that results can be transformed into the row order given initially.<br/>
 * <br/>
 * The more zero values appear below and to the left of the diagonal of pivots, the less chance there is of having an accidental elimination of a
 * pivot value.  Because the work required to discover an accidentally eliminated pivot is quite large (especially in a system involving hundreds or
 * thousands of linear equations), we judged it worthwhile to make the initial permutation be the one with the most zeroes in that area.  It can
 * <i>still</i> cause an accidental elimination (unless <i>every</i> such value is zero), but it reduces the likelihood of it.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class RowEchelonMutator {

    private final double[][] original;


    public RowEchelonMutator( double[][] _original ) {

        if( _original == null )
            throw new IllegalArgumentException( "Original matrix is missing." );

        if( _original.length > _original[0].length )
            throw new IllegalArgumentException( "Original matrix has more rows than columns" );

        original = _original;
    }
}

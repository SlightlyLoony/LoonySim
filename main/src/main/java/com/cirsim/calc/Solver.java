package com.cirsim.calc;

/**
 * This class uses Gaussian-Jordan elimination to solve systems of linear equations that represent an electronic circuit.  The equations may come
 * from either nodal analysis (using Kirchoff's current law) or mesh analysis (using Kirchoff's voltage law).  The equations are presented to this
 * class as an autmented matrix, with 'm' rows and 'm+1' columns.
 *
 * For nodal analysis, each row represents the current equation for a node, and each column the currents entering or leaving the node, expressed as
 * admittances associated with each node voltage.  One node (usually ground, the reference node) is omitted from the system of equations.
 *
 * For mesh analysis, each row represents the voltage equation for an independent loop, and each column the voltage drops around the loop, expressed
 * as impedances associated with each current present in the loop.
 *
 * The solver implemented by this class is actually a general-purpose solver for systems of linear equations, but was designed and optimized for the
 * purposes described above.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Solver {


}

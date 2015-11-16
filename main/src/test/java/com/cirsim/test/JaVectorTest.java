package com.cirsim.test;

import com.cirsim.matrices.ArrayVector;
import com.cirsim.matrices.Vector;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
//TODO: add assertions to all tests...
public class JaVectorTest extends TestCase {

    @Test
    public void testConstructors() throws Exception {
        ArrayVector v1 = new ArrayVector( 25, 100 );
        double[] a = new double[] {0,0,0,0,0,1,2,3,4,5,0,0,0,0,0,6,7,8,9,0,0,0,0,0,0};
        ArrayVector v2 = new ArrayVector( a, 100 );
        ArrayVector v3 = new ArrayVector( v2 );
        hashCode();
    }


    @Test
    public void testAdd() throws Exception {
        ArrayVector a = new ArrayVector( new double[] { 1.1, 0, 0, 1.2, 1.3 }, 10 );
        ArrayVector b = new ArrayVector( new double[] { 0, 0, 2.1, 2.2, 2.3}, 10 );
        Vector c = a.add( b );
        hashCode();
    }


    public void testSubtract() throws Exception {

    }


    public void testAddMultiple() throws Exception {

    }


    public void testGet() throws Exception {

    }


    public void testSet() throws Exception {

    }


    public void testSet1() throws Exception {

    }


    public void testGetEpsilon() throws Exception {

    }


    public void testLength() throws Exception {

    }


    public void testNonZeroElementCount() throws Exception {

    }


    public void testIsValidIndex() throws Exception {

    }


    public void testIsSameLength() throws Exception {

    }


    public void testIsSameLength1() throws Exception {

    }


    public void testDeepCopy() throws Exception {

    }


    public void testSubVector() throws Exception {

    }


    public void testToArray() throws Exception {

    }


    public void testToJaVector() throws Exception {

    }


    public void testIterator() throws Exception {

    }


    public void testEquals() throws Exception {
        ArrayVector jav1 = new ArrayVector( new double[] { 0.0, 1.1, 2.2, 3.3, 4.4 } );
        ArrayVector jav2 = new ArrayVector( new double[] { 0.0, 1.1, 2.2, 3.3, 4.4 } );
        assertTrue( jav1.equals( jav2 ) );
        hashCode();
    }


    public void testHashCode() throws Exception {
        ArrayVector jav1 = new ArrayVector( new double[] { 0.0, 1.1, 2.2, 3.3, 4.4 } );
        ArrayVector jav2 = new ArrayVector( new double[] { 0.0, 1.23, 2.34, 3.45, 4.56 } );
        ArrayVector jav3 = new ArrayVector( new double[] { 0.0, 2.34, 1.23, 3.45, 4.56 } );
        int hc2 = jav2.hashCode();
        int hc3 = jav3.hashCode();
        hashCode();
    }
}
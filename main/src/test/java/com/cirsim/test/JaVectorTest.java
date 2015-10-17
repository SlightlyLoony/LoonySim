package com.cirsim.test;

import com.cirsim.matrices.JaVector;
import com.cirsim.matrices.Vector;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class JaVectorTest extends TestCase {

    @Test
    public void testConstructors() throws Exception {
        JaVector v1 = new JaVector( 25, 100 );
        double[] a = new double[] {0,0,0,0,0,1,2,3,4,5,0,0,0,0,0,6,7,8,9,0,0,0,0,0,0};
        JaVector v2 = new JaVector( a, 100 );
        JaVector v3 = new JaVector( v2 );
        //TODO: add assertions to test these...
        hashCode();
    }


    @Test
    public void testAdd() throws Exception {
        JaVector a = new JaVector( new double[] { 1.1, 0, 0, 1.2, 1.3 }, 10 );
        JaVector b = new JaVector( new double[] { 0, 0, 2.1, 2.2, 2.3}, 10 );
        Vector c = a.add( b );
        //TODO: add assertions to test these...
        hashCode();
    }


    @Test
    public void testSubtract() throws Exception {

    }


    @Test
    public void testGet() throws Exception {

    }


    @Test
    public void testSet() throws Exception {

    }


    @Test
    public void testSet1() throws Exception {

    }


    @Test
    public void testGetMaxEqualsUlpDiff() throws Exception {

    }


    @Test
    public void testLength() throws Exception {

    }


    @Test
    public void testIsValidIndex() throws Exception {

    }


    @Test
    public void testIsSameLength() throws Exception {

    }


    @Test
    public void testIsSameLength1() throws Exception {

    }


    @Test
    public void testDeepCopy() throws Exception {

    }


    @Test
    public void testMultiply() throws Exception {
        JaVector a = new JaVector( new double[] { 1.1, 0, 0, 1.2, 1.3 }, 10 );
        Vector b = a.multiply( 0.5 );
        //TODO: add assertions to test these...
        hashCode();
    }


    @Test
    public void testSubVector() throws Exception {

    }


    @Test
    public void testToArray() throws Exception {

    }


    @Test
    public void testToJaVector() throws Exception {

    }


    @Test
    public void testIterator() throws Exception {

    }


    @Test
    public void testSparseIterator() throws Exception {

    }


    @Test
    public void testEquals() throws Exception {

    }


    @Test
    public void testHashCode() throws Exception {

    }
}
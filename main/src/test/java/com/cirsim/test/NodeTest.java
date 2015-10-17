package com.cirsim.test;

import com.cirsim.expressions.*;
import org.junit.Test;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class NodeTest {

    @Test
    public void SimpleTests() {

        Node n1 = new MultiplyOperator();
        Node n2 = new PlusOperator();
        Node n3 = new NegateOperator();
        Node n4 = new PowerOperator();
        n1.addChild( n3 );
        n3.addChild( n4 );
        n4.addChild( new Constant( 4 ) );
        n4.addChild( new Constant( 2.5 ) );
        n1.addChild( n2 );
        n2.addChild( new Constant( 3 ) );
        n2.addChild( new Constant( 2 ) );
        n2.addChild( new Constant( 1 ) );
        double a = n1.getValue();
        hashCode();
    }
}
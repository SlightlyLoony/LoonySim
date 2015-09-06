package com.slightlyloony.sim;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Test {

    public static void main( String[] args ) {

        Circuit c = new Circuit();
        Resistor r1 = new Resistor( c, 4700, 1.0 / 8, .05, "carbon film" );
        Resistor r2 = new Resistor( c, 4700, 1.0 / 8, .05, "carbon film" );
        Net n1 = new Net();
        n1.connect( r1.getTerminal( "a" ) );
        n1.connect( r2.getTerminal( "b" ) );

        int x = 0;
    }
}

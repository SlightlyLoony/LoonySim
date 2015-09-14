package com.slightlyloony.sim.specs;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ComponentSpec {

    protected String name;
    protected String terminals[][];


    public String getName() {
        return name;
    }


    public String[][] getTerminals() {
        return terminals;
    }
}

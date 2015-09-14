package com.slightlyloony.sim.specs;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ToleranceSpec {

    private double both;
    private double lower;
    private double upper;
    private boolean percent;


    public double getBoth() {
        return both;
    }


    public double getLower() {
        return lower;
    }


    public double getUpper() {
        return upper;
    }


    public boolean isPercent() {
        return percent;
    }
}

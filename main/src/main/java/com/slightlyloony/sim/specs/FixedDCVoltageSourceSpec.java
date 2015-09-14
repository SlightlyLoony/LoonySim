package com.slightlyloony.sim.specs;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class FixedDCVoltageSourceSpec extends ComponentSpec {


    private double value;
    private ToleranceSpec tolerance;


    public FixedDCVoltageSourceSpec() {
        terminals = new String[][] { { "plus" }, { "minus" } };
    }


    public double getValue() {
        return value;
    }


    public ToleranceSpec getToleranceSpec() {
        return tolerance;
    }
}

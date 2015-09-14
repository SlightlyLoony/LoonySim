package com.slightlyloony.sim.specs;

import com.slightlyloony.sim.components.Resistor;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ResistorSpec extends ComponentSpec {

    private double value;
    private ToleranceSpec tolerance;
    private double power_dissipation;
    private Resistor.Kind kind;


    public ResistorSpec() {
        terminals = new String[][] { { "1", "a" }, { "2", "b" } };
    }


    public double getValue() {
        return value;
    }


    public ToleranceSpec getToleranceSpec() {
        return tolerance;
    }


    public double getPower_dissipation() {
        return power_dissipation;
    }


    public Resistor.Kind getKind() {
        return kind;
    }
}

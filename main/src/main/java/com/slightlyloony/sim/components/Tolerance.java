package com.slightlyloony.sim.components;


/**
 * Encapsulates the notion of a tolerance on an electronic component.  For example, capacitors are generally specified as a nominal number of farads,
 * with a tolerance of +/- 20%, or sometimes an asymmetrical specification, wuch as +50%, -20%.  Occasionally a component's tolerance will be specfied
 * with a variance in its absolute value.  For instance, a precision 100 ohm resistor's tolerance might be specified as -0.25 ohms, +0.4 ohms.  All of
 * these variants are possible to specify with instances of this class.
 *
 * Instances of this class are immutable and threadsafe.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Tolerance {

    // the tolerance variances, either as absolute value or (more commonly) as percentage (1.0 = 1%)...
//    private final double plus;
//    private final double minus;
//
//    // the tolerance units, either ABSOLUTE or PERCENT...
//    private final ToleranceUnits units;



//    public double getHigh( final double _value ) {
//        return _value - ((units == ToleranceUnits.ABSOLUTE) ? _value + plus : _value * (1.0 + plus / 100.0));
//    }
//
//
//    public double getLow( final double _value ) {
//        return _value - ((units == ToleranceUnits.ABSOLUTE) ? _value - minus : _value * (1.0 - minus / 100.0));
//    }
//
//
//    public double getPlus() {
//        return plus;
//    }
//
//
//    public double getMinus() {
//        return minus;
//    }
//
//
//    public ToleranceUnits getUnits() {
//        return units;
//    }


    public enum ToleranceUnits { PERCENT, ABSOLUTE }
}

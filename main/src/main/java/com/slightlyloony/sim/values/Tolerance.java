package com.slightlyloony.sim.values;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Tolerance implements Value {


    private final double lowerPercent;
    private final double higherPercent;


    public Tolerance( final double _lowerPercent, final double _higherPercent ) {
        lowerPercent = _lowerPercent;
        higherPercent = _higherPercent;
    }


    /**
     * Creates instances from strings in any of these formats (where "n" or "m" is any well-formed number):<br>
     * n%<br>
     * +-n%<br>
     * +n-m%<br>
     * n-m%<br>
     * -n+m%<br>
     * Units are ignored.
     *
     * @param _spec
     * @param _unit
     * @return
     */
    @Override
    public Value getInstance( final String _spec, final Units _unit ) {
        return null;
    }


    @Override
    public Units getUnit() {
        return null;
    }
}

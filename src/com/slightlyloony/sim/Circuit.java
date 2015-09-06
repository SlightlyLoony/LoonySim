package com.slightlyloony.sim;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Circuit {

    private double tickSeconds;  // time tick interval in seconds


    public double getTickSeconds() {
        return tickSeconds;
    }


    public void setTickSeconds( final double _tickSeconds ) {
        tickSeconds = _tickSeconds;
    }
}

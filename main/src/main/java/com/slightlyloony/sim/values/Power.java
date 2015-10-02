package com.slightlyloony.sim.values;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Power extends RequiredUnit implements Value {


    public Power( final double _value ) {
        super( _value, Units.POWER );
    }


    @Override
    protected AScalar createInstance( final double _value, final Units _unit ) {
        return new Power( _value );
    }


    @Override
    public Power getInstance( final String _spec, final Units _unit ) {
        boolean unitRequired = true;
        return (Power) getInstance( _spec, _unit, unitRequired );
    }
}

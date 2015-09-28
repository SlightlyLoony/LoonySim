package com.slightlyloony.sim.values;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class RequiredUnit extends AScalar implements Value {


    public RequiredUnit( final double _value, final Units _unit ) {
        super( _value, _unit );
    }


    @Override
    protected AScalar createInstance( final double _value, final Units _unit ) {
        return new RequiredUnit( _value, _unit );
    }


    @Override
    public RequiredUnit getInstance( final String _spec, final Units _unit ) {
        boolean unitRequired = true;
        return (RequiredUnit) getInstance( _spec, _unit, unitRequired );
    }
}

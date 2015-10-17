package com.cirsim.values;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class AssumedUnit extends AScalar implements Value {


    public AssumedUnit( final double _value, final Units _unit ) {
        super( _value, _unit );
    }


    @Override
    protected AScalar createInstance( final double _value, final Units _unit ) {
        return new AssumedUnit( _value, _unit );
    }


    @Override
    public AssumedUnit getInstance( final String _spec, final Units _unit ) {
        boolean unitRequired = false;
        return (AssumedUnit) getInstance( _spec, _unit, unitRequired );
    }
}

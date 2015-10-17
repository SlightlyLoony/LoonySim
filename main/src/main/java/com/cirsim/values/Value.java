package com.cirsim.values;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface Value {


    Value getInstance( final String _spec, final Units _unit );

    Units getUnit();
}

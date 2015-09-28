package com.slightlyloony.sim.values;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ResistorType implements Value {


    private final ResistorTechnology type;


    public ResistorType( final ResistorTechnology _type ) {
        type = _type;
    }


    @Override
    public Value getInstance( final String _spec, final Units _unit ) {
        return null;
    }


    @Override
    public Units getUnit() {
        return null;
    }
}

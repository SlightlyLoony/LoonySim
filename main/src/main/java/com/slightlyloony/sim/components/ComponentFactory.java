package com.slightlyloony.sim.components;

import com.slightlyloony.sim.specs.ComponentSpec;
import com.slightlyloony.sim.specs.FixedDCVoltageSourceSpec;
import com.slightlyloony.sim.specs.ResistorSpec;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ComponentFactory {

    public static Component create( final ComponentSpec _spec ) {

        if( _spec instanceof ResistorSpec )
            return new Resistor( (ResistorSpec) _spec );
        else if( _spec instanceof FixedDCVoltageSourceSpec )
            return new FixedDCVoltageSource( (FixedDCVoltageSourceSpec) _spec );
        else
            return null;
    }
}

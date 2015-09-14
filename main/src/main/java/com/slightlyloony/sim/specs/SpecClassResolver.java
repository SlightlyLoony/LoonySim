package com.slightlyloony.sim.specs;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class SpecClassResolver {


    // strings used to identify spec classes in JSON...
    public static final String RESISTOR                = "resistor";
    public static final String FIXED_DC_VOLTAGE_SOURCE = "fixed_dc_voltage_source";


    // tuples defining the relationship between an identifier string and a specification class...
    private static SpecID tuples[] = new SpecID[]{
            new SpecID( RESISTOR,                ResistorSpec.class         ),
            new SpecID( FIXED_DC_VOLTAGE_SOURCE, FixedDCVoltageSourceSpec.class )
    };


    // map to allow fast resolution of the specification class from the identifier string...
    private static final Map<String, Class<? extends ComponentSpec>> resolverMap = initializeResolverMap();


    private static Map<String, Class<? extends ComponentSpec>> initializeResolverMap() {
        Map<String, Class<? extends ComponentSpec>> result = new HashMap<>();
        for( SpecID tuple: tuples ) {
            result.put( tuple.id, tuple.clazz );
        }
        return result;
    }


    public static Class<? extends ComponentSpec> resolve( final String _identifierString ) {
        return resolverMap.get( _identifierString );
    }


    public static boolean has( final String _identifierString ) {
        return resolverMap.containsKey( _identifierString );
    }


    private static class SpecID {
        private final String id;
        private final Class<? extends ComponentSpec> clazz;


        public SpecID( final String _id, final Class<? extends ComponentSpec> _clazz ) {
            id = _id;
            clazz = _clazz;
        }
    }
}

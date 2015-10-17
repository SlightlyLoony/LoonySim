package com.cirsim.values;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public enum ResistorTechnology {

    CARBON_FILM( "carbon film" ),
    THICK_FILM( "thick film" ),
    THIN_FILM( "thin film" ),
    METAL_FILM( "metal film" ),
    METAL_OXIDE_FILM( "metal oxide film" ),
    WIRE_WOUND( "wire wound" ),
    FOIL( "foil" ),
    OTHER( "other" ),
    UNSPECIFIED( "unspecified" );


    private static Map<String,ResistorTechnology> types;


    private final String techName;


    ResistorTechnology( String _techName ) {
        techName = _techName;
        putName( _techName.toUpperCase(), this );
    }


    private static void putName( final String _techName, final ResistorTechnology _instance ) {
        if( types == null )
            types = Maps.newHashMap();
        types.put( _techName.toUpperCase(), _instance );
    }


    public static ResistorTechnology get( final String _techName ) {
        return types.get( _techName );
    }


    public String getTechologyName() {
        return techName;
    }
}

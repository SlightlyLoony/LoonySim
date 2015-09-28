package com.slightlyloony.sim.values;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public enum ResistorTechnology {

    CARBON_FILM( "carbon film" ),
    THICK_FILM( "thick film" ),
    THIN_FILM( "thin_film" ),
    METAL_FILM( "metal film" ),
    METAL_OXIDE_FILM( "metal oxide_film" ),
    WIRE_WOUND( "wire wound" ),
    FOIL( "foil" ),
    OTHER( "other" ),
    UNSPECIFIED( "unspecified" );

    private final String techName;


    ResistorTechnology( String _techName ) {
        techName = _techName;
    }


    public String getTechologyName() {
        return techName;
    }
}

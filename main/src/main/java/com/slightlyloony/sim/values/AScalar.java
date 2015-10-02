package com.slightlyloony.sim.values;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class AScalar implements Value {


    protected static final Pattern NUM_SUFFIX = Pattern.compile( "(\\d+(?:\\.\\d*)?)(.*)" );


    protected final double value;
    protected final Units unit;


    protected AScalar( final double _value, final Units _unit ) {
        value = _value;
        unit = _unit;
    }


    protected Value getInstance( final String _spec, final Units _unit, final boolean _required ) {

        // no arguments, no instance...
        if( (_spec == null) || (_unit == null) )
            return null;

        // remove whitespace and divide into number and suffix...
        String spec = _spec.replaceAll( "\\s", "" );
        Matcher mat = NUM_SUFFIX.matcher( spec );

        // if we don't have a match, then obviously we get no instance...
        if( !mat.matches() )
            return null;

        String numStr = mat.group( 1 );
        String multUnitStr = mat.group( 2 );

        // now the hard part - figuring out what the string section might mean...
        for( int i = 0; i <= multUnitStr.length() - (_required ? 1 : 0); i++ ) {

            String multStr = multUnitStr.substring( 0, i );
            if( Multipliers.contains( multStr ) ) {

                String unitStr = multUnitStr.substring( i );
                if( unitStr.isEmpty() || _unit.isUnit( unitStr ) ) {

                    return createInstance( Double.valueOf( numStr ) * Multipliers.get( multStr ), _unit );
                }
            }
        }

        // if we get here, we tried every possibility and there was no match...
        return null;
    }


    protected abstract AScalar createInstance( final double _value, final Units _unit );


    public double getValue() {
        return value;
    }


    public Units getUnit() {
        return unit;
    }
}

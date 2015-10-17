package com.cirsim.util;

import java.lang.reflect.Constructor;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Instances<T> {

    public T getByName( final String _name ) {
        T result = null;
        try {
            @SuppressWarnings( "unchecked" )
            Class<T> clazz = (Class<T>) Class.forName( _name );
            Constructor<T> ctor = clazz.getConstructor();
            result = ctor.newInstance();
        } catch( Exception e ) {
            // do nothing; null will be returned...
        }
        return result;
    }
}

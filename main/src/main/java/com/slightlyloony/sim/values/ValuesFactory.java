package com.slightlyloony.sim.values;

import com.google.common.collect.Maps;
import com.slightlyloony.sim.Circuit;
import com.slightlyloony.sim.Token;
import com.slightlyloony.sim.TokenType;

import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ValuesFactory {


    public static Map<Class<? extends Value>, Value> getValues(
            final Set<Value> _defaults, final ListIterator<Token> _tokenIterator, final Circuit _circuit ) {

        Map<Class<? extends Value>, Value> result = Maps.newHashMap();

        // iterate over all the sequential value tokens...
        Token token = null;
        while( _tokenIterator.hasNext() && (token = _tokenIterator.next()).is( TokenType.VALUE ) ) {

            String spec = token.getValue();

            // iterate over the possible value types until (and if) we get one that works...
            boolean gotValue = false;
            for( Value value : _defaults ) {

                // try to instantiate this type with the current specification...
                Value newValue = value.getInstance( spec, value.getUnit() );

                // if we got a value, figure out what to do with it...
                if( newValue != null ) {

                    gotValue = true;

                    // if our result already contains a value of this type, we've got an error situation...
                    if( result.containsKey( newValue.getClass() ) ) {
                        _circuit.getCircuitFactory().postError( "Unexpected second token of type " + newValue.getClass().getName(), token );
                    }

                    // otherwise, we need to store the new value in the result...
                    else {
                        result.put( newValue.getClass(), newValue );
                    }
                }
            }

            // if we didn't find a value, we've got an invalid spec...
            if( !gotValue ) {
                _circuit.getCircuitFactory().postWarning( "Invalid value specfication '" + spec + "'", token );
            }
        }

        // ensure each desired value is present in the result...
        for( Value value : _defaults ) {
            if( !result.containsKey( value.getClass() )) {
                result.put( value.getClass(), value );
            }
        }

        // give back the previously gobbled non-value token, if there is one...
        if( (token != null) && !token.is( TokenType.VALUE ))
            _tokenIterator.previous();

        return result;
    }
}

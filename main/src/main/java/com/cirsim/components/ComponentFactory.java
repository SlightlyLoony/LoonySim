package com.cirsim.components;

import com.cirsim.Circuit;
import com.cirsim.TokenType;
import com.cirsim.Token;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ComponentFactory {

    private static final Map<String, Class<? extends Component>> components = getComponents();

    /**
     * Using tokens in the given token list iterator, tests first to see if the next tokens represent a component, and if so, instantiate it.  Returns
     * null if the tokens do not represent a component, with the token list iterator's cursor unchanged.
     *
     * @param _tokenIterator token list iterator
     * @param _circuit the circuit that the component will belong to, if one is instantiated
     * @return the component instantiated, or null if the the tokens do not represent a component
     */
    public static Component getInstance( final ListIterator<Token> _tokenIterator, final Circuit _circuit ) {

        if( !_tokenIterator.hasNext() ) {
            _circuit.getCircuitFactory().postError( "No tokens remaining", _tokenIterator.previous() );
            _tokenIterator.next();
            return null;
        }
        else {
            Token type = _tokenIterator.next();
            if( type.is( TokenType.TYPE ) ) {
                return getComponent( type, _tokenIterator, _circuit );
            }
            else {
                _circuit.getCircuitFactory().postError( "Token is not a type", type );
                return null;
            }
        }
    }


    private static Component getComponent( final Token _token, final ListIterator<Token> _tokenIterator, final Circuit _circuit ) {
        String typeName = _token.getValue();
        Class<? extends Component> klass = components.get( typeName );
        if( klass != null ) {

            try {
                Constructor<? extends Component> ctor = klass.getConstructor( ListIterator.class, Circuit.class );
                return ctor.newInstance( _tokenIterator, _circuit );
            }
            catch( NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e ) {
                e.printStackTrace();
                return null;  // we don't care why it didn't work, just that it didn't...
            }
        }
        return null;
    }


    private static Map<String, Class<? extends Component>> getComponents() {

        Map<String, Class<? extends Component>> result = new HashMap<>();

        result.put( "Resistor",             Resistor.class             );
        result.put( "FixedDCVoltageSource", FixedDCVoltageSource.class );

        return result;
    }
}

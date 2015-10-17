package com.cirsim;

import com.cirsim.components.Component;
import com.cirsim.components.ComponentFactory;
import com.cirsim.nets.Net;
import com.cirsim.nets.NetFactory;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Circuit extends AElement {

    private final Map< String, Component> components = new HashMap<>();
    private final Map< String, Net> nets = new HashMap<>();


    protected Circuit( final ListIterator<Token> _tokenListIterator, final String _path, final CircuitFactory _circuitFactory ) {
        super( _tokenListIterator, _path, _circuitFactory );

        // loop over the tokens we understand, which are in one of the following forms:
        //   1. label componentType value[, value]...
        //   2. label Net terminal[, terminal]...
        //   3. keyword net | type | value[, value]...
        while( tokenIterator.hasNext() ) {

            Token nextToken = tokenIterator.next();

            // if it's a label, we've got form (1) or (2) above...
            if( nextToken.is( TokenType.LABEL )) {

                String label = nextToken.getValue();

                Component component = ComponentFactory.getInstance( tokenIterator, this );
                if( component != null) {
                    components.put( label, component );
                }
                else {
                    Net net = NetFactory.getInstance( tokenIterator, this );
                    if( net != null ) {

                    }
                    else {
                        circuitFactory.postError( "Labeled element is neither a component or a net", peekNextToken() );
                    }
                }

            }
        }
    }


    public boolean hasNet( final String _name ) { return nets.containsKey( _name ); }

    public boolean hasComponent( final String _name ) { return components.containsKey( _name ); }

    public Net getNet( final String _name ) { return nets.get( _name ); }

    public Component getComponent( final String _name ) { return components.get( _name ); }
}

// TODO make facilities for composite components: see comment below
// TODO implement parameterization (see comments below)

/* **********************************************
 * Composite components
 *
 * The general notion is to create "modules" containing multiple components that can be used as if they were a simple component.  The basic approach
 * is to extend the idea of a "circuit" to allow defining these modules, and then provide mechanisms for importing them into a circuit.
 *
 * Some specific ideas (in no particular order):
 *
 * -- Create a special "terminal" component that can be part of any network.  A circuit for a composite component would use these to define and name
 *    the terminals of the composite component.
 *
 * -- Define a naming standard for composite component circuit files, so that they may be identified and automagically imported.
 *
 * -- Define an import command, so that composite component circuit files can be specifically imported.
 *
 * -- In the compilation process:
 *    - create names for sub-circuit components by prefacing the super-circuit component's name.
 *    - map super-circuit networks to sub-circuit components, eliminating the terminal components in the process.
 * ***********************************************/

/* **********************************************
 * Parameterization
 *
 * The general notion is to provide a way for a parameter to be injected into a circuit, and to have that parameter determine the values of some of
 * the components.  A simple example of this might be specifying the gain of a general purpose audio amplifier circuit, and to derive the value of
 * a couple resistors from that gain.  Another might be specifying the center frequency and Q of a bandpass filter, and have the value of resistors
 * and capacitors in an active filter computed from that.  The supporting thoughts to date:
 *
 * -- Create a parameter component, with no terminals, whose only purpose is to have its value set (and in turn, set the value of computed elements)
 * -- Create a mechanism for inputs to subordinate circuits to be supplied as values to the component as it's being used
 * -- Create a mechanism for the value of computed elements to be calculated before a simulation is run
 * -- Create a mechanism for choosing component values from the standard value.
 * ***********************************************/
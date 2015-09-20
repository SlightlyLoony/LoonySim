package com.slightlyloony.sim;

import com.slightlyloony.sim.components.Component;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Circuit extends AElement {

    private String name;

    private double tickSeconds;  // time tick interval in seconds
    private Map< String, Component> components = new HashMap<>();
    private Map< String, Net > nets = new HashMap<>();


    protected Circuit( final ListIterator<Token> _tokenListIterator, final CircuitFactory _circuitFactory ) {
        super( _tokenListIterator, _circuitFactory );
    }


    public void add( final Component _component ) {
        String name = _component.getName().toUpperCase();
        if( components.containsKey( name ) )
            throw new IllegalStateException( "Duplicate component name: " + name );
        components.put( name, _component );
    }


//    public void add( final Net _net ) {
//        String name = _net.getName().toUpperCase();
//        if( nets.containsKey( name ) )
//            throw new IllegalStateException( "Duplicate net name: " + name );
//        nets.put( name, _net );
//    }


    public boolean hasNet( final String _name ) { return nets.containsKey( _name ); }

    public boolean hasComponent( final String _name ) { return components.containsKey( _name ); }

    public Net getNet( final String _name ) { return nets.get( _name ); }

    public Component getComponent( final String _name ) { return components.get( _name ); }

    public double getTickSeconds() {
        return tickSeconds;
    }


    public void setTickSeconds( final double _tickSeconds ) {
        tickSeconds = _tickSeconds;
    }
}

// TODO make nets specified separately, as first-class objects - not like SPICE, as part of the compopnents
// TODO make facilities for composite components: see comment below
// TODO traverse by checking identity and tracking them
// TODO consider better error reporting on compilation - are exceptions really the right way to do this?
// TODO consider how to handle statements larger than a single line - bracketing of some sort?  continuation characters? ???
// TODO use JSON strings for model definition, instead of SPICE strings.  We can always build a translator for SPICE files!

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
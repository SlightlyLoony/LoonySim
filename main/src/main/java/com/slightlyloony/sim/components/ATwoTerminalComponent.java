package com.slightlyloony.sim.components;

import com.slightlyloony.sim.Circuit;
import com.slightlyloony.sim.Token;

import java.util.ListIterator;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class ATwoTerminalComponent extends AComponent {


    protected ATwoTerminalComponent( final ListIterator<Token> _tokenIterator, final Circuit _circuit ) {
        super( _tokenIterator, _circuit );
    }
}

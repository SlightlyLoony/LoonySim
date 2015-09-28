package com.slightlyloony.sim.components;

import com.slightlyloony.sim.Circuit;
import com.slightlyloony.sim.Token;

import java.util.ListIterator;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class AComponent implements Component {



    protected AComponent( final ListIterator<Token> _tokenIterator, final Circuit _circuit ) {
    }


}

package com.slightlyloony.sim;

import java.util.ListIterator;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class NetFactory {


    /**
     * Using tokens in the given token list iterator, tests first to see if the next tokens represent a net, and if so, instantiate it.  Returns
     * null if the tokens do not represent a net, with the token list iterator's cursor unchanged.
     *
     * @param _tokenIterator token list iterator
     * @param _circuit the circuit that the net will belong to, if one is instantiated
     * @return the net instantiated, or null if the the tokens do not represent a net
     */
    public static Net getInstance( ListIterator<Token> _tokenIterator, Circuit _circuit ) {
        return null;
    }
}

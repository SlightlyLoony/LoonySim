package com.slightlyloony.sim;

import java.util.ListIterator;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Run extends AElement {

    protected Run( final ListIterator<Token> _tokenListIterator, final CircuitFactory _circuitFactory ) {
        super( _tokenListIterator, _circuitFactory );

        // the next two tokens MUST be a label followed by a "Run" type...
        if( isNextToken( _tokenListIterator, TokenType.LABEL ) ) {

            Token labelToken = _tokenListIterator.next();

            if( isNextToken( _tokenListIterator, TokenType.TYPE ) ) {

                Token typeToken = _tokenListIterator.next();
                type = typeToken.getValue();
                if( !"Run".equals( type ) ) {
                    _circuitFactory.postError( "Expected 'Run' type, not '" + type + "'", typeToken );
                }

            }
            else {
                _circuitFactory.postError( "Expected type token", peekNextToken( _tokenListIterator ) );
            }
        }
        else {
            _circuitFactory.postError( "Expected label token", peekNextToken( _tokenListIterator ) );
        }

        hashCode();
    }
}

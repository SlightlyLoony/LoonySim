package com.cirsim;

import java.io.IOException;
import java.util.ListIterator;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Run extends AElement {


    protected Circuit useElement = null;


    protected Run( final ListIterator<Token> _tokenListIterator, final String _path, final CircuitFactory _circuitFactory ) throws IOException {
        super( _tokenListIterator, _path, _circuitFactory );

        // analyze the remaining tokens, as they all should belong to this instance or its subordinates...
        while( _tokenListIterator.hasNext() ) {

            Token nextToken = peekNextToken();

            switch( nextToken.getType() ) {

                case KEY_USE:
                    if( useElement == null ) {
                        useElement = loadCircuit();
                    }
                    else {
                        circuitFactory.postError( "Multiple 'use' definitions", nextToken );
                        tokenIterator.next();
                    }
                    break;

                default:
                    break;
            }
        }

        hashCode();
    }
}

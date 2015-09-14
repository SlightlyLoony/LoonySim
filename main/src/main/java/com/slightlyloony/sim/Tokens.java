package com.slightlyloony.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Tokens {

    private final List<String> tokens = new ArrayList<>();
    private final ListIterator<String> tokenIterator = tokens.listIterator();
    private final String spec;


    public Tokens( final String _spec ) {
        spec = _spec;
        parse( _spec );
    }


    /**
     * Parses the given line into a list of strings that were separated by whitespace in the source line.  Comments are not returned.
     *
     * @param _line The source line to be parsed.
     *
     */
    private void parse( final String _line ) {

        if( (_line == null) || _line.isEmpty() )
            return;

        State state = State.START;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while( state != State.DONE ) {

            char c = _line.charAt( i );
            switch( state ) {

                case START:
                case IN_WHITESPACE:
                    if( !Character.isWhitespace( c ) ) {
                        if( c == '"' )
                            state = State.IN_QUOTES;
                        else {
                            state = State.IN_STRING;
                            sb.append( c );
                        }
                    }
                    break;

                case IN_STRING:
                    if( Character.isWhitespace( c ) ) {
                        tokens.add( sb.toString() );
                        sb.setLength( 0 );
                        state = State.IN_WHITESPACE;
                    }
                    else if( c == '"' ) {
                        tokens.add( sb.toString() );
                        sb.setLength( 0 );
                        state = State.IN_QUOTES;
                    }
                    else
                        sb.append( c );
                    break;

                case IN_QUOTES:
                    if( c == '"' ) {
                        // if we have two double quotes sequentially, then put a double quote in our token and skip past the second quote...
                        if( ((i + 1) < _line.length()) && ( _line.charAt( i + 1 ) == '"')) {
                            sb.append( c );
                            i++;
                        }
                        else
                            state = State.IN_STRING;
                    }
                    else
                        sb.append( c );
                    break;
            }
            i++;
            if( i >= _line.length() )
                state = State.DONE;
        }

        if( sb.length() > 0 )
            tokens.add( sb.toString() );
    }


    private enum State { START, IN_WHITESPACE, IN_STRING, IN_QUOTES, DONE }


    public void add( final String _token ) {
        tokens.add( _token );
    }


    public String next() {
        return tokenIterator.next();
    }


    public boolean hasNext() {
        return tokenIterator.hasNext();
    }


    public String getSpec() {
        return spec;
    }
}

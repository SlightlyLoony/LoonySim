package com.slightlyloony.sim;

import com.google.common.collect.Sets;
import com.slightlyloony.sim.util.S;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Tokenizer {

    private int line = 0;
    private int column = 0;
    private StringBuilder accumulator = new StringBuilder();
    private Set<String> keywords = Sets.newHashSet( "IMPORT", "EXPORT", "PACKAGE", "USE"  );
    private int index;
    private String source;
    private List<Token> tokens = new ArrayList<>();


    public void tokenize( final String _source ) throws ParseException {

        source = _source + '\n';

        for( index = 0; index < source.length(); index++ ) {

            char c = source.charAt( index );

            // handle special case of line comment: skip to next newline...
            if( (c == '/') && (peekNext() == '/') ) {
                index = source.indexOf( '\n', index + 2 );
                c = '\n';
            }

            // handle special case of inline comment: skip to terminating "*/" or error...
            if( (c == '/') && (peekNext() == '*' )) {

                // find the comment terminator...
                int term = source.indexOf( "*/", index + 2 );
                if( term < 0 )
                    throw new ParseException( "Unterminated '/*...*/' comment " + ref() );

                // correct the line and column trackers...
                String comment = source.substring( index + 2, term );
                int nlc = S.count( comment, '\n' );
                if( nlc == 0 ) {
                    column += (term - index) + 2;
                }
                else {
                    line += nlc;
                    column = comment.length() - comment.lastIndexOf( '\n' );
                }

                index = term + 1;
                continue;
            }


            column++;
            if( c == '\n' ) {
                if( accumulator.length() > 0 ) {
                    addTypeOrValueToken();
                }
                line++;
                column = 0;
            }

            if( Character.isWhitespace( c ) ) {
                if( accumulator.length() > 0 ) {
                    String word = accumulator.toString().toUpperCase();
                    if( keywords.contains( word ) ) {
                        addToken( TokenType.valueOf( "KEY_" + word ) );
                    }
                    else {
                        accumulator.append( c );
                    }
                }
            }
            else if( c == ':' ) {
                addToken( TokenType.LABEL );
            }
            else if( c == ',' ) {
                addTypeOrValueToken();
            }
            else {
                accumulator.append( c );
            }
        }
    }


    public List<Token> getTokens() {
        return tokens;
    }


    private void addTypeOrValueToken() {
        boolean isType = (tokens.size() > 0) && (tokens.get( tokens.size() - 1 ).getType() == TokenType.LABEL);
        addToken( isType ? TokenType.TYPE : TokenType.VALUE );
    }


    private String ref() {
        return "at line " + line + ", column " + column + ".";
    }


    private char peekNext() {
        return ( index + 1 >= source.length() ) ? 0 : source.charAt( index + 1 );
    }


    private void addToken( final TokenType _type ) {
        int col = column - accumulator.length();
        String val = accumulator.toString().trim();
        boolean fol = (tokens.size() == 0) || (tokens.get( tokens.size() - 1 ).getLine() < line + 1);
        tokens.add( new Token( _type, val, line + 1, col, fol ) );
        accumulator.setLength( 0 );
    }
}

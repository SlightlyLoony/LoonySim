package com.slightlyloony.sim;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.slightlyloony.sim.util.S;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Instances of this class produce tokens representing circuit elements, by parsing the source code for that element.  Unexpected occurrences in the
 * source code will create either warnings or errors, with appropriate human-readable messages.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class CircuitTokens {


    private static final Set<String> keywords = Sets.newHashSet( "IMPORT", "EXPORT", "PACKAGE", "USE"  );


    private final StringBuilder accumulator = new StringBuilder();  // the accumulated characters for a token during scanning...
    private final String source;                                    // the given source with a guaranteed newline at the end...
    private final List<Token> tokens = new ArrayList<>();           // the tokens equivalent to the given source...
    private final StringBuilder messages = new StringBuilder();     // any human-readable error or warning messages produced during parsing...

    private int line;         // current line number during parsing (0 based)...
    private int column;       // current column number during parsing (0 based)...
    private int index;        // index (within source) of current character during parsing...
    private int warnings;     // count of warnings that occurred during parsing...
    private int errors;       // count of errors that occurred during parsing...


    /**
     * Creates a new instance of this class from the provided source code.  Once the instance has been constructed, methods are available to determine
     * whether any errors or warnings occurred, and to retrieve human-readable error and warning messages.  The primary product of this class is a
     * list of tokens representing the circuit element defined by the source code used to construct the instance.
     *
     * @param _source  The circuit element source code to transform into equivalent tokens.
     */
    public CircuitTokens( final String _source ) {

        // it's an error if no source is provided...
        if( Strings.isNullOrEmpty( _source ) ) {
            postError( "No source to tokenize" );
            source = "";
            return;
        }

        source = S.ensureTerminalNewline( _source );

        // scan all the characters in the source code...
        for( index = 0; index < source.length(); index++ ) {

            // get the character we're currently evaluating...
            char c = source.charAt( index );

            // handle special case of line comment: skip to next newline...
            if( (c == '/') && (peekNext() == '/') ) {
                index = source.indexOf( '\n', index + 2 ) - 1;
                continue;
            }

            // handle special case of inline comment: skip to terminating "*/" or error...
            if( (c == '/') && (peekNext() == '*' )) {
                handleInlineComment();
                continue;
            }

            // decide what to do based on the category of the current character...
            if( Character.isWhitespace( c ) ) {

                // we only care about whitespace if the accumulator isn't empty...
                if( accumulator.length() > 0  ) {

                    // if the accumulator equals a keyword, emit a keyword token...
                    String word = accumulator.toString().toUpperCase();
                    if( keywords.contains( word ) ) {
                        addToken( TokenType.valueOf( "KEY_" + word ) );
                    }

                    // or else if we're expecting a type, emit a type token...
                    else if( expectingType() ) {
                        addToken( TokenType.TYPE );
                    }

                    // or else if it's a newline, then we must have a value token to emit...
                    else if( c == '\n' ) {
                        addToken( TokenType.VALUE );
                    }

                    // otherwise the non-newline whitespace might be part of a value, so just accumulate it...
                    else {
                        accumulator.append( c );
                    }
                }
            }

            // else if we have a label terminator...
            else if( c == ':' ) {

                // if the accumulator is empty, that's an error...
                if( accumulator.length() == 0 )
                    postError( "Empty label" );

                // otherwise, emit a label token...
                else
                    addToken( TokenType.LABEL );
            }

            // else if we have a value separator...
            else if( c == ',' ) {

                // then post a value token (an empty value is permissible...
                addToken( TokenType.VALUE );
            }

            // otherwise we just stuff it in the accumulator...
            else {
                accumulator.append( c );
            }

            // adjust the line and column tracking as required...
            column++;
            if( c == '\n' ) {
                line++;
                column = 0;
            }
        }
    }


    /**
     * Handles all the details of an inline ( / * ... * / ) comment.  This is slighly more complicated than you might think, simply because the
     * comment may traverse any number of newlines (inclduing zero) and that complicates the line and column tracking.
     */
    private void handleInlineComment() {

        // find the comment terminator...
        int term = source.indexOf( "*/", index + 2 );
        if( term >= 0 ) {

            // correct the line and column trackers...
            String comment = source.substring( index + 2, term );
            int nlc = S.count( comment, '\n' );
            if( nlc == 0 ) {
                column += (term - index) + 2;
            } else {
                line += nlc;
                column = comment.length() - comment.lastIndexOf( '\n' );
            }

            index = term + 1;
        }

        // if we couldn't find the terminator, it's a warning...
        else {
            postWarning( "Unterminated '/*...*/' comment" );
            index = source.length() - 2;
        }
    }


    /**
     * Returns the list of tokens parsed from the source given when this instance was created.  The tokens are in the same order as the source code
     * that generated them.
     *
     * @return an immutable list of tokens
     */
    public List<Token> getTokens() {
        return Collections.unmodifiableList( tokens );
    }


    /**
     * Returns the error and warning messages (if any) generated while parsing the source code given when this instance was created.  Each message
     * is terminated with a newline.
     *
     * @return the error and warning messages.
     */
    public String getMessages() {
        return messages.toString();
    }


    /**
     * Returns true if any warnings were generated while parsing the source given when this instance was created.
     *
     * @return true if any warnings were generated
     */
    public boolean isWarning() {
        return warnings > 0;
    }


    /**
     * Returns true if any errors were generated while parsing the source given when this instance was created.
     *
     * @return true if any errors were generated
     */
    public boolean isError() {
        return errors > 0;
    }


    /**
     * Returns the count of warnings generated while parsing the source given when this instance was created.
     *
     * @return the count of warnings generated
     */
    public int getWarningsCount() {
        return warnings;
    }


    /**
     * Returns the count of errors generated while parsing the source given when this instance was created.
     *
     * @return the count of errors generated
     */
    public int getErrorsCount() {
        return errors;
    }


    /**
     * Returns true if we're expecting a type token, meaning that the preceding token was a label, or the keyword "use".
     *
     * @return true if we're expecting a type token.
     */
    private boolean expectingType() {

        if( tokens.size() == 0 )
            return false;

        Token prev = tokens.get( tokens.size() - 1 );
        return prev.is( TokenType.LABEL ) || prev.is( TokenType.KEY_USE );
    }


    /**
     * Returns a string in the form " at line [x], column [y]." for appending to warning or error messages.  The line and column numbers are 1 based.
     *
     * @return the location reference string
     */
    private String ref() {
        return " at line " + (line + 1) + ", column " + (column + 1) + ".";
    }


    /**
     * Returns the character at (index + 1), or the null character if the index is currently at the last position in the source.
     *
     * @return the character at (index + 1)
     */
    private char peekNext() {
        return ( index + 1 >= source.length() ) ? 0 : source.charAt( index + 1 );
    }


    /**
     * Post a warning message.  The number of warnings is incremented, and a warning message consisting of the given string, location reference,
     * and newline is appended to the messages.
     *
     * @param _msg the base warning message
     */
    private void postWarning( final String _msg ) {
        messages.append( _msg ).append( ref() ).append( '\n' );
        warnings++;
    }


    /**
     * Post an error message.  The number of errors is incremented, and an error message consisting of the given string, location reference,
     * and newline is appended to the messages.
     *
     * @param _msg the base error message
     */
    private void postError( final String _msg ) {
        messages.append( _msg ).append( ref() ).append( '\n' );
        errors++;
    }


    /**
     * Adds a new token of the given <code>TokenType</code> to the list of tokens held by this instance.  The rest of the parameters for the new
     * token are taken from this instance's current state during parsing.
     *
     * @param _type  the <code>TokenType</code> of the new token
     */
    private void addToken( final TokenType _type ) {
        int col = 1 + column - accumulator.length();
        String val = accumulator.toString().trim();
        boolean fol = (tokens.size() == 0) || (tokens.get( tokens.size() - 1 ).getLine() < line + 1);
        tokens.add( new Token( _type, val, line + 1, col, fol ) );
        accumulator.setLength( 0 );
    }
}

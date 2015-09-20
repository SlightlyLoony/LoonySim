package com.slightlyloony.sim;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class AElement {

    protected final String path;
    protected final Map<String, String> imports = new HashMap<>();

    protected String type;


    protected AElement( final ListIterator<Token> _tokenListIterator, final CircuitFactory _circuitFactory ) {

        // if we have a package, it MUST be the first token...
        if( isNextToken( _tokenListIterator, TokenType.KEY_PACKAGE ) ) {

            // get the value following the package token...
            _tokenListIterator.next();  // get past the package token...
            String val = nextValue( _tokenListIterator );
            if( val != null ) {
                path = val;
            }
            else {
                _circuitFactory.postError( "Value for package not found", _tokenListIterator.next() );
                path = null;
            }
        }

        // if there's no package keyword, then this element must be in the default package...
        else {
            path = "";
        }

        // if we have import statements, they MUST be the next tokens...
        while( isNextToken( _tokenListIterator, TokenType.KEY_IMPORT ) ) {

            // get the value follwing the import token...
            _tokenListIterator.next();  // get past the import token...
            String val = nextValue( _tokenListIterator );
            if( val != null ) {
                imports.put( val.substring( val.lastIndexOf( '.' ) + 1 ), val );
            }
            else {
                _circuitFactory.postError( "Value for import not found", _tokenListIterator.next() );
            }
        }

        hashCode();
    }


    /**
     * Returns true if the next element in the token list iterator is of the given type.  The iterator's cursor is not affected.  If there is no
     * next element, returns false.
     *
     * @param _tokenListIterator iterator for a list of tokens
     * @param _type the token type to match
     * @return true if the next element matches the given type, false if it does not, or if there is no next element
     */
    protected boolean isNextToken( final ListIterator<Token> _tokenListIterator, final TokenType _type ) {

        if( !_tokenListIterator.hasNext() )
            return false;

        Token token = _tokenListIterator.next();
        _tokenListIterator.previous();
        return token.is( _type );
    }


    protected String nextValue( final ListIterator<Token> _tokenListIterator ) {

        if( !_tokenListIterator.hasNext() )
            return null;

        Token token = _tokenListIterator.next();
        if( token.is( TokenType.VALUE ) )
            return token.getValue();

        _tokenListIterator.previous();
        return null;
    }


    protected Token peekNextToken( final ListIterator<Token> _tokenListIterator) {

        if( !_tokenListIterator.hasNext() )
            return null;

        Token result = _tokenListIterator.next();
        _tokenListIterator.previous();
        return result;
    }


    public String getPath() {
        return path;
    }


    public Map<String, String> getImports() {
        return imports;
    }


    public String getType() {
        return type;
    }
}

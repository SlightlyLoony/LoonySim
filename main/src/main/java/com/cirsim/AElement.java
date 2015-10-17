package com.cirsim;

import java.io.IOException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class AElement {

    protected final String path;
    protected final Map<String, String> imports = new HashMap<>();
    protected final String type;
    protected final String label;
    protected final ListIterator<Token> tokenIterator;
    protected final CircuitFactory circuitFactory;
    protected final int indent;


    protected AElement( final ListIterator<Token> _tokenListIterator, final String _path, final CircuitFactory _circuitFactory ) {

        tokenIterator = _tokenListIterator;
        circuitFactory = _circuitFactory;

        // figure out the package path and element type we're supposed to be representing...
        int lastPeriod = _path.lastIndexOf( '\n' );
        String dPath = (lastPeriod >= 0) ? _path.substring( 0, lastPeriod ) : "";
        String dType = (lastPeriod >= 0) ? _path.substring( lastPeriod + 1 ) : _path;

        // if we have a package statement, it MUST be the first token...
        if( isNextToken( TokenType.KEY_PACKAGE ) ) {

            // get the value following the package token...
            tokenIterator.next();  // get past the package token...
            String val = nextValue();
            if( val != null ) {
                path = val;
            }
            else {
                _circuitFactory.postError( "Value for package not found", tokenIterator.next() );
                path = null;
            }
        }

        // if there's no package keyword, then this element must be in the default package...
        else {
            path = "";
        }

        // see if we have the right package (i.e., the one we were expecting)...
        if( !(path != null && path.equals( dPath )) ) {
            _circuitFactory.postError( "Element " + _path + " does not specify the correct package; was '" + path + "', should be '" + dPath + "'" );
        }

        // if we have import statements, they MUST be the next tokens...
        while( isNextToken( TokenType.KEY_IMPORT ) ) {

            // get the value follwing the import token...
            tokenIterator.next();  // get past the import token...
            String val = nextValue();
            if( val != null ) {
                imports.put( val.substring( val.lastIndexOf( '.' ) + 1 ), val );
            }
            else {
                _circuitFactory.postError( "Value for import not found", tokenIterator.next() );
            }
        }

        // the next token MUST be a label...
        if( isNextToken( TokenType.LABEL ) ) {

            Token labelToken = tokenIterator.next();
            label = tokenIterator.next().getValue();
            indent = labelToken.getColumn();

            // the token must be a type, matching what we're supposed to be
            if( isNextToken( TokenType.TYPE ) ) {

                Token typeToken = tokenIterator.next();
                type = typeToken.getValue();
                if( !"Run".equals( type ) ) {
                    _circuitFactory.postError( "Expected '" + dType + "' type, not '" + type + "'", typeToken );
                }
            }
            else {
                _circuitFactory.postError( "Expected type token", peekNextToken() );
                type = null;
            }
        }
        else {
            _circuitFactory.postError( "Expected label token", peekNextToken() );
            label = null;
            type = null;
            indent = -1;
        }

        hashCode();
    }


    protected Circuit loadCircuit() throws IOException {

        // get the type associated with the "use" statement, and load it...
        tokenIterator.next();  // get past the "use"...
        if( tokenIterator.hasNext() ) {

            Token typeToken = tokenIterator.next();
            if( !typeToken.is( TokenType.TYPE ))
                circuitFactory.postError( "Expected type, got '" + typeToken.getType() + "'", typeToken );

            String elementType = typeToken.getValue();
            if( !circuitFactory.hasType( elementType ) )
                elementType = imports.get( elementType );

            if( circuitFactory.hasType( elementType ) ) {
                AElement element = circuitFactory.load( elementType );
                if( element instanceof Circuit ) {
                    return (Circuit) element;
                }
                else {
                    circuitFactory.postError( "Expected Circuit element, got '" + element.getClass().getName() + "'", typeToken );
                }
            }
            else {
                circuitFactory.postError( "Unknown type: " + elementType, typeToken );
            }
        }
        else {
            circuitFactory.postError( "Missing type after element", tokenIterator.previous() );
        }
        return null;
    }


    /**
     * Returns true if the next element in the token list iterator is of the given type.  The iterator's cursor is not affected.  If there is no
     * next element, returns false.
     *
     * @param _type the token type to match
     * @return true if the next element matches the given type, false if it does not, or if there is no next element
     */
    protected boolean isNextToken( final TokenType _type ) {

        if( !tokenIterator.hasNext() )
            return false;

        Token token = tokenIterator.next();
        tokenIterator.previous();
        return token.is( _type );
    }


    protected String nextValue() {

        if( !tokenIterator.hasNext() )
            return null;

        Token token = tokenIterator.next();
        if( token.is( TokenType.VALUE ) )
            return token.getValue();

        tokenIterator.previous();
        return null;
    }


    protected Token peekNextToken() {

        if( !tokenIterator.hasNext() )
            return null;

        Token result = tokenIterator.next();
        tokenIterator.previous();
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


    public String getLabel() {
        return label;
    }


    public int getIndent() {
        return indent;
    }


    public CircuitFactory getCircuitFactory() {
        return circuitFactory;
    }
}

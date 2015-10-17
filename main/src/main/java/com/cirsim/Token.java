package com.cirsim;

import java.util.Objects;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Token {

    private final TokenType type;
    private final String value;
    private final int line;
    private final int column;
    private final boolean firstOnLine;


    public Token( final TokenType _type, final String _value, final int _line, final int _column, final boolean _firstOnLine ) {
        type = _type;
        value = _value;
        line = _line;
        column = _column;
        firstOnLine = _firstOnLine;
    }


    public boolean is( final TokenType _type ) {
        return _type == type;
    }


    public TokenType getType() {
        return type;
    }


    public String getValue() {
        return value;
    }


    public int getLine() {
        return line;
    }


    public int getColumn() {
        return column;
    }


    public boolean isFirstOnLine() { return firstOnLine; }


    @Override
    public boolean equals( final Object o ) {
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        Token token = (Token) o;
        return Objects.equals( line, token.line ) &&
                Objects.equals( column, token.column ) &&
                Objects.equals( firstOnLine, token.firstOnLine ) &&
                Objects.equals( type, token.type ) &&
                Objects.equals( value, token.value );
    }


    @Override
    public int hashCode() {
        return Objects.hash( type, value, line, column, firstOnLine );
    }
}

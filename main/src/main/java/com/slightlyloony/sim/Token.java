package com.slightlyloony.sim;

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
}

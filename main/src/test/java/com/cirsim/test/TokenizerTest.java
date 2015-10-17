package com.cirsim.test;

import com.cirsim.CircuitTokens;
import com.cirsim.Token;
import com.cirsim.TokenType;
import com.cirsim.util.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class TokenizerTest {

    @Test
    public void basic() {

        Token[] testRun1 = {
                new Token( TokenType.KEY_PACKAGE, "package",                          2,  1, true  ),
                new Token( TokenType.VALUE,       "com.slightlyloony.sim.test",       2,  9, false ),
                new Token( TokenType.KEY_IMPORT,  "import",                           4,  1, true  ),
                new Token( TokenType.VALUE,       "com.slightlyloony.sim.test.Test1", 4, 48, false ),
                new Token( TokenType.LABEL,       "TestRun1",                         9,  1, true  ),
                new Token( TokenType.TYPE,        "Run",                              9, 11, false ),
                new Token( TokenType.KEY_USE,     "use",                             10,  5, true  ),
                new Token( TokenType.TYPE,        "Test1",                           10,  9, false )
        };

        Token[] test1 = {
                new Token( TokenType.KEY_PACKAGE, "package",                                            1,  1, true  ),
                new Token( TokenType.VALUE,       "com.slightlyloony.sim.test",                         1,  9, false ),
                new Token( TokenType.KEY_IMPORT,  "import",                                             3,  1, true  ),
                new Token( TokenType.VALUE,       "com.slightlyloony.sim.test.utility.AlkalineBattery", 3,  8, false ),
                new Token( TokenType.LABEL,       "Test1",                                              9,  1, true  ),
                new Token( TokenType.TYPE,        "Circuit",                                            9,  8, false ),
                new Token( TokenType.LABEL,       "R1",                                                11,  5, true  ),
                new Token( TokenType.TYPE,        "Resistor",                                          11, 14, false ),
                new Token( TokenType.VALUE,       "1k",                                                12, 17, true  ),
                new Token( TokenType.VALUE,       "10%",                                               12, 41, false ),
                new Token( TokenType.VALUE,       "10 watts",                                          13, 17, true  ),
                new Token( TokenType.VALUE,       "wire wound",                                        13, 27, false ),
                new Token( TokenType.LABEL,       "R2",                                                14,  5, true  ),
                new Token( TokenType.TYPE,        "Resistor",                                          14, 14, false ),
                new Token( TokenType.VALUE,       "4.7k",                                              14, 23, false ),
                new Token( TokenType.VALUE,       "10%",                                               14, 29, false ),
                new Token( TokenType.VALUE,       "1/2watt",                                           14, 34, false ),
                new Token( TokenType.VALUE,       "thick film",                                        14, 43, false ),
                new Token( TokenType.LABEL,       "Battery",                                           15,  5, true  ),
                new Token( TokenType.TYPE,        "AlkalineBattery",                                   15, 14, false ),
                new Token( TokenType.LABEL,       "ground",                                            20,  5, true  ),
                new Token( TokenType.TYPE,        "Net",                                               20, 13, false ),
                new Token( TokenType.VALUE,       "R2.2",                                              20, 17, false ),
                new Token( TokenType.VALUE,       "Battery.minus",                                     20, 23, false ),
                new Token( TokenType.LABEL,       "Vcc",                                               21,  5, true  ),
                new Token( TokenType.TYPE,        "Net",                                               21, 13, false ),
                new Token( TokenType.VALUE,       "R1.1",                                              21, 17, false ),
                new Token( TokenType.VALUE,       "Battery.plus",                                      21, 23, false ),
                new Token( TokenType.LABEL,       "n1",                                                22,  5, true  ),
                new Token( TokenType.TYPE,        "Net",                                               22, 13, false ),
                new Token( TokenType.VALUE,       "R1.2",                                              22, 17, false ),
                new Token( TokenType.VALUE,       "R2.1",                                              22, 23, false )
        };

        CircuitTokens tokens = getTokens( "circuits/com/slightlyloony/sim/test/TestRun1.sim" );
        assertTrue( isMatch( tokens.getTokens(), testRun1 ) );
        assertFalse( tokens.isError() );
        assertFalse( tokens.isWarning() );

        tokens = getTokens( "circuits/com/slightlyloony/sim/test/Test1.sim" );
        assertTrue( isMatch( tokens.getTokens(), test1 ) );
        assertFalse( tokens.isError() );
        assertFalse( tokens.isWarning() );

        tokens = new CircuitTokens( null );
        assertTrue( tokens.isError() );
        assertFalse( tokens.isWarning() );
        assertTrue( tokens.getErrorsCount() == 1 );
        assertTrue( "No source to tokenize at line 1, column 1.\n".equals( tokens.getMessages() ) );

        tokens = getTokens( "circuits/com/slightlyloony/sim/test/Test1err1.sim" );
        assertTrue( isMatch( tokens.getTokens(), test1 ) );
        assertTrue( tokens.isError() );
        assertFalse( tokens.isWarning() );
        assertTrue( tokens.getErrorsCount() == 1 );
        assertTrue( "Empty label at line 9, column 16.\n".equals( tokens.getMessages() ) );

        tokens = getTokens( "circuits/com/slightlyloony/sim/test/Test1err2.sim" );
        assertTrue( isMatch( tokens.getTokens(), test1 ) );
        assertFalse( tokens.isError() );
        assertTrue( tokens.isWarning() );
        assertTrue( tokens.getWarningsCount() == 1 );
        assertTrue( "Unterminated '/*...*/' comment at line 22, column 28.\n".equals( tokens.getMessages() ) );

        hashCode();
    }


    private CircuitTokens getTokens( final String _path ) {
        String source = null;
        try {
            source = Files.readFileAsUTF8String( new File( _path ) );
        } catch( IOException e ) {
            e.printStackTrace();
        }
        return new CircuitTokens( source );
    }


    private boolean isMatch( final List<Token> _parsedTokens, final Token[] _desiredTokens ) {

        // do we have the right number of tokens?
        if( _parsedTokens.size() != _desiredTokens.length )
            return false;

        // do the tokens match?
        for( int i = 0; i < _desiredTokens.length; i++ )
            if( !_parsedTokens.get( i ).equals( _desiredTokens[i] ))
                return false;

        return true;
    }
}

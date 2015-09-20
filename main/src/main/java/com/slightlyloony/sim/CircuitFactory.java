package com.slightlyloony.sim;

import com.slightlyloony.sim.constants.Strings;
import com.slightlyloony.sim.util.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: make this accept jars (.zim?) for source

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class CircuitFactory {


    private final List<File> sourceDirs = new ArrayList<>();
    private final Map<String, File> sourceFiles = new HashMap<>();
    private final StringBuilder messages = new StringBuilder();

    private int errors;
    private int warnings;


    public AElement load( final String _runElement ) throws IOException {

        findSourceFiles();
        if( !sourceFiles.containsKey( _runElement ) )
            throw new IllegalArgumentException( "Run element does not exist: " + _runElement );

        // parse and tokenize our source file...
        String source = Files.readFileAsUTF8String( sourceFiles.get( _runElement ) );
        CircuitTokens tokenizer = new CircuitTokens( source );
        messages.append( tokenizer.getMessages() );
        errors += tokenizer.getErrorsCount();
        warnings += tokenizer.getWarningsCount();
        List<Token> tokens = tokenizer.getTokens();

        // if we got an error, stop here...
        if( tokenizer.isError() )
            return null;

        // figure out what kind of a circuit element this file represents, by looking for the type following the first label...
        String type = null;
        boolean gotLabel = false;
        for( Token token : tokens ) {
            if( gotLabel ) {
                if( token.is( TokenType.TYPE )) {
                    type = token.getValue();
                }
                break;
            }
            gotLabel = token.is( TokenType.LABEL );
        }

        // if we didn't get any type at all, bail out with an error...
        if( type == null ) {
            postError( "No circuit element type specified in " + _runElement );
            return null;
        }

        // this element is either a run element, a circuit element, or an error...
        switch( type ) {

            case "Circuit":
                return validate( new Circuit( tokens.listIterator(), this ), _runElement );

            case "Run":
                return validate( new Run( tokens.listIterator(), this ), _runElement );

            // if we get here, then we have something invalid as an element type, so bail out with an error...
            default:
                postError( "Invalid circuit element type '" + type + "' in " + _runElement );
                return null;
        }
    }


    public void addDir( final File _root ) {
        sourceDirs.add( _root );
    }


    public String getMessages() {
        return messages.toString();
    }


    public int getErrors() {
        return errors;
    }


    public int getWarnings() {
        return warnings;
    }


    public boolean isError() {
        return errors > 0;
    }


    public boolean isWarning() {
        return warnings > 0;
    }


    private AElement validate( final AElement _element, final String _path ) {

        int lastPeriod = _path.lastIndexOf( '\n' );
        String dPath = (lastPeriod >= 0) ? _path.substring( 0, lastPeriod ) : "";
        String dType = (lastPeriod >= 0) ? _path.substring( lastPeriod + 1 ) : _path;

        if( _element.getPath() != null ) {
            if(!_element.getPath().equals( dPath ) ) {
                postError( "Element " + _path + " does not specify the correct package; should be " + dPath );
            }
        }

        if( !_element.getType().equals( dType ) ) {
            postError( "Element " + _path + " does not specify the correct type; should be " + dType );
        }

        return _element;
    }


    protected void postError( final String _message ) {
        messages.append( _message ).append( '\n' );
        errors++;
    }


    protected void postWarning( final String _message ) {
        messages.append( _message ).append( '\n' );
        warnings++;
    }


    protected void postError( final String _message, final Token _token ) {
        messages.append( _message ).append( ref( _token ) ).append( '\n' );
        errors++;
    }


    protected void postWarning( final String _message, final Token _token ) {
        messages.append( _message ).append( ref( _token ) ).append( '\n' );
        warnings++;
    }


    private String ref( final Token _token ) {
        return (_token != null) ? " at line " + _token.getLine() + ", column " + _token.getColumn() : "";
    }


    private void findSourceFiles() {

        // for each root directory we have...
        for( File root : sourceDirs ) {
            exploreSourceTree( root, "" );
        }
    }


    private void exploreSourceTree( File _node, String _path ) {

        // we're traversing the directory tree breadth-first, so enumerate the files and add them to our index...
        File files[] = _node.listFiles( (node, name) -> { return new File( node, name ).isFile() && name.endsWith( Strings.SIM_FILE_EXT ); } );
        for( File file : files ) {
            String elementName = file.getName();
            elementName = elementName.substring( 0, elementName.length() - Strings.SIM_FILE_EXT.length() );
            String fqn = _path + "." + elementName;
            sourceFiles.put( fqn, file );
        }

        // now enumerate the directories and recursively explore them...
        files = _node.listFiles( (node, name) -> { return new File( node, name ).isDirectory(); } );
        for( File file : files ) {
            exploreSourceTree( file, _path.isEmpty() ? file.getName() : _path + "." + file.getName() );
        }
    }
}
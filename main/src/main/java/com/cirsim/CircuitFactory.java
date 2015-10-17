package com.cirsim;

import com.cirsim.util.Files;
import com.cirsim.constants.Strings;

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


    public AElement load( final String _elementPath ) throws IOException {

        findSourceFiles();
        if( !sourceFiles.containsKey( _elementPath ) )
            throw new IllegalArgumentException( "Run element does not exist: " + _elementPath );

        // parse and tokenize our source file...
        String source = Files.readFileAsUTF8String( sourceFiles.get( _elementPath ) );
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
            postError( "No circuit element type specified in " + _elementPath );
            return null;
        }

        // this element is either a run element, a circuit element, or an error...
        switch( type ) {

            case "Circuit":
                return new Circuit( tokens.listIterator(), _elementPath, this );

            case "Run":
                return new Run( tokens.listIterator(), _elementPath, this );

            // if we get here, then we have something invalid as an element type, so bail out with an error...
            default:
                postError( "Invalid circuit element type '" + type + "' in " + _elementPath );
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


    public void postError( final String _message ) {
        messages.append( _message ).append( '\n' );
        errors++;
    }


    public void postWarning( final String _message ) {
        messages.append( _message ).append( '\n' );
        warnings++;
    }


    public void postError( final String _message, final Token _token ) {
        messages.append( _message ).append( ref( _token ) ).append( '\n' );
        errors++;
    }


    public void postWarning( final String _message, final Token _token ) {
        messages.append( _message ).append( ref( _token ) ).append( '\n' );
        warnings++;
    }


    protected boolean hasType( final String _type ) {
        return sourceFiles.containsKey( _type );
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

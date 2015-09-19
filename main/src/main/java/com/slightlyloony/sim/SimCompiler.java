package com.slightlyloony.sim;

import com.slightlyloony.sim.constants.Strings;
import com.slightlyloony.sim.util.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class SimCompiler {


    private final List<File> sourceDirs = new ArrayList<>();
    private final Map<String, File> sourceFiles = new HashMap<>();


    public void compile( final String _runElement ) throws IOException, ParseException {
        findSourceFiles();
        if( !sourceFiles.containsKey( _runElement ) )
            throw new IllegalArgumentException( "Run element does not exist: " + _runElement );
        compile( sourceFiles.get( _runElement ));
    }


    public void addDir( final File _root ) {
        sourceDirs.add( _root );
    }


    private void compile( File _source ) throws IOException, ParseException {
        String source = Files.readFileAsUTF8String( _source );
        CircuitTokens tokenizer = new CircuitTokens( source );
        List<Token> tokens = tokenizer.getTokens();
        hashCode();
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

package com.slightlyloony.sim.util;

import com.google.common.base.Charsets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Files {

    public static String readFileAsUTF8String( final File _file ) throws IOException {
        return new String( java.nio.file.Files.readAllBytes( Paths.get( _file.getPath() ) ), Charsets.UTF_8 );
    }
}

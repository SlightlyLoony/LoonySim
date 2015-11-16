package com.cirsim.test;

import com.cirsim.matrices.ExpandingValueStore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
@SuppressWarnings("deprecation")
public class ExpandingValueStoreTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void simpleFillAndVerify() throws Exception {

        ExpandingValueStore vs = new ExpandingValueStore( 5, 1000 );
        int[] handles = new int[1000];
        for( int i = 0; i < 1000; i++) {
            handles[i] = vs.create();
            vs.put( handles[i], i );
        }
        for( int i = 0; i < 1000; i++) {
            assertEquals( i, vs.get( i ), 0 );
        }

        assertEquals( vs.memoryAllocated(), vs.memoryUsed() + vs.memoryUnused() );
        assertEquals( 9040, vs.memoryUsed() );
        assertEquals( 0, vs.memoryUnused() );
    }

    @Test
    public void bigFillAndVerify() throws Exception {

        int max = 4096 * 4096 - 1;
        ExpandingValueStore vs = new ExpandingValueStore( 20, max );
        int[] handles = new int[max];
        for( int i = 0; i < max; i++) {
            handles[i] = vs.create();
            vs.put( handles[i], i );
        }
        for( int i = 0; i < max; i++) {
            assertEquals( i, vs.get( i ), 0 );
        }

        assertEquals( vs.memoryAllocated(), vs.memoryUsed() + vs.memoryUnused() );
        assertEquals( 134218576, vs.memoryUsed() );
        assertEquals( 0, vs.memoryUnused() );
    }

    @Test
    public void tinyFillAndVerify() throws Exception {

        ExpandingValueStore vs = new ExpandingValueStore( 1, 10 );
        int[] handles = new int[10];
        for( int i = 0; i < 10; i++) {
            handles[i] = vs.create();
            vs.put( handles[i], i );
        }
        for( int i = 0; i < 10; i++) {
            assertEquals( i, vs.get( i ), 0 );
        }
        assertTrue( vs.getAllocatedSize() == 16 );

        assertEquals( vs.memoryAllocated(), vs.memoryUsed() + vs.memoryUnused() );
        assertEquals( 232, vs.memoryUsed() );
        assertEquals( 0, vs.memoryUnused() );
    }

    @Test
    public void blockSize() throws Exception {

        ExpandingValueStore vs = new ExpandingValueStore( 3, 1000 );
        for( int i = 0; i < 3; i++)
            vs.create();
        assertEquals( 4, vs.getAllocatedSize() );
        for( int i = 0; i < 2; i++)
            vs.create();
        assertEquals( 8, vs.getAllocatedSize() );
        for( int i = 0; i < 4; i++)
            vs.create();
        assertEquals( 16, vs.getAllocatedSize() );
        for( int i = 0; i < 8; i++)
            vs.create();
        assertEquals( 32, vs.getAllocatedSize() );
        for( int i = 0; i < 32; i++)
            vs.create();
        assertEquals( 64, vs.getAllocatedSize() );
    }

    @Test
    public void blockSizeAfterDeletions() throws Exception {

        // completely fill all 1024 allocated slots
        ExpandingValueStore vs = new ExpandingValueStore( 3, 1000 );
        for( int i = 0; i < 1024; i++)
            vs.create();

        // delete them all
        for( int i = 0; i < 1024; i++)
            vs.delete( i );

        // fill them all again, making sure that our indices are within the expected range
        for( int i = 0; i < 1024; i++)
            assertTrue( vs.create() < 1024);
    }

    @Test
    public void allocationAfterDeletion() throws Exception {

        // completely fill all 1024 allocated slots
        ExpandingValueStore vs = new ExpandingValueStore( 3, 1000 );
        for( int i = 0; i < 1024; i++)
            vs.create();

        assertEquals( vs.memoryAllocated(), vs.memoryUsed() + vs.memoryUnused() );
        assertEquals( 9040, vs.memoryUsed() );
        assertEquals( 0, vs.memoryUnused() );

        // delete 400 of them
        for( int i = 0; i < 400; i++)
            vs.delete( i );

        assertEquals( vs.memoryAllocated(), vs.memoryUsed() + vs.memoryUnused() );
        assertEquals( 5840, vs.memoryUsed() );
        assertEquals( 3200, vs.memoryUnused() );

        // fill them the again and make sure we have 1024 allocated
        for( int i = 0; i < 400; i++)
            vs.create();

        assertEquals( 1024, vs.getAllocatedSize() );
    }

    @Test
    public void overfull() throws Exception {

        exception.expect(IllegalStateException.class);
        exception.expectMessage("Value store is completely full");

        // completely fill all 1024 allocated slots + 1
        ExpandingValueStore vs = new ExpandingValueStore( 3, 1000 );
        for( int i = 0; i < 1025; i++)
            vs.create();
    }

    @Test
    public void doubleDelete() throws Exception {

        ExpandingValueStore vs = new ExpandingValueStore( 3, 1000 );
        int h = vs.create();
        vs.delete( h );

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Slot has already been deleted: 0");

        vs.delete( h );
    }

    @Test
    public void badMin() throws Exception {

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Min entries out of bounds: -1");

        ExpandingValueStore vs = new ExpandingValueStore( -1, 1000 );
    }

    @Test
    public void badMax1() throws Exception {

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Max entries out of bounds: 3");

        ExpandingValueStore vs = new ExpandingValueStore( 4, 3 );
    }

    @Test
    public void badMax2() throws Exception {

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Max entries out of bounds: 5000");

        ExpandingValueStore vs = new ExpandingValueStore( 4, 50_000_000 );
    }

    @Test
    public void badDeleteKey1() throws Exception {

        ExpandingValueStore vs = new ExpandingValueStore( 4, 1000 );

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Key out of range: -1");

        vs.delete( -1 );
    }

    @Test
    public void badDeleteKey2() throws Exception {

        ExpandingValueStore vs = new ExpandingValueStore( 4, 1000 );

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage( "Key out of range: 1" );

        vs.delete( 1 );
    }

    @Test
    public void badGetKey1() throws Exception {

        ExpandingValueStore vs = new ExpandingValueStore( 4, 1000 );

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Key out of range: -1");

        vs.get( -1 );
    }

    @Test
    public void badGetKey2() throws Exception {

        ExpandingValueStore vs = new ExpandingValueStore( 4, 1000 );

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage( "Key out of range: 1" );

        vs.get( 1 );
    }

    @Test
    public void badPutKey1() throws Exception {

        ExpandingValueStore vs = new ExpandingValueStore( 4, 1000 );

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Key out of range: -1");

        vs.put( -1, 0 );
    }

    @Test
    public void badPutKey2() throws Exception {

        ExpandingValueStore vs = new ExpandingValueStore( 4, 1000 );

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage( "Key out of range: 1" );

        vs.put( 1, 0 );
    }

    @Test
    public void putNaN() throws Exception {

        ExpandingValueStore vs = new ExpandingValueStore( 4, 1000 );

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage( "Attempted to store NaN: NaN" );

        vs.put( vs.create(), Double.NaN );
    }

    @Test
    public void putToDeleted() throws Exception {

        ExpandingValueStore vs = new ExpandingValueStore( 4, 1000 );
        vs.delete( vs.create() );

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage( "Slot has been deleted: 0" );

        vs.put( 0, 0 );
    }

    @Test
    public void getFromDeleted() throws Exception {

        ExpandingValueStore vs = new ExpandingValueStore( 4, 1000 );
        vs.delete( vs.create() );

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage( "Slot has been deleted: 0" );

        vs.get( 0 );
    }


}

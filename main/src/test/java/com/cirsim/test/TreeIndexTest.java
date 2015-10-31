package com.cirsim.test;

import com.cirsim.matrices.IndexIterator;
import com.cirsim.matrices.TreeIndex;
import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class TreeIndexTest {

    @Test
    public void basicTest() {

        TreeIndex ti = new TreeIndex( 10, 4095 );
        Shuffler shuffler = new Shuffler( 4095 );
        Iterator<Integer> si = shuffler.iterator();
        while( si.hasNext() ) {
            int n = si.next();

            if( ti.size() == 6 )
                hashCode();

            ti.put( n, n );

            TreeIndex.Stats stats = ti.validate();
            System.out.println( stats );
            if( !stats.valid || (stats.nodes != ti.size()) )
                hashCode();

            hashCode();
        }

        hashCode();
    }


    @Test
    public void basicDeleteTest() {

        bail: for( int limit = 1; limit < 500; limit++ ) {

            TreeIndex ti = new TreeIndex( 10, 4095 );
            Shuffler shuffler = new Shuffler( 4095 );
            Iterator<Integer> si = shuffler.iterator();

            int i = 0;
            while( si.hasNext() && (i++ < limit)) {

                int n = si.next();
                ti.put( n, n );

                TreeIndex.Stats stats = ti.validate();
                //System.out.println( stats );

                if( !stats.valid || (stats.nodes != ti.size()) )
                    break bail;
            }

            i = 0;
            si = shuffler.iterator();
            while( si.hasNext() && (i++ < limit)) {

                if( (limit == 9) && (i == 5) )
                    hashCode();

                int n = si.next();
                ti.remove( n );

                TreeIndex.Stats stats = ti.validate();
                //System.out.println( stats );
                if( !stats.valid || (stats.nodes != ti.size()) )
                    break bail;

            }
        }
    }


    @Test
    public void basicIteratorTest() {

        TreeIndex ti = new TreeIndex( 10, 1000 );
        Shuffler shuffler = new Shuffler( 4095 );
        Iterator<Integer> si = shuffler.iterator();
        for( int i = 0; i < 1000; i++ ) {
            int n = si.next();
            ti.put( n, n );
        }
        TreeIndex.Stats stats = ti.validate();
        System.out.println( stats );

        int lastKey = -1;
        IndexIterator ii = ti.iterator();
        while( ii.hasNext() ) {
            ii.next();
            int nextKey = ii.key();
            int nextVal = ii.value();
            if( nextKey < lastKey )
                hashCode();
            if( nextVal != nextKey )
                hashCode();
            System.out.println( "Key: " + nextKey );
        }
        hashCode();
    }



    private class Shuffler {
        private final int[] numbers;
        private final int size;
        private final Random random;


        private Shuffler( final int _size ) {
            size = _size;
            numbers = new int[size];
            random = new Random( 7635 );
            for( int i = 0; i < size; i++ )
                numbers[i] = i;
            shuffle();
        }


        private void shuffle() {
            for( int i = size - 1; i >= 1; i-- ) {
                int j = random.nextInt( i );
                int x = numbers[i];
                numbers[i] = numbers[j];
                numbers[j] = x;
            }
        }


        private Iterator<Integer> iterator() {
            return new ShufflerIterator();
        }


        private class ShufflerIterator implements Iterator<Integer> {

            int index;

            /**
             * Returns {@code true} if the iteration has more elements. (In other words, returns {@code true} if {@link #next} would return an element rather
             * than throwing an exception.)
             *
             * @return {@code true} if the iteration has more elements
             */
            @Override
            public boolean hasNext() {
                return index < size;
            }


            /**
             * Returns the next element in the iteration.
             *
             * @return the next element in the iteration
             * @throws NoSuchElementException if the iteration has no more elements
             */
            @Override
            public Integer next() {
                return numbers[index++];
            }
        }
    }
}
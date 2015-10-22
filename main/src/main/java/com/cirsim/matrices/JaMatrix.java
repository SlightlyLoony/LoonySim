package com.cirsim.matrices;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class JaMatrix implements Matrix {
    /**
     * Returns a value in the range 0..1 (inclusive) that indicates how much of the memory allocated by this instance is actually in use.  The purpose
     * of this is to allow identification of instances that are candidates for being copied to reduce memory consumption.  This number may be an
     * estimate; it need not be exact to be useful, especially in conjunction with {@link #memoryAllocated()}.
     *
     * @return the memory utilization factor for this instance
     */
    @Override
    public double memoryUtilization() {
        return 0;
    }


    /**
     * Returns the approximate number of bytes allocated by this instance.  Note that some, perhas much, of this allocated memory may be unused.  See
     * also {@link #memoryUtilization()}.
     *
     * @return the approximate number of bytes allocated by this intstance.
     */
    @Override
    public long memoryAllocated() {
        return 0;
    }
}

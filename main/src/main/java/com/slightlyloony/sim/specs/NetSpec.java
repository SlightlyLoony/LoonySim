package com.slightlyloony.sim.specs;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class NetSpec {

    private String name;
    private NetConnectionSpec connections[];


    public NetConnectionSpec[] getConnections() {
        return connections;
    }


    public String getName() {
        return name;
    }
}

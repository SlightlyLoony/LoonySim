package com.slightlyloony.sim;

import com.slightlyloony.sim.components.Component;
import com.slightlyloony.sim.components.Terminal;
import com.slightlyloony.sim.specs.NetConnectionSpec;
import com.slightlyloony.sim.specs.NetSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Net {

    private final String name;
    private final List<Terminal> terminals = new ArrayList<>();


    public Net( final NetSpec _netSpec, final Circuit _circuit ) {
        name = _netSpec.getName();
        for( NetConnectionSpec connection : _netSpec.getConnections() ) {
            Component component = _circuit.getComponent( connection.getComponent() );
            if( component == null )
                throw new IllegalArgumentException( "Component specified in net not found: " + connection.getComponent() );
            Terminal terminal = component.getTerminal( connection.getTerminal() );
            if( terminal == null )
                throw new IllegalArgumentException( "Terminal specified in net not found: " + connection.getComponent() + "#" + connection.getTerminal());
            terminal.setNet( this );
            terminals.add( terminal );
        }
    }


    public List<Terminal> getTerminals() { return terminals; }


    public String getName() {
        return name;
    }
}

package com.slightlyloony.sim;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Net {

    private final Terminals terminals = new Terminals();

    public void connect( ATerminal _terminal ) {
        NetTerminal ourTerminal = new NetTerminal( _terminal );
        terminals.add( ourTerminal, "" + terminals.size() );
        _terminal.connect( ourTerminal );
    }


    private class NetTerminal extends ATerminal {


        private NetTerminal( ATerminal _terminal ) {
            connectedTo = _terminal;
        }


        @Override
        boolean isVoltageSource() {
            return false;
        }


        @Override
        boolean isCurrentSource() {
            return false;
        }
    }
}

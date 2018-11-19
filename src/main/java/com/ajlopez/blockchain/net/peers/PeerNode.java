package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.InputChannel;
import com.ajlopez.blockchain.net.OutputChannel;
import com.sun.corba.se.spi.orbutil.fsm.Input;

/**
 * Created by ajlopez on 19/11/2018.
 */
public interface PeerNode extends InputChannel {
    void connectTo(Peer peer, OutputChannel channel);
}

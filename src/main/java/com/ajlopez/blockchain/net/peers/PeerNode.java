package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.MessageChannel;
import com.ajlopez.blockchain.net.OutputChannel;

/**
 * Created by ajlopez on 19/11/2018.
 */
public interface PeerNode extends MessageChannel {
    void connectTo(Peer peer, OutputChannel channel);
}

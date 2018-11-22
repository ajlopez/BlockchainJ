package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.MessageChannel;

/**
 * Created by ajlopez on 19/11/2018.
 */
public interface PeerNode extends MessageChannel {
    Peer getPeer();

    void connectTo(PeerNode node);
}

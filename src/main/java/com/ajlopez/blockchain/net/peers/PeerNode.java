package com.ajlopez.blockchain.net.peers;

import com.ajlopez.blockchain.net.MessageChannel;
import com.ajlopez.blockchain.net.Status;

/**
 * Created by ajlopez on 19/11/2018.
 */
public interface PeerNode extends MessageChannel {
    Peer getPeer();
}

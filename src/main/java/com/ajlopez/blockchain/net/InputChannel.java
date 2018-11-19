package com.ajlopez.blockchain.net;

import com.ajlopez.blockchain.net.messages.Message;
import com.ajlopez.blockchain.net.peers.Peer;

/**
 * Created by ajlopez on 02/02/2018.
 */
public interface InputChannel {
    void postMessage(Peer sender, Message message);
}

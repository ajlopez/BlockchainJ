package com.ajlopez.blockchain.net;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.messages.Message;

/**
 * Created by ajlopez on 04/02/2018.
 */
public class Peer implements OutputChannel {
    private PeerId id;

    public Peer(PeerId id) {
        this.id = id;
    }

    public Hash getId() {
        return this.id;
    }

    @Override
    public void postMessage(Message message) {
        // not implemented
    }
}

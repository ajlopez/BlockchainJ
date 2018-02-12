package com.ajlopez.blockchain.net;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.messages.Message;

/**
 * Created by ajlopez on 04/02/2018.
 */
public class Peer implements OutputChannel {
    private Hash hash;

    public Peer(Hash hash) {
        this.hash = hash;
    }

    public Hash getHash() {
        return this.hash;
    }

    @Override
    public void postMessage(Message message) {
        // not implemented
    }
}

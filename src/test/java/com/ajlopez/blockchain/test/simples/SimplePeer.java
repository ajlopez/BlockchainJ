package com.ajlopez.blockchain.test.simples;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.messages.Message;

/**
 * Created by ajlopez on 12/02/2018.
 */
public class SimplePeer extends Peer {
    private Message message;

    public SimplePeer(Hash id) {
        super(id);
    }

    @Override
    public void postMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }
}

package com.ajlopez.blockchain.test.simples;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.messages.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 12/02/2018.
 */
public class SimplePeer extends Peer {
    private Message message;
    private List<Message> messages = new ArrayList<>();

    public SimplePeer(PeerId id) {
        super(id);
    }

    @Override
    public void postMessage(Message message) {
        this.message = message;
        this.messages.add(message);
    }

    public Message getLastMessage() {
        return this.message;
    }

    public List<Message> getMessages() {
        return this.messages;
    }
}

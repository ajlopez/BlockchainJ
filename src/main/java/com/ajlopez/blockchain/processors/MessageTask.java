package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.messages.Message;

/**
 * Created by ajlopez on 06/02/2018.
 */
public class MessageTask {
    private Message message;
    private Peer sender;

    public MessageTask(Message message, Peer sender) {
        this.message = message;
        this.sender = sender;
    }

    public Message getMessage() {
        return this.message;
    }

    public Peer getSender() {
        return this.sender;
    }
}

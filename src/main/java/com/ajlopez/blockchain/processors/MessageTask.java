package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.net.Node;
import com.ajlopez.blockchain.net.messages.Message;

/**
 * Created by ajlopez on 06/02/2018.
 */
public class MessageTask {
    private Message message;
    private Node sender;

    public MessageTask(Message message, Node sender) {
        this.message = message;
        this.sender = sender;
    }

    public Message getMessage() {
        return this.message;
    }

    public Node getSender() {
        return this.sender;
    }
}

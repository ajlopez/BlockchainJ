package com.ajlopez.blockchain.net.messages;

/**
 * Created by ajlopez on 19/01/2018.
 */
public abstract class Message {
    private MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }

    public MessageType getMessageType() {
        return this.type;
    }

    public abstract byte[] getPayload();
}

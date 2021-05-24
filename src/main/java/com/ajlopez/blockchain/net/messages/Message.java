package com.ajlopez.blockchain.net.messages;

/**
 * Created by ajlopez on 19/01/2018.
 */
public abstract class Message {
    private final MessageType type;
    private byte[] payload;

    public Message(MessageType type) {
        this.type = type;
    }

    public MessageType getMessageType() {
        return this.type;
    }

    public byte[] getMessagePayload() {
        if (this.payload == null)
            this.payload = this.getPayload();

        return this.payload;
    }

    public abstract byte[] getPayload();
}

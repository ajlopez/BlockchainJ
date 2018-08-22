package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.StatusEncoder;
import com.ajlopez.blockchain.net.Status;

/**
 * Created by ajlopez on 04/02/2018.
 */
public class StatusMessage extends Message {
    private Status status;

    public StatusMessage(Status status) {
        super(MessageType.STATUS);
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    @Override
    public byte[] getPayload() {
        return StatusEncoder.encode(this.status);
    }
}

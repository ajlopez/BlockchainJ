package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.types.Hash;

/**
 * Created by ajlopez on 29/01/2018.
 */
public class GetBlockByHashMessage extends Message {
    private Hash hash;

    public GetBlockByHashMessage(Hash hash) {
        super(MessageType.GET_BLOCK_BY_HASH);
        this.hash = hash;
    }

    public Hash getHash() {
        return this.hash;
    }

    @Override
    public byte[] getPayload() {
        return this.hash.getBytes();
    }
}

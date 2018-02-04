package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.types.Hash;

/**
 * Created by ajlopez on 29/01/2018.
 */
public class GetBlockByHashMessage extends Message {
    public GetBlockByHashMessage(Hash hash) {
        super(MessageType.GET_BLOCK_BY_HASH, hash.getBytes());
    }

    public Hash getHash() {
        return new Hash(this.getPayload());
    }
}

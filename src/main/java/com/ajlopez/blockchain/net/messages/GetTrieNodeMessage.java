package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.types.Hash;

/**
 * Created by ajlopez on 21/06/2019.
 */
public class GetTrieNodeMessage extends Message {
    private final TrieType trieType;
    private final Hash trieHash;

    public GetTrieNodeMessage(TrieType trieType, Hash trieHash) {
        super(MessageType.GET_BLOCK_BY_NUMBER);
        this.trieType = trieType;
        this.trieHash = trieHash;
    }

    public TrieType getTrieType() { return this.trieType; }

    public Hash getTrieHash() { return this.trieHash; }

    @Override
    public byte[] getPayload() { return null; }
}

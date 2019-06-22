package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.RLP;

/**
 * Created by ajlopez on 21/06/2019.
 */
public class GetTrieNodeMessage extends Message {
    private final TrieType trieType;
    private final Hash trieHash;

    public GetTrieNodeMessage(TrieType trieType, Hash trieHash) {
        super(MessageType.GET_TRIE_NODE);
        this.trieType = trieType;
        this.trieHash = trieHash;
    }

    public TrieType getTrieType() { return this.trieType; }

    public Hash getTrieHash() { return this.trieHash; }

    @Override
    public byte[] getPayload() {
        byte[] type = new byte[] { (byte)this.trieType.ordinal() };
        return RLP.encodeList(RLP.encode(type), RLP.encode(this.trieHash.getBytes()));
    }
}

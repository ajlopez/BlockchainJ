package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.RLP;
import com.ajlopez.blockchain.store.TrieType;

/**
 * Created by ajlopez on 21/06/2019.
 */
public class GetTrieNodeMessage extends Message {
    private final Hash topHash;
    private final TrieType trieType;
    private final Hash trieHash;

    public GetTrieNodeMessage(Hash topHash, TrieType trieType, Hash trieHash) {
        super(MessageType.GET_TRIE_NODE);
        this.topHash = topHash;
        this.trieType = trieType;
        this.trieHash = trieHash;
    }

    public Hash getTopHash() { return this.topHash; }

    public TrieType getTrieType() { return this.trieType; }

    public Hash getTrieHash() { return this.trieHash; }

    @Override
    public byte[] getPayload() {
        byte[] type = new byte[] { (byte)this.trieType.ordinal() };
        return RLP.encodeList(RLP.encode(this.topHash.getBytes()), RLP.encode(type), RLP.encode(this.trieHash.getBytes()));
    }
}

package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.RLP;

/**
 * Created by ajlopez on 07/06/2019.
 */
public class TrieNodeMessage extends Message {
    private final Hash topHash;
    private final TrieType trieType;
    private final byte[] trieNode;

    public TrieNodeMessage(Hash topHash, TrieType trieType, byte[] trieNode) {
        super(MessageType.TRIE_NODE);
        this.topHash = topHash;
        this.trieType = trieType;
        this.trieNode = trieNode;
    }

    public Hash getTopHash() { return this.topHash; }

    public TrieType getTrieType() {
        return this.trieType;
    }

    public byte[] getTrieNode() {
        return this.trieNode;
    }

    @Override
    public byte[] getPayload() {
        byte[] type = new byte[] { (byte)this.trieType.ordinal() };
        return RLP.encodeList(RLP.encode(this.topHash.getBytes()), RLP.encode(type), RLP.encode(this.trieNode));
    }
}


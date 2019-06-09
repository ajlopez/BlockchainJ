package com.ajlopez.blockchain.net.messages;

import com.ajlopez.blockchain.encoding.RLP;

/**
 * Created by ajlopez on 07/06/2019.
 */
public class TrieNodeMessage extends Message {
    private final TrieType trieType;
    private final byte[] trieNode;

    public TrieNodeMessage(TrieType trieType, byte[] trieNode) {
        super(MessageType.TRIE_NODE);
        this.trieType = trieType;
        this.trieNode = trieNode;
    }

    public TrieType getTrieType() {
        return this.trieType;
    }

    public byte[] getTrieNode() {
        return this.trieNode;
    }

    @Override
    public byte[] getPayload() {
        byte[] type = new byte[] { (byte)this.trieType.ordinal() };
        return RLP.encodeList(RLP.encode(type), RLP.encode(this.trieNode));
    }
}


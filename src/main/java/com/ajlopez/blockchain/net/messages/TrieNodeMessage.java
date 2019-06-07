package com.ajlopez.blockchain.net.messages;

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
        return null;
    }
}


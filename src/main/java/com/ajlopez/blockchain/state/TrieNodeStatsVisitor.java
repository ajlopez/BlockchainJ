package com.ajlopez.blockchain.state;

/**
 * Created by ajlopez on 22/10/2020.
 */
public class TrieNodeStatsVisitor extends TrieNodeCounterVisitor {
    private long encodedSize;

    @Override
    public void processNode(Trie trie) {
        super.processNode(trie);

        encodedSize += trie.getEncoded().length;
    }

    public long getEncodedSize() { return this.encodedSize; }
}

package com.ajlopez.blockchain.state;

/**
 * Created by ajlopez on 21/10/2020.
 */
public class TrieNodeCounterVisitor extends TrieNodeVisitor {
    private int nodeCounter;
    private int valueCounter;

    @Override
    public void processNode(Trie trie) {
        this.nodeCounter++;

        if (trie.hasValue())
            this.valueCounter++;
    }

    public int getNodeCounter() { return this.nodeCounter; }

    public int getValueCounter() { return this.valueCounter; }
}

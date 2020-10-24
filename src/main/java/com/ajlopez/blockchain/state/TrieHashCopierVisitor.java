package com.ajlopez.blockchain.state;

import com.ajlopez.blockchain.store.TrieStore;

import java.io.IOException;

/**
 * Created by ajlopez on 24/10/2020.
 */
public class TrieHashCopierVisitor extends TrieHashVisitor {
    private final TrieStore targetTrieStore;

    public TrieHashCopierVisitor(TrieStore trieStore, TrieStore targetTrieStore) {
        super(trieStore);
        this.targetTrieStore = targetTrieStore;
    }

    @Override
    public void processNode(Trie trie) throws IOException {
        this.targetTrieStore.save(trie);
    }
}

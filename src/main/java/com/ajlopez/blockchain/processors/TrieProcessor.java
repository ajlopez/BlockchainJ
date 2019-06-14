package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.TrieStore;

/**
 * Created by ajlopez on 14/06/2019.
 */
public class TrieProcessor {
    private final TrieStore trieStore;

    public TrieProcessor(TrieStore trieStore) {
        this.trieStore = trieStore;
    }

    public void saveNode(byte[] nodeData) {
        Trie trie = Trie.fromEncoded(nodeData, this.trieStore);
        Hash trieHash = trie.getHash();

        if (this.trieStore.exists(trieHash))
            return;

        this.trieStore.save(trie);
    }
}

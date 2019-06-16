package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.TrieStore;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ajlopez on 14/06/2019.
 */
public class TrieProcessor {
    private final TrieStore trieStore;
    private final Set<Hash> pendingHashes = new HashSet<>();

    public TrieProcessor(TrieStore trieStore) {
        this.trieStore = trieStore;
    }

    public void saveNode(byte[] nodeData) {
        Trie trie = Trie.fromEncoded(nodeData, this.trieStore);
        Hash trieHash = trie.getHash();

        if (!this.pendingHashes.isEmpty() && this.pendingHashes.contains(trieHash))
            return;

        if (this.trieStore.exists(trieHash))
            return;

        this.trieStore.save(trie);

        Hash[] subHashes = trie.getSubHashes();

        for (int k = 0; k < subHashes.length; k++)
            if (!this.trieStore.exists(subHashes[k]))
                this.pendingHashes.add(subHashes[k]);
    }

    public Set<Hash> getPendingHashes() {
        return this.pendingHashes;
    }
}
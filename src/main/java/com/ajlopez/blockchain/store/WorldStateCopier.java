package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ajlopez on 19/07/2020.
 */
public class WorldStateCopier {
    private final Stores sourceStores;
    private final Stores targetStores;
    private final TrieStore sourceAccountTrieStore;
    private final TrieStore targetAccountTrieStore;
    private final Queue<Hash> hashes = new LinkedList<>();

    public WorldStateCopier(Stores sourceStores, Stores targetStores, Hash rootHash) {
        this.sourceStores = sourceStores;
        this.targetStores = targetStores;
        this.sourceAccountTrieStore = sourceStores.getAccountTrieStore();
        this.targetAccountTrieStore = targetStores.getAccountTrieStore();
        this.hashes.add(rootHash);
    }

    public void process() throws IOException {
        while (!this.hashes.isEmpty())
            processHash(this.hashes.poll());
    }

    private void processHash(Hash hash) throws IOException {
        Trie trie = this.sourceAccountTrieStore.retrieve(hash);

        if (!this.targetAccountTrieStore.exists(hash))
            this.targetAccountTrieStore.save(trie);

        Hash[] subhashes = trie.getSubHashes();

        for (int k = 0; k < subhashes.length; k++)
            if (subhashes[k] != null)
                this.hashes.add(subhashes[k]);
    }
}

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
    private final TrieStore source;
    private final TrieStore target;
    private final Queue<Hash> hashes = new LinkedList<>();

    public WorldStateCopier(TrieStore source, TrieStore target, Hash rootHash) {
        this.source = source;
        this.target = target;
        this.hashes.add(rootHash);
    }

    public void process() throws IOException {
        while (!this.hashes.isEmpty())
            processHash(this.hashes.poll());
    }

    private void processHash(Hash hash) throws IOException {
        Trie trie = this.source.retrieve(hash);

        if (!this.target.exists(hash))
            this.target.save(trie);

        Hash[] subhashes = trie.getSubHashes();

        for (int k = 0; k < subhashes.length; k++)
            if (subhashes[k] != null)
                this.hashes.add(subhashes[k]);
    }
}

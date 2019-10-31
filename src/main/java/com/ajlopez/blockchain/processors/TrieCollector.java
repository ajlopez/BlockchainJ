package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.TrieStore;

import java.io.IOException;
import java.util.*;

/**
 * Created by ajlopez on 14/06/2019.
 */
public class TrieCollector {
    private static final Set<Hash> noHashes = Collections.EMPTY_SET;
    private final TrieStore trieStore;
    private final Set<Hash> pendingHashes = new HashSet<>();

    public TrieCollector(TrieStore trieStore, Hash expectedHash) throws IOException {
        this.trieStore = trieStore;
        this.expectHash(expectedHash);
    }

    public boolean expectHash(Hash expectedHash) throws IOException {
        if (this.trieStore.exists(expectedHash))
            return false;

        if (this.pendingHashes.contains(expectedHash))
            return false;

        this.pendingHashes.add(expectedHash);

        return true;
    }

    public Set<Hash> saveNode(byte[] nodeData) throws IOException {
        Trie trie = Trie.fromEncoded(nodeData, this.trieStore);
        Hash trieHash = trie.getHash();

        if (!this.pendingHashes.contains(trieHash))
            return noHashes;

        this.pendingHashes.remove(trieHash);

        if (this.trieStore.exists(trieHash))
            return noHashes;

        this.trieStore.save(trie);

        Hash[] subHashes = trie.getSubHashes();
        Set<Hash> newHashes = new HashSet<>();

        for (int k = 0; k < subHashes.length; k++)
            if (this.expectHash(subHashes[k]))
                newHashes.add(subHashes[k]);

        return newHashes;
    }

    public Set<Hash> getPendingHashes() {
        return this.pendingHashes;
    }
}

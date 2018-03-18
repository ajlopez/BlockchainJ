package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;

/**
 * Created by ajlopez on 04/03/2018.
 */
public class TrieStore {
    private KeyValueStore store;

    public TrieStore(KeyValueStore store) {
        this.store = store;
    }

    public void save(Trie trie) {
        this.store.setValue(trie.getHash().getBytes(), trie.getEncoded());
    }

    public Trie retrieve(Hash hash) {
        byte[] encoded = this.store.getValue(hash.getBytes());

        // TODO add hash
        if (encoded == null)
            throw new RuntimeException("Unknown trie");

        return Trie.fromEncoded(encoded, this);
    }
}

package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;

/**
 * Created by ajlopez on 21/01/2019.
 */
public class CodeStore {
    private final KeyValueStore store;

    public CodeStore(KeyValueStore store) {
        this.store = store;
    }

    public byte[] getCode(Hash codeHash) {
        return this.store.getValue(codeHash.getBytes());
    }

    public void putCode(Hash codeHash, byte[] code) {
        this.store.setValue(codeHash.getBytes(), code);
    }
}

package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;

/**
 * Created by ajlopez on 21/01/2019.
 */
public class CodeStore {
    private Trie trie;

    public CodeStore(Trie trie) {
        this.trie = trie;
    }

    public byte[] getCode(Hash codeHash) {
        return this.trie.get(codeHash.getBytes());

    }

    public void putCode(Hash codeHash, byte[] code) {
        this.trie = this.trie.put(codeHash.getBytes(), code);
    }
}

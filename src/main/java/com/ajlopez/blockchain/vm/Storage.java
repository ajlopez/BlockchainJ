package com.ajlopez.blockchain.vm;

import com.ajlopez.blockchain.state.Trie;

/**
 * Created by ajloopez on 20/11/2017.
 */
public class Storage {
    private Trie trie = Trie.getEmptyTrie();

    public byte[] getValue(byte[] address) {
        return this.trie.get(address);
    }

    public void setValue(byte[] address, byte[] value) {
        this.trie = this.trie.put(address, value);
    }
}

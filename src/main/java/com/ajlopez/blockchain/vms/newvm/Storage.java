package com.ajlopez.blockchain.vms.newvm;

import com.ajlopez.blockchain.state.Trie;

import java.io.IOException;

/**
 * Created by ajlopez on 20/11/2017.
 */
public class Storage {
    private Trie trie = new Trie();

    public byte[] getValue(byte[] address) throws IOException {
        return this.trie.get(address);
    }

    public void setValue(byte[] address, byte[] value) {
        this.trie = this.trie.put(address, value);
    }
}

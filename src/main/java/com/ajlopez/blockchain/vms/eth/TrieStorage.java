package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;

/**
 * Created by Angel on 17/02/2019.
 */
public class TrieStorage implements Storage {
    private Trie trie;

    public TrieStorage(Trie trie) {
        this.trie = trie;
    }

    @Override
    public void setValue(DataWord address, DataWord value) {
        byte[] bkey = address.getBytes();

        if (value.equals(DataWord.ZERO))
            this.trie = this.trie.delete(bkey);
        else {
            byte[] bvalue = value.toNormalizedBytes();
            this.trie = this.trie.put(bkey, bvalue);
        }
    }

    @Override
    public DataWord getValue(DataWord address) {
        byte[] data = this.trie.get(address.getBytes());

        if (data == null)
            return DataWord.ZERO;

        return new DataWord(data);
    }

    public Hash getRootHash() {
        return this.trie.getHash();
    }

    public void commit() {
        this.trie.save();
    }
}

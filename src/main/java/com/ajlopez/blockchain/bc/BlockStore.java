package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.BlockEncoder;
import com.ajlopez.blockchain.store.KeyValueStore;

import java.io.IOException;

/**
 * Created by ajlopez on 03/02/2018.
 */
public class BlockStore {
    private final KeyValueStore keyValueStore;

    public BlockStore(KeyValueStore keyValueStore) {
        this.keyValueStore = keyValueStore;
    }

    public void saveBlock(Block block) throws IOException {
        this.keyValueStore.setValue(block.getHash().getBytes(), BlockEncoder.encode(block));
    }

    public Block getBlock(Hash hash) throws IOException {
        byte[] encoded = this.keyValueStore.getValue(hash.getBytes());

        if (encoded == null)
            return null;

        return BlockEncoder.decode(encoded);
    }

    public boolean containsBlock(Hash hash) throws IOException {
        return this.keyValueStore.getValue(hash.getBytes()) != null;
    }
}

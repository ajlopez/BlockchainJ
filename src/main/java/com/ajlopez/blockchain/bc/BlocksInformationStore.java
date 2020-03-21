package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.BlocksInformationEncoder;
import com.ajlopez.blockchain.store.KeyValueStore;
import com.ajlopez.blockchain.utils.ByteUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 21/03/2020.
 */
public class BlocksInformationStore {
    private final KeyValueStore store;

    public BlocksInformationStore(KeyValueStore store) {
        this.store = store;
    }

    public void put(long height, BlocksInformation blocksInformation) throws IOException {
        byte[] key = ByteUtils.unsignedLongToNormalizedBytes(height);
        byte[] encoded = BlocksInformationEncoder.encode(blocksInformation);

        this.store.setValue(key, encoded);
    }

    public BlocksInformation get(long height) throws IOException {
        byte[] key = ByteUtils.unsignedLongToNormalizedBytes(height);

        byte[] encoded = this.store.getValue(key);

        if (encoded == null)
            return null;

        return BlocksInformationEncoder.decode(encoded);
    }
}

package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.utils.ByteArrayWrapper;

import java.util.HashMap;

/**
 * Created by ajlopez on 06/01/2018.
 */
public class HashMapStore implements KeyValueStore {
    private HashMap<ByteArrayWrapper, byte[]> values = new HashMap<>();

    public byte[] getValue(byte[] key) {
        return this.values.get(new ByteArrayWrapper(key));
    }

    public void setValue(byte[] key, byte[] value) {
        this.values.put(new ByteArrayWrapper(key), value);
    }
}


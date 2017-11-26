package com.ajlopez.blockchain.vm;

import com.ajlopez.blockchain.utils.ByteArrayWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 26/11/2017.
 */
public class Memory {
    private Map<ByteArrayWrapper, byte[]> values = new HashMap<>();

    public byte[] getValue(byte[] key) {
        return values.get(new ByteArrayWrapper(key));
    }

    public void setValue(byte[] key, byte[] value) {
        values.put(new ByteArrayWrapper(key), value);
    }
}

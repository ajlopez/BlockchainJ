package com.ajlopez.blockchain.vm;

import com.ajlopez.blockchain.utils.ByteArrayWrapper;
import com.ajlopez.blockchain.utils.ByteUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 26/11/2017.
 */
public class Memory {
    private Map<ByteArrayWrapper, byte[]> values = new HashMap<>();

    public byte[] getValue(byte[] key) {
        return values.get(toKey(key));
    }

    public void setValue(byte[] key, byte[] value) {
        values.put(toKey(key), value);
    }

    private static ByteArrayWrapper toKey(byte[] key) {
        int offset = ByteUtils.getInitialOffset(key);
        int length = key.length - offset;

        if (offset == 0)
            return new ByteArrayWrapper(key);

        return new ByteArrayWrapper(key, offset, length);
    }
}

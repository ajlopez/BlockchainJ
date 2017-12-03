package com.ajlopez.blockchain.vm;

import com.ajlopez.blockchain.utils.ByteArrayWrapper;
import com.ajlopez.blockchain.utils.ByteUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 26/11/2017.
 */
public class Memory {
    private byte[] bytes;

    public byte getValue(int offset) {
        if (this.bytes == null)
            return 0;

        return bytes[offset];
    }

    public void setValue(int offset, byte value)
    {
        if (this.bytes == null)
            this.bytes = new byte[1024];

        this.bytes[offset] = value;
    }
}

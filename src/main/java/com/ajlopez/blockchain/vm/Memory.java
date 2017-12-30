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
        if (this.bytes == null || this.bytes.length <= offset)
            return 0;

        return this.bytes[offset];
    }

    public byte[] getValues(int offset, int length) {
        byte[] values = new byte[length];

        if (this.bytes == null)
            return values;

        System.arraycopy(this.bytes, offset, values, 0, length);

        return values;
    }

    public void setValue(int offset, byte value)
    {
        if (this.bytes == null)
            this.bytes = new byte[1024];

        this.bytes[offset] = value;
    }

    public void setValues(int offset, byte[] values) {
        if (this.bytes == null)
            this.bytes = new byte[1024];

        System.arraycopy(values, 0, this.bytes, offset, values.length);
    }
}

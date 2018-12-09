package com.ajlopez.blockchain.vms.newvm;

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

        if (this.bytes == null || this.bytes.length <= offset)
            return values;

        int size = length;

        if (offset + length > this.bytes.length)
            size -= length + offset - this.bytes.length;

        System.arraycopy(this.bytes, offset, values, 0, size);

        return values;
    }

    public void setValue(int offset, byte value)
    {
        ensureCapacity(offset + 1);

        this.bytes[offset] = value;
    }

    public void setValues(int offset, byte[] values) {
        ensureCapacity(offset + values.length);

        System.arraycopy(values, 0, this.bytes, offset, values.length);
    }

    private void ensureCapacity(int length) {
        int blocks = (length + 1023) / 1024;
        int required = blocks * 1024;

        if (this.bytes == null) {
            this.bytes = new byte[required];
            return;
        }

        if (this.bytes.length >= required)
            return;

        byte[] newbytes = new byte[required];
        System.arraycopy(this.bytes, 0, newbytes, 0, this.bytes.length);

        this.bytes = newbytes;
    }
}

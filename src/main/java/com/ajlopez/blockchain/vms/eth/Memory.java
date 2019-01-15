package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;

/**
 * Created by ajlopez on 11/12/2018.
 */
public class Memory {
    private byte[] bytes;

    public void setValue(int address, DataWord value) {
        ensureSize(address + DataWord.DATAWORD_BYTES);

        System.arraycopy(value.getBytes(), 0, this.bytes, address, DataWord.DATAWORD_BYTES);
    }

    public DataWord getValue(int address) {
        int size = this.size();

        if (address + DataWord.DATAWORD_BYTES <= size)
            return DataWord.fromBytes(this.bytes, address, DataWord.DATAWORD_BYTES);

        if (address < size) {
            byte[] dbytes = new byte[DataWord.DATAWORD_BYTES];
            System.arraycopy(this.bytes, address, dbytes, 0, size - address);

            return new DataWord(dbytes);
        }

        return DataWord.ZERO;
    }

    public void setByte(int address, byte value) {
        ensureSize(address + 1);

        this.bytes[address] = value;
    }

    public void setBytes(int address, byte[] bytes, int offset, int length) {
        ensureSize(address + length);

        System.arraycopy(bytes, offset, this.bytes, address, Math.min(bytes.length - offset, length));
    }

    public byte[] getBytes(int address, int length) {
        byte[] bytes = new byte[length];

        System.arraycopy(this.bytes, address, bytes, 0, Math.min(this.bytes.length - address, length));

        return bytes;
    }

    public int size() {
        if (this.bytes == null)
            return 0;

        return this.bytes.length;
    }

    private void ensureSize(int size) {
        if (this.bytes == null) {
            this.bytes = new byte[size];
            return;
        }

        if (this.bytes.length >= size)
            return;

        byte[] newbytes = new byte[size];

        System.arraycopy(this.bytes, 0, newbytes, 0, this.bytes.length);

        this.bytes = newbytes;
    }
}

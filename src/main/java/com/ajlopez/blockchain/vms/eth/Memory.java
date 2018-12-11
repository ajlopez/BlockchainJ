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
        if (address + DataWord.DATAWORD_BYTES <= this.size())
            return DataWord.fromBytes(this.bytes, address, DataWord.DATAWORD_BYTES);

        return DataWord.ZERO;
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

package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.utils.ByteUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 11/12/2018.
 */
public class Memory {
    public static final int CHUNK_SIZE = 4096;
    private final List<byte[]> chunks = new ArrayList<>();
    private int size;

    public void setValue(int address, DataWord value) {
        ensureSize(address + DataWord.DATAWORD_BYTES);

        this.setBytes(address, value.getBytes(), 0, DataWord.DATAWORD_BYTES);
    }

    public DataWord getValue(int address) {
        if (address >= this.size)
            return DataWord.ZERO;

        byte[] data = this.getBytes(address, DataWord.DATAWORD_BYTES);

        // TODO improve copy bytes
        return DataWord.fromBytes(data, 0, data.length);
    }

    public void setByte(int address, byte value) {
        ensureSize(address + 1);

        int nchunk = address / CHUNK_SIZE;
        int choffset = address % CHUNK_SIZE;

        this.chunks.get(nchunk)[choffset] = value;
    }

    public void setBytes(int address, byte[] bytes, int offset, int length) {
        ensureSize(address + length);

        if (bytes.length <= offset)
            return;

        int nchunk = address / CHUNK_SIZE;
        int choffset = address % CHUNK_SIZE;
        int tocopy = Math.min(bytes.length - offset, length);
        int tofill = length > bytes.length - offset ? length - (bytes.length - offset) : 0;
        int copied = 0;

        while (copied < tocopy) {
            int nbytes;

            if (choffset + (tocopy - copied) > CHUNK_SIZE)
                nbytes = CHUNK_SIZE - choffset;
            else
                nbytes = tocopy - copied;

            System.arraycopy(bytes, offset + copied, this.chunks.get(nchunk), choffset, nbytes);

            copied += nbytes;
            nchunk++;
            choffset = 0;
        }

        if (tofill == 0)
            return;

        address += length - tofill;

        nchunk = address / CHUNK_SIZE;
        choffset = address % CHUNK_SIZE;
        int filled = 0;

        while (filled < tofill) {
            int nbytes;

            if (choffset + (tofill - filled) > CHUNK_SIZE)
                nbytes = CHUNK_SIZE - choffset;
            else
                nbytes = tofill - filled;

            ByteUtils.fillWithZeros(this.chunks.get(nchunk), choffset, nbytes);

            filled += nbytes;
            nchunk++;
            choffset = 0;
        }
    }

    public byte[] getBytes(int address, int length) {
        byte[] bytes = new byte[length];

        if (this.size < address)
            return bytes;

        int nchunk = address / CHUNK_SIZE;
        int offset = address % CHUNK_SIZE;
        int tocopy = Math.min(this.size - address, length);
        int copied = 0;

        while (copied < tocopy) {
            int nbytes;

            if (offset + (tocopy - copied) > CHUNK_SIZE)
                nbytes = CHUNK_SIZE - offset;
            else
                nbytes = tocopy - copied;

            System.arraycopy(this.chunks.get(nchunk), offset, bytes, copied, nbytes);

            copied += nbytes;
            nchunk++;
            offset = 0;
        }

        return bytes;
    }

    public int size() {
        return this.size;
    }

    private void ensureSize(int size) {
        if (size <= this.size)
            return;

        while (this.chunks.size() * CHUNK_SIZE < size)
            this.chunks.add(new byte[CHUNK_SIZE]);

        this.size = size;
    }
}

package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;

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

        int nchunk = address / CHUNK_SIZE;
        int choffset = address % CHUNK_SIZE;

        if (choffset + DataWord.DATAWORD_BYTES >= CHUNK_SIZE) {
            byte[] data = value.getBytes();
            System.arraycopy(data, 0, this.chunks.get(nchunk), choffset, CHUNK_SIZE - choffset);
            System.arraycopy(data, CHUNK_SIZE - choffset, this.chunks.get(nchunk + 1), 0, DataWord.DATAWORD_BYTES - (CHUNK_SIZE - choffset));
        }
        else
            System.arraycopy(value.getBytes(), 0, this.chunks.get(nchunk), choffset, DataWord.DATAWORD_BYTES);
    }

    public DataWord getValue(int address) {
        if (address >= this.size)
            return DataWord.ZERO;

        int nchunk = address / CHUNK_SIZE;
        int choffset = address % CHUNK_SIZE;

        // TODO check chunk crossing
        if (address + DataWord.DATAWORD_BYTES <= this.size) {
            if (choffset + DataWord.DATAWORD_BYTES >= CHUNK_SIZE) {
                byte[] data = new byte[DataWord.DATAWORD_BYTES];
                System.arraycopy(this.chunks.get(nchunk), choffset, data, 0, CHUNK_SIZE - choffset);
                System.arraycopy(this.chunks.get(nchunk + 1), 0, data, CHUNK_SIZE - choffset, DataWord.DATAWORD_BYTES - (CHUNK_SIZE - choffset));

                return DataWord.fromBytes(data, 0, data.length);
            }
            else
                return DataWord.fromBytes(this.chunks.get(nchunk), choffset, DataWord.DATAWORD_BYTES);
        }

        // TODO check chunk crossing
        return DataWord.fromBytesToLeft(this.chunks.get(nchunk), choffset, this.size - address);
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

        // TODO Control chunk crossing
        System.arraycopy(bytes, offset, this.chunks.get(nchunk), choffset, Math.min(bytes.length - offset, length));
    }

    public byte[] getBytes(int address, int length) {
        byte[] bytes = new byte[length];

        if (this.size < address)
            return bytes;

        int nchunk = address / CHUNK_SIZE;
        int offset = address % CHUNK_SIZE;

        // TODO Control chunk crossing
        System.arraycopy(this.chunks.get(nchunk), offset, bytes, 0, Math.min(this.size - address, length));

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

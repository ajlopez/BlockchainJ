package com.ajlopez.blockchain.db;

import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.KeyValueStore;
import com.ajlopez.blockchain.utils.ByteArrayWrapper;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 21/10/2019.
 */
public class KeyFile {
    private final RandomAccessFile file;
    private final int keyLength;
    private Map<ByteArrayWrapper, ValueInfo> keys = new HashMap<>();

    public KeyFile(String name, int keyLength) throws IOException {
        this.file = new RandomAccessFile(name, "rw");
        this.keyLength = keyLength;

        int blockSize = this.getBlockSize();
        long nkeys = this.getNoKeys(blockSize);

        this.file.seek(0);

        for (long k = 0; k < nkeys; k++) {
            byte[] buffer = new byte[this.keyLength];

            this.file.read(buffer);

            long position = this.file.readLong();
            int length = this.file.readInt();

            this.keys.put(new ByteArrayWrapper(buffer), new ValueInfo(position, length));
        }
    }

    private int getBlockSize() {
        return this.keyLength + Long.BYTES + Integer.BYTES;
    }

    private long getNoKeys(int blockSize) throws IOException {
        return this.file.length() / blockSize;
    }

    public void writeKey(byte[] key, long position, int length) throws IOException {
        if (key == null || key.length != this.keyLength)
            throw new IllegalArgumentException("invalid key");

        int blockSize = this.getBlockSize();

        this.file.seek(this.getNoKeys(blockSize) * blockSize);
        this.file.write(key);
        this.file.writeLong(position);
        this.file.writeInt(length);

        this.keys.put(new ByteArrayWrapper(key), new ValueInfo(position, length));
    }

    public ValueInfo readKey(byte[] key) throws IOException {
        return this.keys.get(new ByteArrayWrapper(key));
    }

    public void close() throws IOException {
        this.file.close();
    }
}

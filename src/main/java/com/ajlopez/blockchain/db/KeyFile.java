package com.ajlopez.blockchain.db;

import com.ajlopez.blockchain.utils.ByteUtils;
import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by ajlopez on 21/10/2019.
 */
public class KeyFile {
    private final RandomAccessFile file;
    private final int keyLength;

    public KeyFile(String name, int keyLength) throws FileNotFoundException {
        this.file = new RandomAccessFile(name, "rw");
        this.keyLength = keyLength;
    }

    public void writeKey(byte[] key, long position, int length) throws IOException {
        if (key == null || key.length != this.keyLength)
            throw new IllegalArgumentException("invalid key");

        int blockSize = this.keyLength + Long.BYTES + Integer.BYTES;

        this.file.seek((this.file.length() / blockSize) * blockSize);
        this.file.write(key);
        this.file.writeLong(position);
        this.file.writeInt(length);
    }

    public ValueInfo readKey(byte[] key) throws IOException {
        this.file.seek(0L);

        byte[] buffer = new byte[this.keyLength];

        while (true) {
            int nbytes = this.file.read(buffer);

            if (nbytes != this.keyLength)
                return null;

            long position = this.file.readLong();
            int length = this.file.readInt();

            if (ByteUtils.equals(key, 0, buffer, 0, this.keyLength)) {
                return new ValueInfo(position, length);
            }
        }
    }
}

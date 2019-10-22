package com.ajlopez.blockchain.db;

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

        this.file.write(key);
        this.file.writeLong(position);
        this.file.writeInt(keyLength);
    }
}

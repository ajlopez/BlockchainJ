package com.ajlopez.blockchain.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by ajlopez on 20/10/2019.
 */
public class ValueFile {
    private final RandomAccessFile file;

    public ValueFile(String name) throws FileNotFoundException {
        this.file = new RandomAccessFile(name, "rw");
    }

    public void close() throws IOException {
        this.file.close();
    }

    public void writeValue(byte[] value, long position) throws IOException {
        this.file.seek(position);

        this.file.write(value);
    }

    public int readValue(long position, byte[] buffer) throws IOException {
        this.file.seek(position);

        return this.file.read(buffer);
    }
}

package com.ajlopez.blockchain.db;

import com.ajlopez.blockchain.store.KeyValueStore;

import java.io.IOException;

/**
 * Created by ajlopez on 20/10/2019.
 */
public class KeyValueDb implements KeyValueStore {
    private final ValueFile valueFile;
    private final KeyFile keyFile;

    public KeyValueDb(String name, int keyLength) throws IOException {
        this.valueFile = new ValueFile(name + ".values");
        this.keyFile = new KeyFile(name + ".keys", keyLength);
    }

    @Override
    public void setValue(byte[] key, byte[] value) throws IOException {
        long position = this.valueFile.writeValue(value);
        this.keyFile.writeKey(key, position, value.length);
    }

    @Override
    public byte[] getValue(byte[] key) throws IOException {
        ValueInfo valueInfo = this.keyFile.readKey(key);

        if (valueInfo == null)
            return null;

        byte[] buffer = new byte[valueInfo.length];

        // TODO Check read
        int read = this.valueFile.readValue(valueInfo.position, buffer);

        return buffer;
    }
}

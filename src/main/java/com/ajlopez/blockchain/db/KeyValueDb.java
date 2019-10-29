package com.ajlopez.blockchain.db;

import com.ajlopez.blockchain.store.KeyValueStore;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ajlopez on 20/10/2019.
 */
public class KeyValueDb implements KeyValueStore {
    private final ValueFile valueFile;
    private final KeyFile keyFile;

    public KeyValueDb(String name, int keyLength) throws IOException {
        this.valueFile = new ValueFile(name + ".values");
        this.keyFile = new KeyFile(name + ".values", keyLength);
    }

    @Override
    public void setValue(byte[] key, byte[] value) {

    }

    @Override
    public byte[] getValue(byte[] key) {
        return new byte[0];
    }
}

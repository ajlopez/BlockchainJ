package com.ajlopez.blockchain.store;

import java.io.IOException;

/**
 * Created by Angel on 19/04/2020.
 */
public class KeyValueCopierStore implements KeyValueStore {
    private final KeyValueStore originalStore;
    private final KeyValueStore newStore;

    public KeyValueCopierStore(KeyValueStore originalStore, KeyValueStore newStore) {
        this.originalStore = originalStore;
        this.newStore = newStore;
    }

    @Override
    public void setValue(byte[] key, byte[] value) throws IOException {
        this.newStore.setValue(key, value);
    }

    @Override
    public byte[] getValue(byte[] key) throws IOException {
        byte[] value = this.newStore.getValue(key);

        if (value != null)
            return value;

        value = this.originalStore.getValue(key);

        if (value == null)
            return null;

        this.newStore.setValue(key, value);

        return value;
    }
}

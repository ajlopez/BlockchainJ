package com.ajlopez.blockchain.store;

/**
 * Created by ajlopez on 06/01/2018.
 */
public interface KeyValueStore {
    void setValue(byte[] key, byte[] value);

    byte[] getValue(byte[] key);
}

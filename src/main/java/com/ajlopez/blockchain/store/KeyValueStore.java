package com.ajlopez.blockchain.store;

import java.io.IOException;

/**
 * Created by ajlopez on 06/01/2018.
 */
public interface KeyValueStore {
    void setValue(byte[] key, byte[] value) throws IOException;

    byte[] getValue(byte[] key) throws IOException;
}

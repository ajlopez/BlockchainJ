package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.types.Hash;

import java.security.Key;

/**
 * Created by ajlopez on 22/07/2020.
 */
public class KeyInformation {
    private final KeyValueStoreType keyValueStoreType;
    private final Hash hash;

    public KeyInformation(KeyValueStoreType keyValueStoreType, Hash hash) {
        this.keyValueStoreType = keyValueStoreType;
        this.hash = hash;
    }

    public KeyValueStoreType getKeyValueStoreType() { return this.keyValueStoreType; }

    public Hash getHash() { return this.hash; }
}

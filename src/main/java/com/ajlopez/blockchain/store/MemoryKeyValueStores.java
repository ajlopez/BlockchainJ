package com.ajlopez.blockchain.store;

import java.security.Key;

/**
 * Created by ajlopez on 04/05/2020.
 */
public class MemoryKeyValueStores implements KeyValueStores {
    private final KeyValueStore accountKeyValueStore = new HashMapStore();
    private final KeyValueStore storageKeyValueStore = new HashMapStore();
    private final KeyValueStore blockKeyValueStore = new HashMapStore();
    private final KeyValueStore blockInformationKeyValueStore = new HashMapStore();
    private final KeyValueStore codeKeyValueStore = new HashMapStore();

    @Override
    public KeyValueStore getAccountKeyValueStore() {
        return this.accountKeyValueStore;
    }

    @Override
    public KeyValueStore getStorageKeyValueStore() {
        return this.storageKeyValueStore;
    }

    @Override
    public KeyValueStore getBlockKeyValueStore() {
        return this.blockKeyValueStore;
    }

    @Override
    public KeyValueStore getCodeKeyValueStore() {
        return this.codeKeyValueStore;
    }

    @Override
    public KeyValueStore getBlockInformationKeyValueStore() {
        return this.blockInformationKeyValueStore;
    }
}

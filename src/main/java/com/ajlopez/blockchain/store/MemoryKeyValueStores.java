package com.ajlopez.blockchain.store;

/**
 * Created by ajlopez on 04/05/2020.
 */
public class MemoryKeyValueStores implements KeyValueStores {
    private final KeyValueStore accountKeyValueStore;
    private final KeyValueStore storageKeyValueStore;
    private final KeyValueStore blockKeyValueStore;
    private final KeyValueStore blockInformationKeyValueStore;
    private final KeyValueStore codeKeyValueStore;

    public MemoryKeyValueStores() {
        this.accountKeyValueStore = new HashMapStore();
        this.storageKeyValueStore = new HashMapStore();
        this.blockKeyValueStore = new HashMapStore();
        this.blockInformationKeyValueStore = new HashMapStore();
        this.codeKeyValueStore = new HashMapStore();
    }

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

package com.ajlopez.blockchain.store;


/**
 * Created by ajlopez on 04/05/2020.
 */
public interface KeyValueStores {
    KeyValueStore getAccountKeyValueStore();

    KeyValueStore getStorageKeyValueStore();

    KeyValueStore getBlockKeyValueStore();

    KeyValueStore getCodeKeyValueStore();

    KeyValueStore getBlockInformationKeyValueStore();
}

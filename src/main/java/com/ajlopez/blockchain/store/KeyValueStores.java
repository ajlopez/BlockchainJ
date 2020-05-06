package com.ajlopez.blockchain.store;


import java.io.IOException;

/**
 * Created by ajlopez on 04/05/2020.
 */
public interface KeyValueStores {
    KeyValueStore getAccountKeyValueStore();

    KeyValueStore getStorageKeyValueStore();

    KeyValueStore getBlockKeyValueStore();

    KeyValueStore getCodeKeyValueStore();

    KeyValueStore getBlockInformationKeyValueStore();

    default byte[] getValue(KeyValueStoreType keyValueStoreType, byte[] key) throws IOException {
        switch (keyValueStoreType) {
            case BLOCKS:
                return this.getBlockKeyValueStore().getValue(key);

            case CODES:
                return this.getCodeKeyValueStore().getValue(key);

            case ACCOUNTS:
                return this.getAccountKeyValueStore().getValue(key);

            case STORAGE:
                return this.getStorageKeyValueStore().getValue(key);

            case BLOCKS_INFORMATION:
                return this.getBlockInformationKeyValueStore().getValue(key);
        }

        return null;
    }
}

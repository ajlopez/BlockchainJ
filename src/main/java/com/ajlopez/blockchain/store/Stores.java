package com.ajlopez.blockchain.store;


import com.ajlopez.blockchain.bc.BlockHashStore;
import com.ajlopez.blockchain.bc.BlocksInformationStore;
import com.ajlopez.blockchain.vms.eth.TrieStorageProvider;

/**
 * Created by ajlopez on 01/01/2020.
 */
public interface Stores {
    TrieStore getAccountTrieStore();

    TrieStore getStorageTrieStore();

    CodeStore getCodeStore();

    BlockHashStore getBlockHashStore();

    BlocksInformationStore getBlocksInformationStore();

    default AccountStoreProvider getAccountStoreProvider() {
        return new AccountStoreProvider(this.getAccountTrieStore());
    }

    default TrieStorageProvider getTrieStorageProvider() {
        return new TrieStorageProvider(this.getStorageTrieStore());
    }
}

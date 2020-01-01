package com.ajlopez.blockchain.store;


/**
 * Created by ajlopez on 01/01/2020.
 */
public interface Stores {
    TrieStore getAccountTrieStore();

    TrieStore getStorageTrieStore();

    CodeStore getCodeStore();
}

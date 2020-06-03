package com.ajlopez.blockchain.store;

/**
 * Created by Angel on 01/01/2020.
 */
public class MemoryStores extends Stores {

    // TODO remove usage
    public MemoryStores() {
        super(new MemoryKeyValueStores());
    }
}


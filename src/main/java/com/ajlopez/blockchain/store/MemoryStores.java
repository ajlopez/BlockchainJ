package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.bc.BlockStore;
import com.ajlopez.blockchain.bc.BlocksInformationStore;

/**
 * Created by Angel on 01/01/2020.
 */
public class MemoryStores extends Stores {

    // TODO remove usage
    public MemoryStores() {
        super(new MemoryKeyValueStores());
    }
}


package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.bc.BlockStore;
import com.ajlopez.blockchain.bc.BlocksInformationStore;

/**
 * Created by Angel on 01/01/2020.
 */
public class MemoryStores implements Stores {
    private final TrieStore accountTrieStore;
    private final TrieStore storageTrieStore;
    private final CodeStore codeStore;
    private final BlockStore blockHashStore;
    private final BlocksInformationStore blocksInformationStore;

    public MemoryStores() {
        KeyValueStores keyValueStores = new MemoryKeyValueStores();

        this.accountTrieStore = new TrieStore(keyValueStores.getAccountKeyValueStore());
        this.storageTrieStore = new TrieStore(keyValueStores.getStorageKeyValueStore());
        this.codeStore = new CodeStore(keyValueStores.getCodeKeyValueStore());
        this.blockHashStore = new BlockStore(keyValueStores.getBlockKeyValueStore());
        this.blocksInformationStore = new BlocksInformationStore(keyValueStores.getBlockInformationKeyValueStore());
    }

    public TrieStore getAccountTrieStore() {
        return this.accountTrieStore;
    }

    public TrieStore getStorageTrieStore() {
        return this.storageTrieStore;
    }

    public CodeStore getCodeStore() {
        return this.codeStore;
    }

    public BlockStore getBlockHashStore() { return this.blockHashStore; }

    public BlocksInformationStore getBlocksInformationStore() { return this.blocksInformationStore; }
}


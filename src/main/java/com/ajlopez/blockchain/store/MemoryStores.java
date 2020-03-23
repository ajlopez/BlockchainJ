package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.bc.BlockHashStore;
import com.ajlopez.blockchain.bc.BlocksInformationStore;

/**
 * Created by Angel on 01/01/2020.
 */
public class MemoryStores implements Stores {
    private final TrieStore accountTrieStore = new TrieStore(new HashMapStore());
    private final TrieStore storageTrieStore = new TrieStore(new HashMapStore());
    private final CodeStore codeStore = new CodeStore(new HashMapStore());
    private final BlockHashStore blockHashStore = new BlockHashStore();
    private final BlocksInformationStore blocksInformationStore = new BlocksInformationStore(new HashMapStore());

    public TrieStore getAccountTrieStore() {
        return this.accountTrieStore;
    }

    public TrieStore getStorageTrieStore() {
        return this.storageTrieStore;
    }

    public CodeStore getCodeStore() {
        return this.codeStore;
    }

    public BlockHashStore getBlockHashStore() { return this.blockHashStore; }

    public BlocksInformationStore getBlocksInformationStore() { return this.blocksInformationStore; }
}


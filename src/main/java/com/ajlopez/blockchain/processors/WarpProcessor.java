package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.TrieStore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajlopez on 24/06/2019.
 */
public class WarpProcessor {
    private final TrieStore accountStore;
    private final Map<Hash, TrieCollector> accountCollectors = new HashMap<>();

    public WarpProcessor(TrieStore accountStore) {
        this.accountStore = accountStore;
    }

    public List<Hash> processBlock(Block block) {
        Hash hash = block.getStateRootHash();

        if (accountCollectors.containsKey(hash))
            return Collections.emptyList();

        if (this.accountStore.exists(hash))
            return Collections.emptyList();
        
        accountCollectors.put(hash, new TrieCollector(this.accountStore, hash));

        return Collections.singletonList(hash);
    }
}

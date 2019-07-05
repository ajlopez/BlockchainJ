package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.TrieStore;

import java.util.*;

/**
 * Created by ajlopez on 24/06/2019.
 */
public class WarpProcessor {
    private final TrieStore accountStore;
    private final Map<Hash, TrieCollector> accountCollectors = new HashMap<>();

    public WarpProcessor(TrieStore accountStore) {
        this.accountStore = accountStore;
    }

    public Set<Hash> processBlock(Block block) {
        Hash hash = block.getStateRootHash();

        if (accountCollectors.containsKey(hash))
            return accountCollectors.get(hash).getPendingHashes();

        if (this.accountStore.exists(hash))
            return Collections.emptySet();

        accountCollectors.put(hash, new TrieCollector(this.accountStore, hash));

        return Collections.singleton(hash);
    }

    public Set<Hash> processAccountNode(Hash topHash, byte[] nodeData) {
        if (!this.accountCollectors.containsKey(topHash))
            return Collections.emptySet();

        return this.accountCollectors.get(topHash).saveNode(nodeData);
    }

    public Set<Hash> getPendingAccountHashes(Hash topHash) {
        if (!this.accountCollectors.containsKey(topHash))
            return Collections.EMPTY_SET;

        return new HashSet<>(this.accountCollectors.get(topHash).getPendingHashes());
    }
}

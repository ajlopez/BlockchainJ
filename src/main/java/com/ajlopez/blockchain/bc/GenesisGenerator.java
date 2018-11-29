package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;

/**
 * Created by ajlopez on 29/11/2018.
 */
public class GenesisGenerator {
    private GenesisGenerator() {

    }

    public static Block generateGenesis() {
        return generateGenesis(new AccountStore(new Trie()));
    }

    public static Block generateGenesis(AccountStore accountStore) {
        return new Block(0, BlockHash.EMPTY_BLOCK_HASH, accountStore.getRootHash());
    }
}

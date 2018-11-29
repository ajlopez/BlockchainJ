package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.state.Trie;

/**
 * Created by ajlopez on 29/11/2018.
 */
public class GenesisGenerator {
    private GenesisGenerator() {

    }

    public static Block generateGenesis() {
        return new Block(0, BlockHash.EMPTY_BLOCK_HASH, Trie.EMPTY_TRIE_HASH);
    }
}

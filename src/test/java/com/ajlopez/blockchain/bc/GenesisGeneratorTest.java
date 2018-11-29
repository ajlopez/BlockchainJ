package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.state.Trie;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 29/11/2018.
 */
public class GenesisGeneratorTest {
    @Test
    public void generateGenesisWithNoInitialAccounts() {
        Block genesis = GenesisGenerator.generateGenesis();

        Assert.assertNotNull(genesis);
        Assert.assertEquals(0, genesis.getNumber());
        Assert.assertNotNull(genesis.getParentHash());
        Assert.assertEquals(BlockHash.EMPTY_BLOCK_HASH, genesis.getParentHash());
        Assert.assertNotNull(genesis.getStateRootHash());
        Assert.assertEquals(Trie.EMPTY_TRIE_HASH, genesis.getStateRootHash());
        Assert.assertNotNull(genesis.getTransactions());
        Assert.assertTrue(genesis.getTransactions().isEmpty());
    }
}

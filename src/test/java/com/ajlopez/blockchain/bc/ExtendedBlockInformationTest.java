package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.merkle.MerkleTree;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 11/07/2020.
 */
public class ExtendedBlockInformationTest {
    @Test
    public void simpleCreate() {
        Block block = new Block(1, FactoryHelper.createRandomBlockHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), Difficulty.ONE, 0, 0, null, 0);

        ExtendedBlockInformation extendedBlockInformation = new ExtendedBlockInformation(block, Difficulty.ONE.TEN);

        Assert.assertEquals(block.getNumber(), extendedBlockInformation.getBlockNumber());
        Assert.assertEquals(block.getHash(), extendedBlockInformation.getBlockHash());
        Assert.assertEquals(Difficulty.TEN, extendedBlockInformation.getTotalDifficulty());
        Assert.assertSame(block, extendedBlockInformation.getBlock());
    }
}

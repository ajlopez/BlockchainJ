package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 26/08/2017.
 */
public class BlockChainTest {
    @Test
    public void noBestBlock() {
        BlockChain blockChain = new BlockChain();

        Assert.assertNull(blockChain.getBestBlock());
        Assert.assertEquals(BlockChain.NO_BEST_BLOCK_NUMBER, blockChain.getBestBlockNumber());
    }

    @Test
    public void noBlockByHash() {
        BlockChain blockChain = new BlockChain();

        Assert.assertNull(blockChain.getBlockByHash(HashUtilsTest.generateRandomHash()));
    }

    @Test
    public void noBlockByNumber() {
        BlockChain blockChain = new BlockChain();

        Assert.assertNull(blockChain.getBlockByNumber(0));
        Assert.assertNull(blockChain.getBlockByNumber(1));
        Assert.assertNull(blockChain.getBlockByNumber(42));
    }

    @Test
    public void addFirstBlock() {
        BlockChain blockChain = new BlockChain();
        Block block = new Block(0, null);

        Assert.assertTrue(blockChain.connectBlock(block));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block.getHash(), blockChain.getBestBlock().getHash());

        Assert.assertEquals(block.getHash(), blockChain.getBlockByHash(block.getHash()).getHash());
        Assert.assertEquals(block.getHash(), blockChain.getBlockByNumber(block.getNumber()).getHash());

        Assert.assertEquals(0, blockChain.getBestBlockNumber());
    }

    @Test
    public void addSecondBlock() {
        BlockChain blockChain = new BlockChain();
        Block genesis = new Block(0, null);
        Block block = new Block(1, genesis.getHash());

        Assert.assertTrue(blockChain.connectBlock(genesis));
        Assert.assertTrue(blockChain.connectBlock(block));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block.getHash(), blockChain.getBestBlock().getHash());

        Assert.assertEquals(genesis.getHash(), blockChain.getBlockByHash(genesis.getHash()).getHash());
        Assert.assertEquals(genesis.getHash(), blockChain.getBlockByNumber(genesis.getNumber()).getHash());
        Assert.assertEquals(block.getHash(), blockChain.getBlockByHash(block.getHash()).getHash());
        Assert.assertEquals(block.getHash(), blockChain.getBlockByNumber(block.getNumber()).getHash());
    }

    @Test
    public void addSecondBlockTwice() {
        BlockChain blockChain = new BlockChain();
        Block genesis = new Block(0, null);
        Block block = new Block(1, genesis.getHash());

        Assert.assertTrue(blockChain.connectBlock(genesis));
        Assert.assertTrue(blockChain.connectBlock(block));
        Assert.assertTrue(blockChain.connectBlock(block));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block.getHash(), blockChain.getBestBlock().getHash());
    }

    @Test
    public void addFirstBlockTwice() {
        BlockChain blockChain = new BlockChain();
        Block genesis = new Block(0, null);
        Block block = new Block(1, genesis.getHash());

        Assert.assertTrue(blockChain.connectBlock(genesis));
        Assert.assertTrue(blockChain.connectBlock(block));
        Assert.assertTrue(blockChain.connectBlock(genesis));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block.getHash(), blockChain.getBestBlock().getHash());
    }

    @Test
    public void rejectBlockIfNotChild() {
        BlockChain blockChain = new BlockChain();
        Block genesis = new Block(0, null);
        Block block = new Block(1, HashUtilsTest.generateRandomHash());

        Assert.assertTrue(blockChain.connectBlock(genesis));
        Assert.assertFalse(blockChain.connectBlock(block));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(genesis.getHash(), blockChain.getBestBlock().getHash());
        Assert.assertNull(blockChain.getBlockByNumber(block.getNumber()));
    }

    @Test
    public void switchToABetterFork() {
        BlockChain blockChain = new BlockChain();
        Block genesis = new Block(0, null);
        Block block1 = new Block(1, genesis.getHash());
        Block block1b = new Block(1, genesis.getHash());
        Block block2b = new Block(2, block1b.getHash());

        Assert.assertTrue(blockChain.connectBlock(genesis));
        Assert.assertTrue(blockChain.connectBlock(block1));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block1.getHash(), blockChain.getBestBlock().getHash());

        Assert.assertTrue(blockChain.connectBlock(block1b));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block1.getHash(), blockChain.getBestBlock().getHash());

        Assert.assertTrue(blockChain.connectBlock(block2b));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block2b.getHash(), blockChain.getBestBlock().getHash());

        Assert.assertEquals(block1.getHash(), blockChain.getBlockByHash(block1.getHash()).getHash());
        Assert.assertEquals(block1b.getHash(), blockChain.getBlockByHash(block1b.getHash()).getHash());
        Assert.assertEquals(block2b.getHash(), blockChain.getBlockByHash(block2b.getHash()).getHash());

        Assert.assertEquals(block1b.getHash(), blockChain.getBlockByNumber(block1b.getNumber()).getHash());
        Assert.assertEquals(block2b.getHash(), blockChain.getBlockByNumber(block2b.getNumber()).getHash());
    }
}

package com.ajlopez.blockchain.core;

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
    }

    @Test
    public void addFirstBlock() {
        BlockChain blockChain = new BlockChain();
        Block block = new Block(0, null);

        blockChain.connectBlock(block);

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block.getHash(), blockChain.getBestBlock().getHash());
    }

    @Test
    public void addSecondBlock() {
        BlockChain blockChain = new BlockChain();
        Block genesis = new Block(0, null);
        Block block = new Block(1, genesis.getHash());

        blockChain.connectBlock(genesis);
        blockChain.connectBlock(block);

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block.getHash(), blockChain.getBestBlock().getHash());
    }

    @Test
    public void addSecondBlockTwice() {
        BlockChain blockChain = new BlockChain();
        Block genesis = new Block(0, null);
        Block block = new Block(1, genesis.getHash());

        blockChain.connectBlock(genesis);
        blockChain.connectBlock(block);
        blockChain.connectBlock(block);

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block.getHash(), blockChain.getBestBlock().getHash());
    }

    @Test
    public void addFirstBlockTwice() {
        BlockChain blockChain = new BlockChain();
        Block genesis = new Block(0, null);
        Block block = new Block(1, genesis.getHash());

        blockChain.connectBlock(genesis);
        blockChain.connectBlock(block);
        blockChain.connectBlock(genesis);

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block.getHash(), blockChain.getBestBlock().getHash());
    }

    @Test
    public void rejectBlockIfNotChild() {
        BlockChain blockChain = new BlockChain();
        Block genesis = new Block(0, null);
        Block block = new Block(1, new Hash());

        blockChain.connectBlock(genesis);
        blockChain.connectBlock(block);

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(genesis.getHash(), blockChain.getBestBlock().getHash());
    }

    @Test
    public void switchToABetterFork() {
        BlockChain blockChain = new BlockChain();
        Block genesis = new Block(0, null);
        Block block1 = new Block(1, genesis.getHash());
        Block block1b = new Block(1, genesis.getHash());
        Block block2b = new Block(2, block1b.getHash());

        blockChain.connectBlock(genesis);
        blockChain.connectBlock(block1);

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block1.getHash(), blockChain.getBestBlock().getHash());

        blockChain.connectBlock(block1b);

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block1.getHash(), blockChain.getBestBlock().getHash());

        blockChain.connectBlock(block2b);

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block2b.getHash(), blockChain.getBestBlock().getHash());
    }

    @Test
    public void switchToABetterForkUsingOrphan() {
        BlockChain blockChain = new BlockChain();
        Block genesis = new Block(0, null);
        Block block1 = new Block(1, genesis.getHash());
        Block block2 = new Block(2, block1.getHash());
        Block block3 = new Block(3, block2.getHash());

        blockChain.connectBlock(genesis);
        blockChain.connectBlock(block1);

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertNotNull(blockChain.getBestBlock().getHash());
        Assert.assertEquals(block1.getHash(), blockChain.getBestBlock().getHash());

        blockChain.connectBlock(block3);

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block1.getNumber(), blockChain.getBestBlock().getNumber());
        Assert.assertEquals(block1.getHash(), blockChain.getBestBlock().getHash());

        blockChain.connectBlock(block2);

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block3.getHash(), blockChain.getBestBlock().getHash());
    }
}

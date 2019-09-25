package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
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

        Assert.assertNull(blockChain.getBlockByHash(FactoryHelper.createRandomHash()));
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
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

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
        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);
        Block block = new Block(1, genesis.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

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
        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block = new Block(1, genesis.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertTrue(blockChain.connectBlock(genesis));
        Assert.assertTrue(blockChain.connectBlock(block));
        Assert.assertTrue(blockChain.connectBlock(block));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block.getHash(), blockChain.getBestBlock().getHash());
    }

    @Test
    public void addFirstBlockTwice() {
        BlockChain blockChain = new BlockChain();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);
        Block block = new Block(1, genesis.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertTrue(blockChain.connectBlock(genesis));
        Assert.assertTrue(blockChain.connectBlock(block));
        Assert.assertTrue(blockChain.connectBlock(genesis));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block.getHash(), blockChain.getBestBlock().getHash());
    }

    @Test
    public void rejectBlockIfNotChild() {
        BlockChain blockChain = new BlockChain();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);
        Block block = new Block(1, FactoryHelper.createRandomBlockHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertTrue(blockChain.connectBlock(genesis));
        Assert.assertFalse(blockChain.connectBlock(block));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(genesis.getHash(), blockChain.getBestBlock().getHash());
        Assert.assertNull(blockChain.getBlockByNumber(block.getNumber()));
    }

    @Test
    public void switchToABetterFork() {
        BlockChain blockChain = new BlockChain();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);
        Block block1 = new Block(1, genesis.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block1b = new Block(1, genesis.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block2b = new Block(2, block1b.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

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

package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.test.BlockConsumer;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.hamcrest.Factory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 17/12/2017.
 */
public class BlockProcessorTest {
    @Test
    public void noBestBlock() {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Assert.assertNull(processor.getBestBlock());
    }

    @Test
    public void noBlockByHash() {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Assert.assertNull(processor.getBlockByHash(HashUtilsTest.generateRandomHash()));
    }

    @Test
    public void noBlockByNumber() {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Assert.assertNull(processor.getBlockByNumber(1));
    }

    @Test
    public void notChainedBlock() {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Assert.assertFalse(processor.isChainedBlock(HashUtilsTest.generateRandomHash()));
    }

    @Test
    public void notOrphanBlock() {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Assert.assertFalse(processor.isOrphanBlock(HashUtilsTest.generateRandomHash()));
    }

    @Test
    public void unknownBlock() {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Assert.assertFalse(processor.isKnownBlock(HashUtilsTest.generateRandomHash()));
    }

    @Test
    public void addFirstBlock() {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Block block = new Block(0, null);

        processor.processBlock(block);

        Assert.assertNotNull(processor.getBestBlock());
        Assert.assertEquals(block.getHash(), processor.getBestBlock().getHash());

        Assert.assertEquals(block.getHash(), processor.getBlockByHash(block.getHash()).getHash());
        Assert.assertEquals(block.getHash(), processor.getBlockByNumber(block.getNumber()).getHash());
    }

    @Test
    public void switchToABetterForkUsingOrphan() {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Block genesis = new Block(0, null);
        Block block1 = new Block(1, genesis.getHash());
        Block block2 = new Block(2, block1.getHash());
        Block block3 = new Block(3, block2.getHash());

        processor.processBlock(genesis);
        processor.processBlock(block1);

        Assert.assertNotNull(processor.getBestBlock());
        Assert.assertNotNull(processor.getBestBlock().getHash());
        Assert.assertEquals(block1.getHash(), processor.getBestBlock().getHash());

        processor.processBlock(block3);

        Assert.assertNotNull(processor.getBestBlock());
        Assert.assertEquals(block1.getNumber(), processor.getBestBlock().getNumber());
        Assert.assertEquals(block1.getHash(), processor.getBestBlock().getHash());

        processor.processBlock(block2);

        Assert.assertNotNull(processor.getBestBlock());
        Assert.assertEquals(block3.getHash(), processor.getBestBlock().getHash());

        Assert.assertEquals(genesis.getHash(), processor.getBlockByHash(genesis.getHash()).getHash());
        Assert.assertEquals(block1.getHash(), processor.getBlockByHash(block1.getHash()).getHash());
        Assert.assertEquals(block2.getHash(), processor.getBlockByHash(block2.getHash()).getHash());
        Assert.assertEquals(block3.getHash(), processor.getBlockByHash(block3.getHash()).getHash());
    }

    @Test
    public void switchToABetterForkUsingOrphanAndEmitNewBestBlock() {
        BlockProcessor processor = FactoryHelper.createBlockProcessor();

        Block genesis = new Block(0, null);
        Block block1 = new Block(1, genesis.getHash());
        Block block2 = new Block(2, block1.getHash());
        Block block3 = new Block(3, block2.getHash());

        processor.processBlock(genesis);
        processor.processBlock(block1);

        Assert.assertNotNull(processor.getBestBlock());
        Assert.assertNotNull(processor.getBestBlock().getHash());
        Assert.assertEquals(block1.getHash(), processor.getBestBlock().getHash());

        processor.processBlock(block3);

        Assert.assertNotNull(processor.getBestBlock());
        Assert.assertEquals(block1.getNumber(), processor.getBestBlock().getNumber());
        Assert.assertEquals(block1.getHash(), processor.getBestBlock().getHash());

        BlockConsumer consumer = new BlockConsumer();

        processor.onNewBestBlock(consumer);

        processor.processBlock(block2);

        Assert.assertNotNull(processor.getBestBlock());
        Assert.assertEquals(block3.getHash(), processor.getBestBlock().getHash());

        Assert.assertEquals(genesis.getHash(), processor.getBlockByHash(genesis.getHash()).getHash());
        Assert.assertEquals(block1.getHash(), processor.getBlockByHash(block1.getHash()).getHash());
        Assert.assertEquals(block2.getHash(), processor.getBlockByHash(block2.getHash()).getHash());
        Assert.assertEquals(block3.getHash(), processor.getBlockByHash(block3.getHash()).getHash());

        Assert.assertNotNull(consumer.getBlock());
        Assert.assertEquals(block3.getHash(), consumer.getBlock().getHash());
    }
}

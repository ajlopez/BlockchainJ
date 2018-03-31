package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by ajlopez on 24/01/2018.
 */
public class MinerProcessorTest {
    @Test
    public void mineBlockWithNoTransactions() {
        TransactionPool txpool = new TransactionPool();
        MinerProcessor processor = new MinerProcessor(null, txpool);

        BlockHash hash = new BlockHash(HashUtilsTest.generateRandomHash());
        Block parent = new Block(1L, hash);

        Block block = processor.mineBlock(parent, txpool);

        Assert.assertNotNull(block);
        Assert.assertEquals(2, block.getNumber());
        Assert.assertEquals(parent.getHash(), block.getParentHash());

        List<Transaction> txs = block.getTransactions();

        Assert.assertNotNull(txs);
        Assert.assertTrue(txs.isEmpty());
    }

    @Test
    public void mineBlockWithOneTransaction() {
        BlockHash hash = new BlockHash(HashUtilsTest.generateRandomHash());
        Block parent = new Block(1L, hash);

        Transaction tx = FactoryHelper.createTransaction(100);

        TransactionPool txpool = new TransactionPool();
        txpool.addTransaction(tx);

        MinerProcessor processor = new MinerProcessor(null, txpool);

        Block block = processor.mineBlock(parent, txpool);

        Assert.assertNotNull(block);
        Assert.assertEquals(2, block.getNumber());
        Assert.assertEquals(parent.getHash(), block.getParentHash());

        List<Transaction> txs = block.getTransactions();

        Assert.assertNotNull(txs);
        Assert.assertFalse(txs.isEmpty());
        Assert.assertEquals(1, txs.size());
        Assert.assertSame(tx, txs.get(0));

        Assert.assertFalse(txpool.getTransactions().isEmpty());
    }

    @Test
    public void processBlockWithOneTransaction() {
        Block genesis = new Block(0, null);

        Transaction tx = FactoryHelper.createTransaction(100);

        TransactionPool txpool = new TransactionPool();
        txpool.addTransaction(tx);

        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        blockProcessor.processBlock(genesis);

        MinerProcessor processor = new MinerProcessor(blockProcessor, txpool);

        processor.process();

        Block block = blockProcessor.getBestBlock();

        Assert.assertNotNull(block);
        Assert.assertEquals(1, block.getNumber());
        Assert.assertEquals(genesis.getHash(), block.getParentHash());

        List<Transaction> txs = block.getTransactions();

        Assert.assertNotNull(txs);
        Assert.assertFalse(txs.isEmpty());
        Assert.assertEquals(1, txs.size());
        Assert.assertSame(tx, txs.get(0));

        Assert.assertFalse(txpool.getTransactions().isEmpty());
    }
}

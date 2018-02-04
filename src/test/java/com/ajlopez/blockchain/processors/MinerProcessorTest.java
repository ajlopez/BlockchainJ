package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
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
        MinerProcessor processor = new MinerProcessor();

        Hash hash = HashUtilsTest.generateRandomHash();
        Block parent = new Block(1L, hash);

        TransactionPool txpool = new TransactionPool();

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
        MinerProcessor processor = new MinerProcessor();

        Hash hash = HashUtilsTest.generateRandomHash();
        Block parent = new Block(1L, hash);

        Transaction tx = FactoryHelper.createTransaction(100);

        TransactionPool txpool = new TransactionPool();
        txpool.addTransaction(tx);

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
}

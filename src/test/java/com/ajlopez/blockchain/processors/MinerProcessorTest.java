package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 24/01/2018.
 */
public class MinerProcessorTest {
    @Test
    public void mineBlockWithNoTransactions() {
        TransactionPool transactionPool = new TransactionPool();
        MinerProcessor processor = new MinerProcessor(null, transactionPool);

        BlockHash hash = new BlockHash(HashUtilsTest.generateRandomHash());
        Block parent = new Block(1L, hash, Hash.emptyHash);

        Block block = processor.mineBlock(parent, transactionPool);

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
        Block parent = new Block(1L, hash, Hash.emptyHash);

        Transaction tx = FactoryHelper.createTransaction(100);

        TransactionPool transactionPool = new TransactionPool();
        transactionPool.addTransaction(tx);

        MinerProcessor processor = new MinerProcessor(null, transactionPool);

        Block block = processor.mineBlock(parent, transactionPool);

        Assert.assertNotNull(block);
        Assert.assertEquals(2, block.getNumber());
        Assert.assertEquals(parent.getHash(), block.getParentHash());

        List<Transaction> txs = block.getTransactions();

        Assert.assertNotNull(txs);
        Assert.assertFalse(txs.isEmpty());
        Assert.assertEquals(1, txs.size());
        Assert.assertSame(tx, txs.get(0));

        Assert.assertFalse(transactionPool.getTransactions().isEmpty());
    }

    @Test
    public void processBlockWithOneTransaction() {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        Transaction tx = FactoryHelper.createTransaction(100);

        TransactionPool transactionPool = new TransactionPool();
        transactionPool.addTransaction(tx);

        MinerProcessor processor = new MinerProcessor(blockChain, transactionPool);

        Block block = processor.process();

        Assert.assertNotNull(block);
        Assert.assertEquals(1, block.getNumber());
        Assert.assertEquals(blockChain.getBlockByNumber(0).getHash(), block.getParentHash());

        List<Transaction> txs = block.getTransactions();

        Assert.assertNotNull(txs);
        Assert.assertFalse(txs.isEmpty());
        Assert.assertEquals(1, txs.size());
        Assert.assertSame(tx, txs.get(0));

        Assert.assertFalse(transactionPool.getTransactions().isEmpty());
    }

    @Test
    public void mineOneBlockUsingStartAndStop() throws InterruptedException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        Transaction tx = FactoryHelper.createTransaction(100);

        TransactionPool transactionPool = new TransactionPool();
        transactionPool.addTransaction(tx);

        MinerProcessor processor = new MinerProcessor(blockChain, transactionPool);

        Semaphore sem = new Semaphore(0, true);

        List<Block> minedBlocks = new ArrayList<>();

        processor.onMinedBlock((blk) -> {
            minedBlocks.add(blk);
            sem.release();
        });

        processor.start();

        sem.acquire();

        processor.stop();

        Assert.assertFalse(minedBlocks.isEmpty());
        Assert.assertEquals(1, minedBlocks.size());

        Block block = minedBlocks.get(0);

        Assert.assertNotNull(block);
        Assert.assertEquals(1, block.getNumber());
        Assert.assertEquals(blockChain.getBlockByNumber(0).getHash(), block.getParentHash());

        List<Transaction> txs = block.getTransactions();

        Assert.assertNotNull(txs);
        Assert.assertFalse(txs.isEmpty());
        Assert.assertEquals(1, txs.size());
        Assert.assertSame(tx, txs.get(0));

        Assert.assertFalse(transactionPool.getTransactions().isEmpty());
    }

    @Test
    public void mineTwoBlocksUsingStartAndStop() throws InterruptedException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        TransactionPool transactionPool = new TransactionPool();

        MinerProcessor processor = new MinerProcessor(blockChain, transactionPool);

        Semaphore sem = new Semaphore(0, true);

        List<Block> minedBlocks = new ArrayList<>();

        processor.onMinedBlock((block) -> {
            minedBlocks.add(block);
            sem.release();
        });

        processor.start();

        sem.acquire();
        sem.acquire();

        processor.stop();

        Assert.assertFalse(minedBlocks.isEmpty());
        Assert.assertEquals(2, minedBlocks.size());

        Block block1 = minedBlocks.get(0);

        Assert.assertNotNull(block1);
        Assert.assertEquals(1, block1.getNumber());

        Block block2 = minedBlocks.get(1);

        Assert.assertNotNull(block2);
        Assert.assertEquals(1, block1.getNumber());

        Assert.assertTrue(transactionPool.getTransactions().isEmpty());
    }
}

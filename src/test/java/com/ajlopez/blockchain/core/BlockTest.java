package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createWithNumberAndParentHash() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1L, hash, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertEquals(1L, block.getNumber());
        Assert.assertEquals(hash, block.getParentHash());
        Assert.assertNotNull(block.getHash());
    }

    @Test
    public void noTransactions() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1L, hash, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        List<Transaction> transactions = block.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertTrue(transactions.isEmpty());
    }

    @Test
    public void cannotAddTransactionToEmptyTransactionList() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1L, hash, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        List<Transaction> transactions = block.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertTrue(transactions.isEmpty());

        exception.expect(UnsupportedOperationException.class);
        transactions.add(FactoryHelper.createTransaction(42));
    }

    @Test
    public void blockWithDifferentParentHashesHaveDifferentHashes() {
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block1 = new Block(1L, FactoryHelper.createRandomBlockHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block2 = new Block(1L, FactoryHelper.createRandomBlockHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertNotEquals(block1.getHash(), block2.getHash());
    }

    @Test
    public void blockWithDifferentNumbersHaveDifferentHashes() {
        BlockHash parentHash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block1 = new Block(1L, parentHash, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block2 = new Block(2L, parentHash, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertNotEquals(block1.getHash(), block2.getHash());
    }

    @Test
    public void blockWithDifferentTransactionsHaveDifferentHashes() {
        Transaction tx1 = FactoryHelper.createTransaction(42);
        Transaction tx2 = FactoryHelper.createTransaction(144);

        List<Transaction> txs1 = new ArrayList<>();
        List<Transaction> txs2 = new ArrayList<>();

        txs1.add(tx1);
        txs2.add(tx2);

        BlockHash hash = FactoryHelper.createRandomBlockHash();

        Address coinbase = FactoryHelper.createRandomAddress();

        Block block1 = new Block(1L, hash, txs1, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block2 = new Block(1L, hash, txs2, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertNotEquals(block1.getHash(), block2.getHash());
    }

    @Test
    public void withOneTransaction() {
        Transaction tx = FactoryHelper.createTransaction(42);

        List<Transaction> txs = new ArrayList<>();
        txs.add(tx);

        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1L, hash, txs, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        List<Transaction> transactions = block.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertFalse(transactions.isEmpty());
        Assert.assertEquals(1, transactions.size());
        Assert.assertEquals(tx.getHash(), transactions.get(0).getHash());
    }

    @Test
    public void withTwoTransactionInmmutable() {
        Transaction tx1 = FactoryHelper.createTransaction(42);
        Transaction tx2 = FactoryHelper.createTransaction(100);

        List<Transaction> txs = new ArrayList<>();
        txs.add(tx1);
        txs.add(tx2);

        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1L, hash, txs, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        List<Transaction> transactions = block.getTransactions();

        exception.expect(UnsupportedOperationException.class);
        transactions.add(FactoryHelper.createTransaction(1));
    }
}

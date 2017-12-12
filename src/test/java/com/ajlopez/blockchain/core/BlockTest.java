package com.ajlopez.blockchain.core;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockTest {
    @Test
    public void createWithNumberAndParentHash() {
        Hash hash = generateHash();
        Block block = new Block(1L, hash);

        Assert.assertEquals(1L, block.getNumber());
        Assert.assertEquals(hash, block.getParentHash());
        Assert.assertNotNull(block.getHash());
    }

    @Test
    public void noTransactions() {
        Hash hash = generateHash();
        Block block = new Block(1L, hash);

        List<Transaction> transactions = block.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertTrue(transactions.isEmpty());
    }

    @Test
    public void blockWithDifferentParentHashesHaveDifferentHashes() {
        Block block1 = new Block(1L, generateHash());
        Block block2 = new Block(1L, generateHash());

        Assert.assertNotEquals(block1.getHash(), block2.getHash());
    }

    @Test
    public void blockWithDifferentNumbersHaveDifferentHashes() {
        Hash parentHash = generateHash();
        Block block1 = new Block(1L, parentHash);
        Block block2 = new Block(2L, parentHash);

        Assert.assertNotEquals(block1.getHash(), block2.getHash());
    }

    @Test
    public void blockWithDifferentTransactionsHaveDifferentHashes() {
        Address sender = new Address();
        Address receiver = new Address();

        Transaction tx1 = new Transaction(sender, receiver, BigInteger.ONE, 0);
        Transaction tx2 = new Transaction(sender, receiver, BigInteger.TEN, 1);

        List<Transaction> txs1 = new ArrayList<>();
        List<Transaction> txs2 = new ArrayList<>();

        txs1.add(tx1);
        txs2.add(tx2);

        Hash hash = generateHash();

        Block block1 = new Block(1L, hash, txs1);
        Block block2 = new Block(1L, hash, txs2);

        Assert.assertNotEquals(block1.getHash(), block2.getHash());
    }

    @Test
    public void withOneTransaction() {
        Address sender = new Address();
        Address receiver = new Address();
        BigInteger value = BigInteger.ONE;

        Transaction tx = new Transaction(sender, receiver, value, 0);

        List<Transaction> txs = new ArrayList<>();
        txs.add(tx);

        Hash hash = generateHash();
        Block block = new Block(1L, hash, txs);

        List<Transaction> transactions = block.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertFalse(transactions.isEmpty());
        Assert.assertEquals(1, transactions.size());
        Assert.assertEquals(tx.getHash(), transactions.get(0).getHash());
    }

    private static Hash generateHash() {
        byte[] bytes = new byte[32];
        Random random = new Random();
        random.nextBytes(bytes);
        return new Hash(bytes);
    }
}

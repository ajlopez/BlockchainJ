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
    public void withOneTransaction() {
        Address sender = new Address();
        Address receiver = new Address();
        BigInteger value = BigInteger.ONE;

        Transaction tx = new Transaction(sender, receiver, value);

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

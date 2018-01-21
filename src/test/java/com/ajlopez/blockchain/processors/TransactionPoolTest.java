package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Address;
import com.ajlopez.blockchain.core.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

/**
 * Created by ajlopez on 21/01/2018.
 */
public class TransactionPoolTest {
    @Test
    public void noTransactions() {
        TransactionPool pool = new TransactionPool();

        List<Transaction> result = pool.getTransactions();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void addTransaction() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = createTransaction(100);

        pool.addTransaction(transaction);

        List<Transaction> result = pool.getTransactions();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(transaction, result.get(0));
    }

    @Test
    public void addTransactionTwice() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = createTransaction(100);

        pool.addTransaction(transaction);
        pool.addTransaction(transaction);

        List<Transaction> result = pool.getTransactions();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(transaction, result.get(0));
    }

    @Test
    public void addTransactionGetListAddTransaction() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction1 = createTransaction(100);
        Transaction transaction2 = createTransaction(200);

        pool.addTransaction(transaction1);
        List<Transaction> result = pool.getTransactions();
        pool.addTransaction(transaction2);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(transaction1, result.get(0));
    }

    private static Transaction createTransaction(int value) {
        Address sender = new Address();
        Address receiver = new Address();
        BigInteger bivalue = BigInteger.valueOf(value);
        Random random = new Random();
        int nonce = Math.abs(random.nextInt());

        return new Transaction(sender, receiver, bivalue, nonce);
    }
}

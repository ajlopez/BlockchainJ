package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

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
        Transaction transaction = FactoryHelper.createTransaction(100);

        pool.addTransaction(transaction);

        List<Transaction> result = pool.getTransactions();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(transaction, result.get(0));
    }

    @Test
    public void addAndRemoveTransaction() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100);

        pool.addTransaction(transaction);
        pool.removeTransaction(transaction);

        List<Transaction> result = pool.getTransactions();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void addAndRemoveTwiceATransaction() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100);

        pool.addTransaction(transaction);
        pool.removeTransaction(transaction);
        pool.removeTransaction(transaction);

        List<Transaction> result = pool.getTransactions();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void removeUnknownTransaction() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100);

        pool.removeTransaction(transaction);

        List<Transaction> result = pool.getTransactions();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void addTransactionTwice() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100);

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
        Transaction transaction1 = FactoryHelper.createTransaction(100);
        Transaction transaction2 = FactoryHelper.createTransaction(200);

        pool.addTransaction(transaction1);
        List<Transaction> result = pool.getTransactions();
        pool.addTransaction(transaction2);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(transaction1, result.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullTransaction() {
        TransactionPool pool = new TransactionPool();

        pool.addTransaction(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTransaction() {
        TransactionPool pool = new TransactionPool();

        pool.removeTransaction(null);
    }
}

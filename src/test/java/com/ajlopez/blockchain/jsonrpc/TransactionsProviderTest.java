package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.processors.TransactionPool;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 20/08/2019.
 */
public class TransactionsProviderTest {
    @Test
    public void getUnknownTransactionAsNull() {
        TransactionPool transactionPool = new TransactionPool();
        Hash hash = FactoryHelper.createRandomHash();
        String txid = hash.toString();

        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);

        Assert.assertNull(transactionsProvider.getTransaction(txid));
    }

    @Test
    public void getTransaction() {
        TransactionPool transactionPool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(1000);
        transactionPool.addTransaction(transaction);

        Hash hash = transaction.getHash();
        String txid = hash.toString();

        TransactionsProvider transactionsProvider = new TransactionsProvider(transactionPool);

        Transaction result = transactionsProvider.getTransaction(txid);

        Assert.assertNotNull(result);
        Assert.assertEquals(transaction, result);
    }
}

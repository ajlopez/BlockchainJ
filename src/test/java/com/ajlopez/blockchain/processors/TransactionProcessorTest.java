package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.test.utils.TransactionHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class TransactionProcessorTest {
    @Test
    public void processTransaction() {
        TransactionPool pool = new TransactionPool();
        TransactionProcessor processor = new TransactionProcessor(pool);

        Transaction transaction = TransactionHelper.createTransaction(100);

        processor.processTransaction(transaction);

        Assert.assertTrue(pool.containsTransaction(transaction));
    }
}

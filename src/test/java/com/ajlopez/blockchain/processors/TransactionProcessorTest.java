package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class TransactionProcessorTest {
    @Test
    public void processTransaction() {
        TransactionPool pool = new TransactionPool();
        TransactionProcessor processor = new TransactionProcessor(pool);

        Transaction transaction = FactoryHelper.createTransaction(100);

        processor.processTransaction(transaction);

        Assert.assertTrue(pool.getTransactions().contains(transaction));
    }

    @Test
    public void rejectInvalidTransaction() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = Address.ADDRESS_RICH_TRANSACTION;
        Coin value = Coin.ONE;
        byte []data = FactoryHelper.createRandomBytes(42);

        Transaction transaction = new Transaction(sender, receiver, value, 42, data, 6000000, Coin.ZERO);

        TransactionPool pool = new TransactionPool();
        TransactionProcessor processor = new TransactionProcessor(pool);

        List<Transaction> processed = processor.processTransaction(transaction);

        Assert.assertNotNull(processed);
        Assert.assertTrue(processed.isEmpty());

        Assert.assertFalse(pool.getTransactions().contains(transaction));
    }
}

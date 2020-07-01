package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 01/07/2020.
 */
public class TransactionValidatorTest {
    @Test
    public void normalTransactionIsValid() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = FactoryHelper.createRandomAddress();
        Coin value = Coin.ONE;

        Transaction transaction = new Transaction(sender, receiver, value, 42, null, 6000000, Coin.ZERO);

        TransactionValidator transactionValidator = new TransactionValidator();

        Assert.assertTrue(transactionValidator.isValid(transaction));
    }

    @Test
    public void richTransactionWithZeroValueAndNonEmptyDataIsValid() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = Address.ADDRESS_RICH_TRANSACTION;
        Coin value = Coin.ZERO;
        byte []data = FactoryHelper.createRandomBytes(42);

        Transaction transaction = new Transaction(sender, receiver, value, 42, data, 6000000, Coin.ZERO);

        TransactionValidator transactionValidator = new TransactionValidator();

        Assert.assertTrue(transactionValidator.isValid(transaction));
    }

    @Test
    public void richTransactionWithNonZeroValueIsInvalid() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = Address.ADDRESS_RICH_TRANSACTION;
        Coin value = Coin.ONE;
        byte []data = FactoryHelper.createRandomBytes(42);

        Transaction transaction = new Transaction(sender, receiver, value, 42, data, 6000000, Coin.ZERO);

        TransactionValidator transactionValidator = new TransactionValidator();

        Assert.assertFalse(transactionValidator.isValid(transaction));
    }

    @Test
    public void richTransactionWithNullDataIsInvalid() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = Address.ADDRESS_RICH_TRANSACTION;
        Coin value = Coin.ZERO;

        Transaction transaction = new Transaction(sender, receiver, value, 42, null, 6000000, Coin.ZERO);

        TransactionValidator transactionValidator = new TransactionValidator();

        Assert.assertFalse(transactionValidator.isValid(transaction));
    }

    @Test
    public void richTransactionWithEmptyDataIsInvalid() {
        Address sender = FactoryHelper.createRandomAddress();
        Address receiver = Address.ADDRESS_RICH_TRANSACTION;
        Coin value = Coin.ZERO;
        byte []data = new byte[0];

        Transaction transaction = new Transaction(sender, receiver, value, 42, data, 6000000, Coin.ZERO);

        TransactionValidator transactionValidator = new TransactionValidator();

        Assert.assertFalse(transactionValidator.isValid(transaction));
    }
}
